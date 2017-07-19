package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;

/**
 * Sent when the player clicks certain options on an interface.
 * 
 * @author lare96
 */
public class InterfaceClickPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        if (player.getTrading().inTrade()) {
            player.getTrading().resetTrade(true);
        }

        player.getServerPacketBuilder().closeWindows();
    }

    @Override
    public int[] opcode() {
        return new int[] { 130 };
    }
}
