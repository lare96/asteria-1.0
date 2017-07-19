package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.buffer.PacketBuffer.ValueType;
import server.net.packet.ClientPacketBuilder;
import server.world.World;
import server.world.entity.mob.Mob;
import server.world.entity.mob.MobDefinition;
import server.world.entity.player.Player;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.impl.Fishing;
import server.world.entity.player.skill.impl.Thieving;
import server.world.entity.player.skill.impl.Fishing.Tools;
import server.world.entity.player.skill.impl.Thieving.TheftMob;
import server.world.map.Position;
import server.world.shop.Shop;

/**
 * Sent when the player initiates some sort of action with a mob.
 * 
 * @author lare96
 */
public class MobActionPackets implements ClientPacketBuilder {

    public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155,
            SECOND_CLICK = 17;

    @Override
    public void execute(final Player player, InBuffer in) {
        TrainableSkill.check(player);

        switch (player.getNetwork().getPacketOpcode()) {
            case ATTACK_NPC:
                int index = in.readShort(false, ValueType.A);
                final Mob attackMelee = World.getNpcs()[index];

                if (attackMelee == null) {
                    return;
                }

                if (attackMelee.isHasDied()) {
                    return;
                }

                if (!MobDefinition.getMobDefinition()[attackMelee.getMobId()].isAttackable()) {
                    return;
                }

                break;
            case MAGE_NPC:
                index = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
                final Mob attackMagic = World.getNpcs()[index];

                if (attackMagic == null) {
                    return;
                }

                // FIXME: start magic!!
                break;
            case FIRST_CLICK:
                index = in.readShort(true, ByteOrder.LITTLE);
                final Mob firstClickMob = World.getNpcs()[index];

                if (firstClickMob == null) {
                    return;
                }

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(firstClickMob.getPosition().getX(), firstClickMob.getPosition().getY(), firstClickMob.getPosition().getZ()), 1)) {
                            player.facePosition(firstClickMob.getPosition());

                            switch (firstClickMob.getMobId()) {
                                case 460:
                                    player.setRunecraftingMob(firstClickMob);
                                    player.dialogue(2);
                                    break;
                                case 1:
                                    player.dialogue(1);
                                    break;
                                case 249:
                                    player.dialogue(3);
                                    break;
                                case 605:
                                    player.dialogue(4);
                                    break;
                                case 319:
                                    if (player.getInventory().getItemContainer().contains(Tools.NET.getId())) {
                                        Fishing.getSingleton().fish(player, Tools.NET);
                                    } else {
                                        Fishing.getSingleton().fish(player, Tools.BIG_NET);
                                    }
                                    break;
                                case 324:
                                    Fishing.getSingleton().fish(player, Tools.LOBSTER_POT);
                                    break;
                                case 328:
                                    Fishing.getSingleton().fish(player, Tools.FLY_FISHING_ROD);
                                    break;
                                case 520:
                                    Shop.getShop(0).openShop(player);
                                    break;
                            }
                        }
                    }
                });
                break;

            case SECOND_CLICK:
                index = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
                final Mob secondClickMob = World.getNpcs()[index];

                if (secondClickMob == null) {
                    return;
                }

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(secondClickMob.getPosition().getX(), secondClickMob.getPosition().getY(), secondClickMob.getPosition().getZ()), 1)) {
                            player.facePosition(secondClickMob.getPosition());

                            switch (secondClickMob.getMobId()) {
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.MAN_AND_WOMAN, secondClickMob);
                                    break;
                                case 7:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.FARMER, secondClickMob);
                                    break;
                                case 1714:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.MALE_HAM, secondClickMob);
                                    break;
                                case 1715:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.FEMALE_HAM, secondClickMob);
                                    break;
                                case 15:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.WARRIOR_WOMAN, secondClickMob);
                                    break;
                                case 187:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.ROGUE, secondClickMob);
                                    break;
                                case 2234:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.MASTER_FARMER, secondClickMob);
                                    break;
                                case 9:
                                case 32:
                                case 2699:
                                case 2700:
                                case 2701:
                                case 2702:
                                case 2703:
                                case 3228:
                                case 3229:
                                case 3230:
                                case 3231:
                                case 3232:
                                case 3233:
                                case 3241:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.GUARD, secondClickMob);
                                    break;
                                case 1305:
                                case 1306:
                                case 1307:
                                case 1308:
                                case 1309:
                                case 1310:
                                case 1311:
                                case 1312:
                                case 1313:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.RELLEKKA_CITIZEN, secondClickMob);
                                    break;
                                case 23:
                                case 26:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.KNIGHT_OF_ARDOUGNE, secondClickMob);
                                    break;
                                case 34:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.WATCHMAN, secondClickMob);
                                    break;
                                case 1904:
                                case 1905:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.MENAPHITE_THUG, secondClickMob);
                                    break;
                                case 20:
                                case 365:
                                case 2256:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.PALADIN, secondClickMob);
                                    break;
                                case 66:
                                case 67:
                                case 68:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.GNOME, secondClickMob);
                                    break;
                                case 21:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.HERO, secondClickMob);
                                    break;
                                case 1183:
                                case 1184:
                                    Thieving.getSingleton().stealFromMob(player, TheftMob.ELF, secondClickMob);
                                    break;
                                case 319:
                                    if (player.getInventory().getItemContainer().contains(Tools.FISHING_ROD.getId())) {
                                        Fishing.getSingleton().fish(player, Tools.FISHING_ROD);
                                    } else {
                                        Fishing.getSingleton().fish(player, Tools.OILY_FISHING_ROD);
                                    }
                                    break;
                                case 324:
                                    Fishing.getSingleton().fish(player, Tools.HARPOON);
                                    break;
                                case 328:
                                    if (player.getInventory().getItemContainer().contains(Tools.FISHING_ROD.getId())) {
                                        Fishing.getSingleton().fish(player, Tools.FISHING_ROD);
                                    } else {
                                        Fishing.getSingleton().fish(player, Tools.OILY_FISHING_ROD);
                                    }
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
        return new int[] { 72, 131, 155, 17 };
    }
}
