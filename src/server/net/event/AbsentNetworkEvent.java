package server.net.event;

/**
 * Used for events that take place before the network has been created for the
 * client.
 * 
 * @author lare96
 */
public interface AbsentNetworkEvent {

    /**
     * The pre-network event that will be dispatched.
     */
    public void event() throws Exception;
}
