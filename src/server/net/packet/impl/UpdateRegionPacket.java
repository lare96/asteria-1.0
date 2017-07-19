package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;
import server.world.item.WorldItem;
import server.world.object.WorldObject;

/**
 * Sent when the player enters a new region
 * 
 * @author lare96
 */
public class UpdateRegionPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        WorldObject.load(player);
        WorldItem.load(player);

        if (!player.isFirstPacket()) {
            player.setFirstPacket(true);
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 121 };
    }
}
