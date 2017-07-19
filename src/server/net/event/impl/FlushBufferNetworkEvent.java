package server.net.event.impl;

import java.io.IOException;

import server.net.event.NetworkEvent;
import server.world.entity.player.PlayerNetwork;

/**
 * An implementation of the {@link NetworkEvent} that flushes the buffer.
 * 
 * @author lare96
 */
public final class FlushBufferNetworkEvent implements NetworkEvent {

    @Override
    public void event(PlayerNetwork network) {
        try {
            synchronized (network.getOutData()) {
                network.getOutData().flip();
                network.getSocketChannel().write(network.getOutData());

                /** Check if all the data was sent. */
                if (!network.getOutData().hasRemaining()) {

                    /** And clear the buffer. */
                    network.getOutData().clear();
                } else {
                    /** Not all data was sent - compact it! */
                    network.getOutData().compact();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            network.setPacketDisconnect(true);
            network.disconnect();
        }
    }
}
