package server.net.event;

import server.world.entity.player.PlayerNetwork;

/**
 * Used for events that take place after the network has been created for the
 * client.
 * 
 * @author lare96
 */
public interface NetworkEvent {

    /**
     * The post-network event that will be dispatched.
     * 
     * @param network
     *            the client that this network event will be dispatched to.
     */
    public void event(PlayerNetwork network);
}
