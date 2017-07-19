package server.net.packet.impl;

import server.net.buffer.PacketBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;

/**
 * Sent when the player speaks.
 * 
 * @author lare96
 */
public class PublicChatPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, PacketBuffer.InBuffer in) {
        int effects = in.readByte(false, PacketBuffer.ValueType.S);
        int color = in.readByte(false, PacketBuffer.ValueType.S);
        int chatLength = (player.getNetwork().getPacketLength() - 2);
        byte[] text = in.readBytesReverse(chatLength, PacketBuffer.ValueType.A);

        player.setChatEffects(effects);
        player.setChatColor(color);
        player.setChatText(text);
        player.getFlags().flag(Flag.CHAT);
    }

    @Override
    public int[] opcode() {
        return new int[] { 4 };
    }
}
