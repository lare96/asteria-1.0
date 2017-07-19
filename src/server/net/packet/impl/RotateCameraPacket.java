package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;

/**
 * Sent when the player rotates the camera.
 * 
 * @author lare96
 */
public class RotateCameraPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {

    }

    @Override
    public int[] opcode() {
        return new int[] { 86 };
    }
}
