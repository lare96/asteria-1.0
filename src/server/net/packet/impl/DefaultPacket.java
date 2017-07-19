package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;

/**
 * An empty packet executed when unused packets are sent.
 * 
 * @author lare96
 */
public class DefaultPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {

    }

    @Override
    public int[] opcode() {
        return new int[] { 0 };
    }
}
