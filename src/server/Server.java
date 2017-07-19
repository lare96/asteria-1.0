package server;

import java.io.FileNotFoundException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Calendar;

import server.logic.GameLogic;
import server.net.HostGateway;
import server.net.packet.PacketRegistry;
import server.util.Misc;
import server.util.ServerGUI;
import server.util.Misc.Stopwatch;
import server.world.LogicCycle;
import server.world.entity.mob.Mob;
import server.world.entity.mob.MobDefinition;
import server.world.entity.mob.MobDialogue;
import server.world.entity.player.skill.TrainableSkill;
import server.world.item.ItemDefinition;
import server.world.item.WorldItem;
import server.world.object.WorldObject;
import server.world.shop.Shop;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * The main class which puts the server online and contains a series of network
 * operations.
 * 
 * @author blakeman8192
 * @author lare96
 */
public final class Server {

    /**
     * The singleton instance.
     */
    private static Server singleton;

    /**
     * The host address for the server to listen on.
     */
    private static final String HOST = "127.0.0.1";

    /**
     * The port for the server to listen on.
     */
    private static final int PORT = 43594;

    /**
     * The selector.
     */
    private static Selector selector;

    /**
     * The address the server will listen on.
     */
    private static InetSocketAddress address;

    /**
     * The listener itself.
     */
    private static ServerSocketChannel serverChannel;

    /**
     * The last cycle time calculated.
     */
    private static long cycleTimeOverhead;

    /**
     * If the statistics should update or not.
     */
    private static boolean liveStatistics = false;

    /**
     * The current date.
     */
    private static Calendar date = Calendar.getInstance();

    /**
     * If this server is in developer mode (no connections other than localhost
     * will be accepted).
     */
    private static boolean inDeveloperMode;

    /**
     * If this server is in beta mode (welcome message changed).
     */
    private static boolean inBetaMode;

    /**
     * A timer that will determine how much stress the server is under.
     */
    private static Stopwatch overhead = new Stopwatch();

    /**
     * A timer that determines how long it will take for the server to start.
     */
    private static Stopwatch startup = new Stopwatch();

    /**
     * Total time this server has been online.
     */
    private static Stopwatch totalOnlineTime = new Stopwatch().reset();

    /**
     * The first method called when the server is ran.
     * 
     * @param args
     *            any runtime arguments.
     */
    public static void main(String[] args) {
        startup.reset();

        /** Set the singleton instance. */
        setSingleton(new Server());

        /** Start the graphical user interface. */
        // XXX: Comment this out to stop the gui from loading!
        ServerGUI.start();

        /** Configure the socket address. */
        Server.getSingleton().setAddress(new InetSocketAddress(Server.HOST, Server.PORT));

        /** Load miscellaneous things. */
        try {
            Misc.loadPlatebody();
            Misc.loadFullHelm();
            WorldObject.load();
            ItemDefinition.load();
            MobDefinition.load();
            PacketRegistry.load();
            HostGateway.retrieveHosts();
            Shop.load();
            WorldItem.load();
            Mob.load();
            Misc.loadTwoHanded();
            TrainableSkill.load();
            MobDialogue.load();

            // XXX: Uncomment to load minigames.
            // MinigameManager.load();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        /** Initialize the networking objects. */
        try {
            Server.getSingleton().setSelector(Selector.open());
            Server.getSingleton().setServerChannel(ServerSocketChannel.open());

            /** ... and configure them! */
            Server.getSingleton().getServerChannel().configureBlocking(false);
            Server.getSingleton().getServerChannel().socket().bind(Server.getSingleton().getAddress());
            Server.getSingleton().getServerChannel().register(Server.getSingleton().getSelector(), SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Start the engine! */
        GameLogic.getSingleton().submit(new LogicCycle());

        Server.print(Server.getSingleton() + " took " + startup.elapsed() + "ms to load!");
        Server.print(Server.getSingleton() + " is online on " + Server.getSingleton().getAddress());
    }

    @Override
    public String toString() {
        return "Asteria #317";
    }

    /**
     * Prints a standard message that will be displayed on the gui.
     * 
     * @param print
     *            the string to print.
     */
    public static void print(String print) {
        ServerGUI.getConsoleTextArea().append("[" + date.get(Calendar.YEAR) + "/" + date.get(Calendar.MONTH) + "/" + date.get(Calendar.DAY_OF_MONTH) + "]: " + print + "\n");
        ServerGUI.getConsoleTextArea().setCaretColor(null);
        ServerGUI.getConsoleTextArea().setCaretPosition(ServerGUI.getConsoleTextArea().getDocument().getLength());
    }

    /**
     * Prints an integer that will be displayed on the gui.
     * 
     * @param print
     *            the integer to print.
     */
    public static void print(int print) {
        print("" + print + "");
    }

    /**
     * Prints a short that will be displayed on the gui.
     * 
     * @param print
     *            the short to print.
     */
    public static void print(short print) {
        print("" + print + "");
    }

    /**
     * Prints a long that will be displayed on the gui.
     * 
     * @param print
     *            the long to print.
     */
    public static void print(long print) {
        print("" + print + "");
    }

    /**
     * Prints a double that will be displayed on the gui.
     * 
     * @param print
     *            the double to print.
     */
    public static void print(double print) {
        print("" + print + "");
    }

    /**
     * Sets the server singleton object.
     * 
     * @param singleton
     *            the singleton.
     */
    private static void setSingleton(Server singleton) {
        if (Server.singleton != null) {
            throw new IllegalStateException("Singleton already set!");
        }

        Server.singleton = singleton;
    }

    /**
     * Gets the selector.
     * 
     * @return The selector.
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * @param address
     *            the address to set.
     */
    private void setSelector(Selector selector) {
        Server.selector = selector;
    }

    /**
     * Gets the server singleton object.
     * 
     * @return the singleton.
     */
    public static Server getSingleton() {
        return singleton;
    }

    /**
     * @return the address.
     */
    private InetSocketAddress getAddress() {
        return Server.address;
    }

    /**
     * @param address
     *            the address to set.
     */
    private void setAddress(InetSocketAddress address) {
        Server.address = address;
    }

    /**
     * @return the serverChannel.
     */
    public ServerSocketChannel getServerChannel() {
        return Server.serverChannel;
    }

    /**
     * @param serverChannel
     *            the serverChannel to set.
     */
    private void setServerChannel(ServerSocketChannel serverChannel) {
        Server.serverChannel = serverChannel;
    }

    /**
     * @return the cycleTimeOverhead.
     */
    public static long getCycleTimeOverhead() {
        return cycleTimeOverhead;
    }

    /**
     * @param cycleTimeOverhead
     *            the cycleTimeOverhead to set.
     */
    public static void setCycleTimeOverhead(long cycleTimeOverhead) {
        Server.cycleTimeOverhead = cycleTimeOverhead;
    }

    /**
     * @return the inDeveloperMode.
     */
    public static boolean isInDeveloperMode() {
        return inDeveloperMode;
    }

    /**
     * @param inDeveloperMode
     *            the inDeveloperMode to set.
     */
    public static void setInDeveloperMode(boolean inDeveloperMode) {
        Server.inDeveloperMode = inDeveloperMode;
    }

    /**
     * @return the inBetaMode.
     */
    public static boolean isInBetaMode() {
        return inBetaMode;
    }

    /**
     * @param inBetaMode
     *            the inBetaMode to set.
     */
    public static void setInBetaMode(boolean inBetaMode) {
        Server.inBetaMode = inBetaMode;
    }

    /**
     * @return the liveStatistics.
     */
    public static boolean isLiveStatistics() {
        return liveStatistics;
    }

    /**
     * @param liveStatistics
     *            the liveStatistics to set.
     */
    public static void setLiveStatistics(boolean liveStatistics) {
        Server.liveStatistics = liveStatistics;
    }

    /**
     * @return the totalOnlineTime.
     */
    public static Stopwatch getTotalOnlineTime() {
        return totalOnlineTime;
    }

    /**
     * @return the overhead.
     */
    public static Stopwatch getOverhead() {
        return overhead;
    }
}
