package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;

/**
 * Sent when the player updates the chat options.
 * 
 * @author lare96
 */
public class UpdateChatOptionsPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {

    }

    @Override
    public int[] opcode() {
        return new int[] { 95 };
    }
}
