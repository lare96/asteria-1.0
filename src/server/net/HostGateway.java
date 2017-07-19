package server.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import server.Server;

/**
 * A static gateway type class that is used to limit the maximum amount of
 * connections per host.
 * 
 * @author blakeman8192
 * @author lare96
 */
public final class HostGateway {

    /** The maximum amount of connections per host. */
    public static final int MAX_CONNECTIONS_PER_HOST = 1;

    /** Used to keep track of hosts and their amount of connections. */
    private static ConcurrentHashMap<String, Integer> hostConnection = new ConcurrentHashMap<String, Integer>();

    /** List of the ip banned hosts. */
    private static CopyOnWriteArrayList<String> banned = new CopyOnWriteArrayList<String>();

    /**
     * Checks the host into the gateway.
     * 
     * @param host
     *            the host that needs to be checked.
     * 
     * @return true if the host can connect, false if it has reached the maximum
     *         amount of connections.
     */
    public static boolean enter(String host) {

        /** Makes sure this host is not connecting too fast. */
        if (!HostThrottler.throttleHost(host)) {
            return false;
        }

        /**
         * If the server is in developer mode, block all connections not from
         * localhost.
         */
        if (Server.isInDeveloperMode() && !host.equals("127.0.0.1") && !host.equals("localhost")) {
            Server.print("Session request from host(" + host + ") rejected until server is no longer in developer mode.");
            return false;
        }

        /** Reject if this host is banned. */
        if (banned.contains(host)) {
            Server.print("Session request from IP banned host(" + host + ") rejected.");
            return false;
        }

        Integer amount = hostConnection.putIfAbsent(host, 1);

        /** If the host was not in the map, they're clear to go. */
        if (amount == null) {
            Server.print("Session request from " + host + "(1) accepted.");
            return true;
        }

        /** If they've reached the connection limit, return false. */
        if (amount == MAX_CONNECTIONS_PER_HOST) {
            Server.print("Session request from " + host + "(" + amount + ") over connection limit, rejected.");
            return false;
        }

        /** Otherwise, replace the key with the next value if it was present. */
        hostConnection.replace(host, amount + 1);
        Server.print("Session request from " + host + "(" + hostConnection.get(host) + ") accepted.");
        return true;
    }

    /**
     * Unchecks the host from the gateway.
     * 
     * @param host
     *            the host that needs to be unchecked.
     */
    public static void exit(String host) {
        Integer amount = hostConnection.get(host);

        if (amount == null) {
            return;
        }

        /** Remove the host from the map if it's at 1 connection. */
        if (amount == 1) {
            hostConnection.remove(host);
            HostThrottler.getHostConnectionTime().remove(host);
            return;
        }

        /** Otherwise decrement the amount of connections stored. */
        if (amount != null) {
            hostConnection.replace(host, amount - 1);
        }
    }

    /**
     * Retrieves the hosts from the <code>ip_banned.txt</code> file and adds
     * them to the list of banned hosts.
     */
    public static void retrieveHosts() {
        Scanner s;
        // int retrieved = 0;

        try {
            s = new Scanner(new File("./data/ip_banned.txt"));

            while (s.hasNextLine()) {
                banned.add(s.nextLine());
                // retrieved++;
            }

            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the banned.
     */
    public static CopyOnWriteArrayList<String> getBanned() {
        return banned;
    }

    /**
     * @return the map.
     */
    public static ConcurrentHashMap<String, Integer> getHostConnection() {
        return hostConnection;
    }
}
