package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.World;
import server.world.entity.player.Player;

/**
 * Sent when a player attacks another player.
 * 
 * @author lare96
 */
public class AttackPlayerPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        int index = in.readShort(true, ByteOrder.LITTLE);

        Player attacked = World.getPlayers()[index];

        if (attacked == null) {
            return;
        }

    }

    @Override
    public int[] opcode() {
        return new int[] { 73 };
    }
}
