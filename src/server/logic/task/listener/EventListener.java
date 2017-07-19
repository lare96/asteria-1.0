package server.logic.task.listener;

import server.logic.task.Task;

/**
 * These type of tasks act as listeners that fire logic after some sort of event
 * is false-flagged.
 * 
 * @author lare96
 */
public abstract class EventListener extends Task {

    /**
     * Determines if the listener should be shut down once the logic has fired.
     */
    private boolean shutdownOnFire = true;

    /**
     * Create a new listener ready to listen for an event.
     * 
     * @param shutdownOnFire
     *            if the listener should be shut down once the logic has fired.
     */
    public EventListener(boolean shutdownOnFire) {
        super(1, false, Time.TICK);
        this.setShutdownOnFire(shutdownOnFire);
    }

    /**
     * Create a new listener ready to listen for an event.
     */
    public EventListener() {
        super(1, false, Time.TICK);
    }

    /**
     * Will block from firing the logic until this condition is false-flagged.
     * 
     * @return if the condition is true or false.
     */
    public abstract boolean listenForEvent();

    /**
     * The actual logic that will be fired once the event has been
     * false-flagged.
     */
    public abstract void run();

    @Override
    public void logic() {

        /** Block if the event has not yet occurred. */
        if (this.listenForEvent()) {
            return;
        }

        /** Fire the logic once the event has occurred. */
        this.run();

        /**
         * Shutdown the listener once the task has been ran if it is set to do
         * so, if not it will keep listening and will execute the logic every
         * 600ms (as long as the condition is flagged as false).
         */
        if (this.isShutdownOnFire()) {
            this.cancel();
        }
    }

    /**
     * @return the shutdownOnFire.
     */
    public boolean isShutdownOnFire() {
        return shutdownOnFire;
    }

    /**
     * @param shutdownOnFire
     *            the shutdownOnFire to set.
     */
    private void setShutdownOnFire(boolean shutdownOnFire) {
        this.shutdownOnFire = shutdownOnFire;
    }
}
