package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.buffer.PacketBuffer.ValueType;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.entity.player.skill.impl.Mining;
import server.world.entity.player.skill.impl.Runecrafting;
import server.world.entity.player.skill.impl.Smithing;
import server.world.entity.player.skill.impl.Thieving;
import server.world.entity.player.skill.impl.Woodcutting;
import server.world.entity.player.skill.impl.Mining.Ore;
import server.world.entity.player.skill.impl.Mining.OreObject;
import server.world.entity.player.skill.impl.Mining.Pickaxe;
import server.world.entity.player.skill.impl.Runecrafting.Altar;
import server.world.entity.player.skill.impl.Thieving.TheftObject;
import server.world.entity.player.skill.impl.Woodcutting.Axe;
import server.world.entity.player.skill.impl.Woodcutting.StumpObject;
import server.world.entity.player.skill.impl.Woodcutting.Tree;
import server.world.map.Position;
import server.world.object.ObjectDistance;

/**
 * Sent when the player initiates some sort of action with an object.
 * 
 * @author lare96
 */
public class ObjectActionPackets implements ClientPacketBuilder {

    /**
     * The packet opcodes.
     */
    private static final int FIRST_CLICK = 132, SECOND_CLICK = 252,
            THIRD_CLICK = 70;

    @Override
    public void execute(final Player player, InBuffer in) {
        switch (player.getNetwork().getPacketOpcode()) {
            case FIRST_CLICK:
                final int objectX = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
                final int objectId = in.readShort(false);
                final int objectY = in.readShort(false, ValueType.A);

                player.facePosition(new Position(objectX, objectY));
                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(objectX, objectY, player.getPosition().getZ()), ObjectDistance.getDefault())) {

                            switch (objectId) {
                                case 450:
                                case 451:
                                case 452:
                                case 453:
                                    player.getServerPacketBuilder().sendMessage("There is no ore concealed within this rock!");
                                    break;
                                case 3193:
                                case 2213:
                                    player.getBank().createBankingInterface();
                                    break;
                                case 409:
                                    if (player.getSkills().getTrainable()[Skill.PRAYER.ordinal()].getLevel() < player.getSkills().getTrainable()[Skill.PRAYER.ordinal()].getLevelForExperience()) {
                                        player.animation(new Animation(645));
                                        player.getSkills().getTrainable()[Skill.PRAYER.ordinal()].setLevel(player.getSkills().getTrainable()[Skill.PRAYER.ordinal()].getLevelForExperience());
                                        player.getServerPacketBuilder().sendMessage("You recharge your prayer points.");
                                        SkillManager.getSingleton().refresh(player, Skill.PRAYER);
                                    } else {
                                        player.getServerPacketBuilder().sendMessage("You already have full prayer points.");
                                    }
                                    break;
                                case 6552:
                                    if (player.getSpellbook() == Spellbook.ANCIENT) {
                                        Spellbook.convert(player, Spellbook.NORMAL);
                                    } else if (player.getSpellbook() == Spellbook.NORMAL) {
                                        Spellbook.convert(player, Spellbook.ANCIENT);
                                    }
                                    break;
                                case 2108:
                                case 2109:
                                case 2090:
                                case 2091:
                                case 2094:
                                case 2095:
                                case 2092:
                                case 2093:
                                case 2100:
                                case 2101:
                                case 2096:
                                case 2097:
                                case 2098:
                                case 2099:
                                case 2102:
                                case 2103:
                                case 2104:
                                case 2105:
                                case 2106:
                                case 2107:
                                case 2491:
                                    for (Ore o : Ore.values()) {
                                        if (o == null) {
                                            continue;
                                        }

                                        for (OreObject ore : o.getObjectOre()) {
                                            if (ore == null) {
                                                continue;
                                            }

                                            if (objectId == ore.getOre()) {
                                                Pickaxe pick = Mining.getSingleton().getPickaxe(player);

                                                if (pick != null) {
                                                    Mining.getSingleton().mine(player, o, pick, new Position(objectX, objectY, player.getPosition().getZ()), objectId);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case 2478:
                                case 2479:
                                case 2480:
                                case 2481:
                                case 2482:
                                case 2483:
                                case 2484:
                                case 2487:
                                case 2486:
                                case 2485:
                                case 2488:
                                case 7141:
                                case 7138:
                                    for (Altar a : Altar.values()) {
                                        if (a == null) {
                                            continue;
                                        }

                                        if (a.getAltarId() == objectId) {
                                            Runecrafting.getSingleton().craftRunes(player, a.getRune());
                                        }
                                    }
                                    break;
                                case 2781:
                                case 2785:
                                case 2966:
                                case 6189:
                                case 3044:
                                case 3294:
                                case 4304:
                                    Smithing.getSingleton().smeltInterface(player);
                                    break;
                            }

                            for (Tree t : Tree.values()) {
                                if (t == null) {
                                    continue;
                                }

                                for (StumpObject tree : t.getTrees()) {
                                    if (tree == null) {
                                        continue;
                                    }

                                    if (objectId == tree.getTreeId()) {
                                        Axe axe = Woodcutting.getSingleton().getAxe(player);

                                        if (axe != null) {
                                            Woodcutting.getSingleton().cut(player, t, axe, new Position(objectX, objectY, player.getPosition().getZ()), objectId);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                break;

            case SECOND_CLICK:
                final int objId = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
                final int objY = in.readShort(true, ByteOrder.LITTLE);
                final int objX = in.readShort(false, ValueType.A);

                player.facePosition(new Position(objX, objY));

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(objX, objY, player.getPosition().getZ()), ObjectDistance.getDefault())) {
                            switch (objId) {
                                case 635:
                                    player.getServerPacketBuilder().sendMessage("lol");
                                    Thieving.getSingleton().stealFromObject(player, TheftObject.TEA_STALL);
                                    break;
                                case 2108:
                                case 2109:
                                case 2090:
                                case 2091:
                                case 2094:
                                case 2095:
                                case 2092:
                                case 2093:
                                case 2100:
                                case 2101:
                                case 2096:
                                case 2097:
                                case 2098:
                                case 2099:
                                case 2102:
                                case 2103:
                                case 2104:
                                case 2105:
                                case 2106:
                                case 2107:
                                case 2491:
                                    for (Ore o : Ore.values()) {
                                        if (o == null) {
                                            continue;
                                        }

                                        for (OreObject ore : o.getObjectOre()) {
                                            if (ore == null) {
                                                continue;
                                            }

                                            if (objId == ore.getOre()) {
                                                Mining.getSingleton().prospect(player, o);
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                });
                break;

            case THIRD_CLICK:
                final int x = in.readShort(true, ByteOrder.LITTLE);
                final int y = in.readShort(false);
                final int id = in.readShort(false, ValueType.A, ByteOrder.LITTLE);

                player.facePosition(new Position(x, y));

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(x, y, player.getPosition().getZ()), ObjectDistance.getDefault())) {
                            switch (id) {

                                // FIXME: Find the ids for all of the thieving
                                // stalls, and add them here.
                                case 635:
                                    Thieving.getSingleton().stealFromObject(player, TheftObject.TEA_STALL);
                                    break;
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 132, 252, 70 };
    }
}
