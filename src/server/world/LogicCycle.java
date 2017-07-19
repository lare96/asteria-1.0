package server.world;

import server.Server;
import server.logic.task.Task;
import server.net.event.NetworkEventDispatcher;
import server.util.ServerGUI;

/**
 * The core of the server where all tickable logic is executed.
 * 
 * @author lare96
 */
public final class LogicCycle extends Task {

    /**
     * Creates a new task to carry out tickable logic.
     */
    public LogicCycle() {
        super(1, true, Time.TICK);
    }

    @Override
    public void logic() {

        /** Reset the overhead timer at the beginning of the cycle. */
        Server.getOverhead().reset();

        /** First dispatch network events. */
        NetworkEventDispatcher.getSingleton().dispatch();

        /** Next execute game logic. */
        try {
            World.process();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        /** Next execute the updating of the GUI if needed. */
        if (Server.isLiveStatistics()) {
            try {
                ServerGUI.getProgressBar().setValue((Server.getCycleTimeOverhead() / 10) > 100 ? 100 : (int) (Server.getCycleTimeOverhead() / 10));
                ServerGUI.getProgressBar().setString("Server Load: " + (Server.getCycleTimeOverhead() / 10.0) + "%");
                ServerGUI.getStatisticsTable().setValueAt(Thread.activeCount() + " threads currently active", 0, 1);
                ServerGUI.getStatisticsTable().setValueAt(Server.getCycleTimeOverhead() + " milliseconds", 1, 1);
                ServerGUI.getStatisticsTable().setValueAt(World.playerAmount() + " players currently online", 2, 1);
                ServerGUI.getStatisticsTable().setValueAt((Server.getTotalOnlineTime().elapsed() / 1000) + " seconds", 3, 1);
                ServerGUI.getStatisticsTable().setValueAt(((Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory()) / 1048576) + " megabytes of memory", 4, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /** ... Set the elapsed time. */
        Server.setCycleTimeOverhead(Server.getOverhead().elapsed());

        /** And finally, print a warning message if needed. */
        if (Server.getCycleTimeOverhead() > 600) {
            Server.print("Server under stress! [tick: " + Server.getCycleTimeOverhead() + "]");
        }
    }
}
