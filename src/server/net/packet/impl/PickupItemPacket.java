package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.World;
import server.world.entity.player.Player;
import server.world.entity.player.skill.TrainableSkill;
import server.world.item.Item;
import server.world.item.WorldItem;
import server.world.map.Position;

/**
 * Sent when the player picks up an item.
 * 
 * @author lare96
 */
public class PickupItemPacket implements ClientPacketBuilder {

    @Override
    public void execute(final Player player, InBuffer in) {
        final int itemY = in.readShort(ByteOrder.LITTLE);
        final int item = in.readShort(false);
        final int itemX = in.readShort(ByteOrder.LITTLE);

        if (!World.isCanPickup()) {
            player.getServerPacketBuilder().sendMessage("Picking up items has been disabled!");
            return;
        }

        TrainableSkill.check(player);

        player.getMovementQueueListener().submit(new Runnable() {
            @Override
            public void run() {
                if (player.getPosition().equals(new Position(itemX, itemY, player.getPosition().getZ()))) {
                    WorldItem pickingUp = WorldItem.itemExists(new WorldItem(new Item(item, 1), new Position(itemX, itemY, player.getPosition().getZ()), player));

                    if (pickingUp != null) {
                        if (!player.getInventory().getItemContainer().hasRoomFor(new Item(item, pickingUp.getItem().getAmount()))) {
                            player.getServerPacketBuilder().sendMessage("You don't have enough free inventory space to pick-up this item.");
                            return;
                        }

                        pickingUp.pickup(player);
                    } else {
                        player.getServerPacketBuilder().sendMessage("[DEBUG]: Item you are trying to pickup is non-existant.");
                    }
                }
            }
        });
    }

    @Override
    public int[] opcode() {
        return new int[] { 236 };
    }
}
