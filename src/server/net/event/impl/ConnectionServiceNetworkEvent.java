package server.net.event.impl;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import server.Server;
import server.net.HostGateway;
import server.net.event.AbsentNetworkEvent;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerNetwork;

/**
 * An implementation of the {@link AbsentNetworkEvent} that creates a network
 * for any newly connected clients.
 * 
 * @author lare96
 */
public final class ConnectionServiceNetworkEvent implements AbsentNetworkEvent {

    @Override
    public void event() throws Exception {
        SocketChannel socket;

        /**
         * Here we use a for loop so that we can accept multiple clients per
         * cycle for lower latency. We limit the amount of clients that we
         * accept per cycle to better combat potential denial of service type
         * attacks.
         */
        for (int i = 0; i < 10; i++) {
            socket = Server.getSingleton().getServerChannel().accept();

            if (socket == null) {
                /** No more connections to accept (as this one was invalid). */
                break;
            }

            /** Make sure we can allow this connection. */
            if (!HostGateway.enter(socket.socket().getInetAddress().getHostAddress())) {
                socket.close();
                continue;
            }

            /** Set up the new connection. */
            socket.configureBlocking(false);
            SelectionKey newKey = socket.register(Server.getSingleton().getSelector(), SelectionKey.OP_READ);
            PlayerNetwork client = new PlayerNetwork(newKey);
            new Player(client);
            newKey.attach(client);
        }
    }
}
