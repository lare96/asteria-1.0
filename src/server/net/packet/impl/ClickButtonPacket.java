package server.net.packet.impl;

import server.Server;
import server.net.buffer.PacketBuffer;
import server.net.packet.ClientPacketBuilder;
import server.util.Misc;
import server.world.entity.Gfx;
import server.world.entity.mob.MobDialogue;
import server.world.entity.player.Player;
import server.world.entity.player.content.Trade.Stage;
import server.world.entity.player.skill.impl.Cooking;
import server.world.entity.player.skill.impl.Runecrafting;
import server.world.entity.player.skill.impl.Runecrafting.Altar;

/**
 * Sent when the player clicks an action button.
 * 
 * @author lare96
 */
public class ClickButtonPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, PacketBuffer.InBuffer in) {
        int buttonId = Misc.hexToInt(in.readBytes(2));

        switch (buttonId) {

            /** Prayers */
            case 21233:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.THICK_SKIN);
                break;
            case 21234:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.BURST_OF_STRENGTH);
                break;
            case 21235:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.CLARITY_OF_THOUGHT);
                break;
            case 21236:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.ROCK_SKIN);
                break;
            case 21237:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.SUPERHUMAN_STRENGTH);
                break;
            case 21238:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.IMPROVED_REFLEXES);
                break;
            case 21239:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.RAPID_RESTORE);
                break;
            case 21240:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.RAPID_HEAL);
                break;
            case 21241:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_ITEM);
                break;
            case 21242:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.STEEL_SKIN);
                break;
            case 21243:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.ULTIMATE_STRENGTH);
                break;
            case 21244:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.INCREDIBLE_REFLEXES);
                break;
            case 21245:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_FROM_MAGIC);
                break;
            case 21246:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_FROM_MISSILES);
                break;
            case 21247:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_FROM_MELEE);
                break;
            case 2171:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.RETRIBUTION);
                break;
            case 2172:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.REDEMPTION);
                break;
            case 2173:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.SMITE);
                break;
            /** End of Prayers */

            case 150:
                player.setAutoRetaliate(true);
                player.getServerPacketBuilder().sendMessage("Auto retaliate has been turned on!");
                break;
            case 151:
                player.setAutoRetaliate(false);
                player.getServerPacketBuilder().sendMessage("Auto retaliate has been turned off!");
                break;
            case 56109:
                switch (player.getOption()) {
                    case 1:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Runecrafting.RUNE_ESSENCE_MINE);
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 6:
                        player.getServerPacketBuilder().closeWindows();
                        player.heal(99);
                        player.getServerPacketBuilder().sendMessage("You feel a magical aura pass through your body.");
                        player.gfx(new Gfx(436));
                        break;
                    case 7:
                        // start security tapes
                        break;
                }
                break;
            case 56110:
                switch (player.getOption()) {
                    case 1:
                        MobDialogue.fiveOptions(player, "Air", "Mind", "Water", "Earth", "Next");
                        player.setOption(2);
                        break;
                    case 6:
                    case 7:
                        player.getServerPacketBuilder().closeWindows();
                        break;
                }
                break;

            case 32017:
                switch (player.getOption()) {
                    case 5:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.DEATH.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32018:
                switch (player.getOption()) {
                    case 5:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.BLOOD.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32019:
                switch (player.getOption()) {
                    case 5:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.SOUL.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32020:
                switch (player.getOption()) {
                    case 5:
                        MobDialogue.fiveOptions(player, "Chaos", "Nature", "Law", "Next", "Previous");
                        player.setOption(4);
                        break;
                }
                break;

            case 32029:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.AIR.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 3:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.FIRE.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 4:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.CHAOS.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32030:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.MIND.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 3:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.BODY.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 4:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.NATURE.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32031:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.WATER.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 3:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.COSMIC.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 4:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.LAW.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32032:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.EARTH.getTeleport());
                        player.getServerPacketBuilder().closeWindows();
                        break;
                    case 3:
                        MobDialogue.fiveOptions(player, "Chaos", "Nature", "Law", "Next", "Previous");
                        player.setOption(4);
                        break;
                    case 4:
                        MobDialogue.fourOptions(player, "Death", "Blood", "Soul", "Previous");
                        player.setOption(5);
                        break;
                }
                break;
            case 32033:
                switch (player.getOption()) {
                    case 2:
                        MobDialogue.fiveOptions(player, "Fire", "Body", "Cosmic", "Next", "Previous");
                        player.setOption(3);
                        break;
                    case 3:
                        MobDialogue.fiveOptions(player, "Air", "Mind", "Water", "Earth", "Next");
                        player.setOption(2);
                        break;
                    case 4:
                        MobDialogue.fiveOptions(player, "Fire", "Body", "Cosmic", "Next", "Previous");
                        player.setOption(3);
                        break;
                }
                break;

            case 9154:
                player.getNetwork().disconnect();
                break;
            case 153:
                player.getMovementQueue().setRunToggled(true);
                break;
            case 152:
                player.getMovementQueue().setRunToggled(false);
                break;
            case 21011:
                player.setWithdrawAsNote(false);
                break;
            case 21010:
                player.setWithdrawAsNote(true);
                break;
            case 31195:
                player.setInsertItem(true);
                break;
            case 31194:
                player.setInsertItem(false);
                break;
            case 53152:
                Cooking.getSingleton().cook(player, player.getCook(), 1);
                break;
            case 53151:
                Cooking.getSingleton().cook(player, player.getCook(), 5);
                break;
            case 53149:
                int i = player.getInventory().getItemContainer().getCount(player.getCook().getId());

                Cooking.getSingleton().cook(player, player.getCook(), i);
                break;
            case 13092:
                Player partner = player.getTrading().getPartner();

                if (partner.getInventory().getItemContainer().freeSlots() < player.getTrading().getOffering().size()) {
                    player.getServerPacketBuilder().sendMessage(partner.getUsername() + " does not have enough free slots for this many items.");
                    return;
                } else {
                    player.getTrading().setAcceptInitialOffer(true);
                    player.getServerPacketBuilder().sendString("Waiting for other player...", 3431);
                    partner.getServerPacketBuilder().sendString("Other player has accepted", 3431);
                }

                if (player.getTrading().isAcceptInitialOffer() && partner.getTrading().isAcceptInitialOffer()) {
                    player.getTrading().setStage(Stage.CONFIRM_OFFER);
                    partner.getTrading().setStage(Stage.CONFIRM_OFFER);

                    player.getTrading().confirmTrade();
                    partner.getTrading().confirmTrade();
                }
                break;
            case 13218:
                partner = player.getTrading().getPartner();

                player.getTrading().setAcceptConfirmOffer(true);
                partner.getServerPacketBuilder().sendString("Other player has accepted.", 3535);
                player.getServerPacketBuilder().sendString("Waiting for other player...", 3535);

                if (player.getTrading().isAcceptConfirmOffer() && partner.getTrading().isAcceptConfirmOffer()) {
                    player.getTrading().finishTrade();
                }
                break;
            default:
                Server.print("Unhandled button: " + buttonId);
                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 185 };
    }
}
