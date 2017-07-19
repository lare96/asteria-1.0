package server.net.event;

import java.nio.channels.SelectionKey;

import server.Server;
import server.net.event.impl.ConnectionServiceNetworkEvent;
import server.net.event.impl.FlushBufferNetworkEvent;
import server.net.event.impl.IncomingPacketNetworkEvent;
import server.world.entity.player.PlayerNetwork;

/**
 * Asynchronously dispatches network events to multiple clients at the beginning
 * of every tick.
 * 
 * @author lare96
 */
public class NetworkEventDispatcher {

    /**
     * The singleton instance.
     */
    private static NetworkEventDispatcher singleton;

    /**
     * Creates a network for the client.
     */
    private static ConnectionServiceNetworkEvent connectionServiceNetworkEvent = new ConnectionServiceNetworkEvent();

    /**
     * Reads and handles packets for this client.
     */
    private static IncomingPacketNetworkEvent incomingPacketNetworkEvent = new IncomingPacketNetworkEvent();

    /**
     * Flushes the buffer for this client.
     */
    private static FlushBufferNetworkEvent flushBufferNetworkEvent = new FlushBufferNetworkEvent();

    /**
     * Dispatches all of the networking events.
     */
    public void dispatch() {
        try {

            /** Selects the clients ready for network events. */
            Server.getSingleton().getSelector().selectNow();

            /** Iterates over the clients selected. */
            for (SelectionKey key : Server.getSingleton().getSelector().selectedKeys()) {
                if (key.isValid() && key.isAcceptable()) {

                    /**
                     * Dispatch an absent network event implementation that
                     * creates a network for the client.
                     */
                    NetworkEventDispatcher.getSingleton().getConnectionServiceNetworkEvent().event();
                } else {
                    PlayerNetwork client = (PlayerNetwork) key.attachment();

                    if (key.isValid() && key.isReadable()) {

                        /**
                         * Dispatch a network event implementation that reads
                         * and handles packets for this client.
                         */
                        NetworkEventDispatcher.getSingleton().getIncomingPacketNetworkEvent().event(client);
                    }
                    if (key.isValid() && key.isWritable()) {

                        /**
                         * Dispatch a network event implementation that flushes
                         * the buffer for this client.
                         */
                        NetworkEventDispatcher.getSingleton().getFlushBufferNetworkEvent().event(client);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the connectionServiceNetworkEvent.
     */
    public ConnectionServiceNetworkEvent getConnectionServiceNetworkEvent() {
        return connectionServiceNetworkEvent;
    }

    /**
     * @return the incomingPacketNetworkEvent.
     */
    public IncomingPacketNetworkEvent getIncomingPacketNetworkEvent() {
        return incomingPacketNetworkEvent;
    }

    /**
     * @return the flushBufferNetworkEvent.
     */
    public FlushBufferNetworkEvent getFlushBufferNetworkEvent() {
        return flushBufferNetworkEvent;
    }

    /**
     * 
     * @return the singleton.
     */
    public static NetworkEventDispatcher getSingleton() {
        if (singleton == null) {
            singleton = new NetworkEventDispatcher();
        }

        return singleton;
    }
}
