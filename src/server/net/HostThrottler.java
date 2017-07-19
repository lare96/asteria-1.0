package server.net;

import java.util.concurrent.ConcurrentHashMap;

import server.Server;
import server.logic.task.Task.Time;
import server.util.Misc.Stopwatch;

/**
 * Controls and limits the amount of connections a single host can make per
 * second.
 * 
 * @author lare96
 */
public final class HostThrottler {

    // XXX: Wrote this in one sitting and attempted to use the runerebels stress
    // tester to flood the server, and even at 1ms intervals this throttler
    // prevented all of the bots but one from connecting! Success! I haven't
    // tested this for bugs nor have I found any yet, so be on the lookout.

    // TODO: extensive testing.

    /** A map of hosts and their respective timers. */
    private static ConcurrentHashMap<String, Stopwatch> hostConnectionTime = new ConcurrentHashMap<String, Stopwatch>();

    /** The maximum amount of connections allowed per second for a single host. */
    private static final int AMOUNT_OF_CONNECTIONS_PER_SECOND = 1;

    /**
     * Makes sure the host isn't connecting too fast.
     * 
     * @param host
     *            the host being checked for connecting too fast.
     * @return true if the host is allowed to pass.
     */
    public static boolean throttleHost(String host) {

        /**
         * If the host has connected once already we need to check if they are
         * allowed to connect again.
         */
        if (HostThrottler.getHostConnectionTime().containsKey(host)) {

            /** Get the time since the last connection. */
            long time = HostThrottler.getHostConnectionTime().get(host).elapsed();

            /** Get how many existing connections this host has. */
            Integer connection = HostGateway.getHostConnection().get(host) == null ? 0 : HostGateway.getHostConnection().get(host);

            /**
             * If the time since the last connection is less than a second and
             * the amount of connections is equal to or above the
             * <code>AMOUNT_OF_CONNECTIONS_PER_SECOND</code> then the host is
             * connecting too fast.
             */
            if (time < Time.SECOND.getTime() && connection >= AMOUNT_OF_CONNECTIONS_PER_SECOND) {
                Server.print("Session request from " + host + " denied: connecting too fast!");
                return false;
            }

            /**
             * If the host has waited one second before connecting again the
             * timer is reset and the host is allowed to pass.
             */
            HostThrottler.getHostConnectionTime().get(host).reset();
            return true;

            /**
             * If the host is connecting for the first time (has no other
             * clients logged in) then the host is added to the connection list
             * with its own timer.
             */
        } else {
            HostThrottler.getHostConnectionTime().put(host, new Stopwatch().reset());
            return true;
        }
    }

    /**
     * @return the hostConnectionTime.
     */
    public static ConcurrentHashMap<String, Stopwatch> getHostConnectionTime() {
        return hostConnectionTime;
    }
}
