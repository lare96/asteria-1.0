package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;

/**
 * Sent when the player does anything that has to do with private messaging.
 * 
 * @author lare96
 */
public class PrivateMessagingPackets implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        switch (player.getNetwork().getPacketOpcode()) {
            case 188:
                long name = in.readLong();
                player.getPrivateMessage().addFriend(name);
                break;
            case 215:
                name = in.readLong();
                player.getPrivateMessage().removeFriend(name);
                break;
            case 133:
                name = in.readLong();
                player.getPrivateMessage().addIgnore(name);
                break;
            case 74:
                name = in.readLong();
                player.getPrivateMessage().removeIgnore(name);
                break;
            case 126:
                long to = in.readLong();
                int size = player.getNetwork().getPacketLength() - 8;
                byte[] message = in.readBytes(size);

                player.getPrivateMessage().sendPrivateMessage(player, to, message, size);
                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 188, 215, 133, 74, 126 };
    }
}
