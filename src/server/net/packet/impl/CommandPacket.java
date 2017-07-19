package server.net.packet.impl;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.net.buffer.PacketBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.Teleport;
import server.world.entity.combat.Hit;
import server.world.entity.combat.Hit.DamageType;
import server.world.entity.combat.magic.TeleportSpell;
import server.world.entity.mob.Mob;
import server.world.entity.player.Player;
import server.world.entity.player.container.InventoryContainer;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.map.MapRegion;
import server.world.map.MapRegionTile;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * A custom client packet that is sent when the player types a '::' command.
 * 
 * @author lare96
 */
public class CommandPacket implements ClientPacketBuilder {

    @Override
    public void execute(final Player player, PacketBuffer.InBuffer in) {
        String command = in.readString();
        final String[] cmd = command.toLowerCase().split(" ");

        if (cmd[0].equals("tele")) {
            final int x = Integer.parseInt(cmd[1]);
            final int y = Integer.parseInt(cmd[2]);

            player.teleport(new TeleportSpell() {
                @Override
                public Position teleportTo() {
                    return new Position(x, y);
                }

                @Override
                public Teleport type() {
                    return player.getSpellbook().getTeleport();
                }

                @Override
                public int baseExperience() {
                    return 500;
                }

                @Override
                public Item[] itemsRequired() {
                    return null;
                }

                @Override
                public int levelRequired() {
                    return 1;
                }
            });
        } else if (cmd[0].equals("picture")) {
            // XXX: take a picture of the screen, saved in the
            // ./data/coordinates/folder :)

            int time = Integer.parseInt(cmd[1]);

            try {
                Robot robot = new Robot();

                robot.keyPress(KeyEvent.VK_ALT);
                robot.keyPress(KeyEvent.VK_PRINTSCREEN);
                robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
                robot.keyRelease(KeyEvent.VK_ALT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GameLogic.getSingleton().submit(new Task(time, false, Time.SECOND) {
                @Override
                public void logic() {
                    try {
                        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                        RenderedImage image = (RenderedImage) t.getTransferData(DataFlavor.imageFlavor);
                        ImageIO.write(image, "png", new File("./data/coordinates/" + player.getPosition() + ".png"));
                        player.getServerPacketBuilder().sendMessage("Finished taking screenshot!");
                        this.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (cmd[0].equals("npc")) {
            int npc = Integer.parseInt(cmd[1]);

            final Mob mob = new Mob(npc, player.getPosition());
            World.register(mob);
        } else if (cmd[0].equals("kill")) {
            player.primaryHit(new Hit(10, DamageType.POISON));
        } else if (cmd[0].equals("knpc")) {
            final Mob mob = new Mob(1, player.getPosition());
            mob.setRespawn(true);
            mob.register();

            GameLogic.getSingleton().submit(new Task(5, false, Time.TICK) {
                @Override
                public void logic() {
                    mob.primaryHit(new Hit(15));
                    this.cancel();
                }
            });

        } else if (cmd[0].equals("region")) {
            MapRegion map = new MapRegion();

            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 13; x++) {
                    for (int y = 0; y < 13; y++) {
                        map.setTile(x, y, z, new MapRegionTile(player.getPosition().getX(), player.getPosition().getY()));
                    }
                }
            }

            player.getServerPacketBuilder().sendCustomMapRegion(map);
        } else if (cmd[0].equals("item")) {
            String item = cmd[1].replaceAll("_", " ");
            int amount = Integer.parseInt(cmd[2]);
            player.getServerPacketBuilder().sendMessage("Searching...");

            int count = 0;
            boolean addedToBank = false;
            for (ItemDefinition i : ItemDefinition.getDefinitions()) {
                if (i == null) {
                    continue;
                }

                if (i.getItemName().equalsIgnoreCase(item) || i.getItemName().startsWith(item) || i.getItemName().endsWith(item)) {
                    if (player.getInventory().getItemContainer().hasRoomFor(new Item(i.getItemId(), amount))) {
                        player.getInventory().addItem(new Item(i.getItemId(), 1));
                    } else {
                        player.getBank().depositItem(player.getBank().getContainer().freeSlot(), i.getItemId(), 1);
                        addedToBank = true;
                    }
                    count++;
                }
            }

            if (count == 0) {
                player.getServerPacketBuilder().sendMessage("Item [" + item + "] not found!");
            } else {
                player.getServerPacketBuilder().sendMessage("Item [" + item + "] found on " + count + " occurances.");
            }

            if (addedToBank) {
                player.getServerPacketBuilder().sendMessage("Some items were added to your bank because there was no space in your inventory.");
            }
        } else if (cmd[0].equals("i")) {
            int x = Integer.parseInt(cmd[1]);

            player.getServerPacketBuilder().sendInterface(x);
        } else if (cmd[0].equals("mypos")) {
            player.getServerPacketBuilder().sendMessage("You are at: " + player.getPosition());
        } else if (cmd[0].equals("additem")) {
            player.getInventory().addItem(new Item(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2])));
        } else if (cmd[0].equals("empty")) {
            player.getInventory().getItemContainer().clear();
            player.getInventory().refresh(InventoryContainer.DEFAULT_INVENTORY_INTERFACE);
        } else if (cmd[0].equals("bank")) {
            player.getBank().createBankingInterface();
        } else if (cmd[0].equals("emote")) {
            int emote = Integer.parseInt(cmd[1]);

            player.animation(new Animation(emote));
        } else if (cmd[0].equals("players")) {
            player.getServerPacketBuilder().sendMessage(World.playerAmount() == 1 ? "There is currently 1 player online!" : "There are currently " + World.playerAmount() + " players online!");
        } else if (cmd[0].equals("gfx")) {
            int gfx = Integer.parseInt(cmd[1]);

            player.gfx(new Gfx(gfx));
        } else if (cmd[0].equals("object")) {
            int id = Integer.parseInt(cmd[1]);

            WorldObject.register(new WorldObject(id, player.getPosition(), Rotation.SOUTH, 10));
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 103 };
    }
}
