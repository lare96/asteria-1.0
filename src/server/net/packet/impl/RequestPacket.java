package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.World;
import server.world.entity.player.Player;
import server.world.entity.player.skill.TrainableSkill;

/**
 * Sent when the player sends another player some sort of request.
 * 
 * @author lare96
 */
public class RequestPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        int tradeId = in.readShort(true, ByteOrder.LITTLE);
        Player trade = World.getPlayers()[tradeId];

        if (!World.isCanTrade()) {
            player.getServerPacketBuilder().sendMessage("Trading has been disabled!");
            return;
        }

        if (trade != null) {
            player.getTrading().request(trade);
        }

        TrainableSkill.check(player);
    }

    @Override
    public int[] opcode() {
        return new int[] { 139 };
    }
}
