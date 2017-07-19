package server.net.packet;

import server.net.buffer.PacketBuffer;
import server.world.entity.player.Player;

/**
 * A packet sent by the client that will be read by the server.
 * 
 * @author lare96
 */
public interface ClientPacketBuilder {

    /**
     * The packet that will be read by the server.
     * 
     * @param player
     *            the player to execute this packet for.
     * @param in
     *            the buffer for reading the packet data.
     */
    public void execute(Player player, PacketBuffer.InBuffer in);

    /**
     * An array of the opcode(s) for registration.
     * 
     * @return the opcode(s) in an array.
     */
    public int[] opcode();
}
