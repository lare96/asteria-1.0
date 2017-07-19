package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.mob.MobDialogue;
import server.world.entity.player.Player;

/**
 * Sent when the player tries to forward a dialogue.
 * 
 * @author lare96
 */
public class ForwardDialoguePacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        if (player.getMobDialogue() != 0) {
            MobDialogue.getDialogues().get(player.getMobDialogue()).dialogue(player);
        } else {
            player.getServerPacketBuilder().closeWindows();
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 40 };
    }
}
