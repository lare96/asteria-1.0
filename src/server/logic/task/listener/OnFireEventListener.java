package server.logic.task.listener;

import server.logic.task.Task;

/**
 * These type of tasks act as listeners that fire logic until some sort of event
 * is true-flagged.
 * 
 * @author lare96
 */
public abstract class OnFireEventListener extends Task {

    /**
     * Create a new listener ready to listen until an event is true-flagged.
     */
    public OnFireEventListener() {
        super(1, false, Time.TICK);
    }

    /**
     * Will keep firing the logic until this is condition is true-flagged.
     * 
     * @return if the condition is true or false.
     */
    public abstract boolean fireLogicUntil();

    /**
     * The actual logic that will be fired until the event is true-flagged.
     */
    public abstract void run();

    @Override
    public void logic() {

        /** Check if the event has been true-flagged. */
        if (this.fireLogicUntil()) {

            /** If so stop firing logic and shutdown. */
            this.cancel();
            return;
        }

        /** Fire the logic otherwise. */
        this.run();
    }
}
