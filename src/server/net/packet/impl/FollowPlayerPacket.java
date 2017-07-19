package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;
import server.world.entity.player.skill.TrainableSkill;

/**
 * Sent when the player tries to follow another player.
 * 
 * @author lare96
 */
public class FollowPlayerPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        @SuppressWarnings("unused")
        int followId = in.readShort(false, ByteOrder.LITTLE);

        TrainableSkill.check(player);
    }

    @Override
    public int[] opcode() {
        return new int[] { 39 };
    }
}
