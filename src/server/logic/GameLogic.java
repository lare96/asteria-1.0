package server.logic;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.Server;
import server.logic.task.Task;

/**
 * Contains an executor that uses worker threads to carry out various tasks
 * throughout the server.
 * 
 * @author lare96
 */
public final class GameLogic {

    /**
     * The singleton instance.
     */
    private static GameLogic singleton;

    /**
     * A ScheduledExecutorService that schedules and carries out delayed logic.
     */
    private static final ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Creates and submits a new task to be scheduled and later executed by an
     * idle worker thread.
     * 
     * @param task
     *            the task being submitted.
     */
    public Task submit(final Task task) {

        /** Tasks must have a positive delay. */
        if (task.getDelay() < 1) {
            throw new IllegalArgumentException("Task must have a positive delay!");
        }

        /** Execute the logic within the task before scheduling if we need too. */
        if (task.isExecuteFirst()) {
            synchronized (this) {
                try {
                    task.logic();
                } catch (Exception e) {
                    Server.print("Error during task execution!");
                    e.printStackTrace();
                }
            }
        }

        /** Do not schedule the task if it was stopped. */
        if (!task.isStoppedOnFirstExecution()) {

            /**
             * Begin scheduling and wrap the ScheduledFuture within the task.
             */
            task.setFuture(taskExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    synchronized (this) {
                        try {
                            task.logic();
                        } catch (Exception e) {
                            Server.print("Error during task execution!");
                            e.printStackTrace();
                        }
                    }
                }
            }, task.getDelay() * task.getTimeUnit().getTime(), task.getDelay() * task.getTimeUnit().getTime(), TimeUnit.MILLISECONDS));
        }
        return task;
    }

    /**
     * Attempts to stop all tasks that are currently running. The
     * <code>taskExecutor</code> will no longer accept tasks after this method
     * is invoked.
     */
    public void shutdown() {
        taskExecutor.shutdownNow();
    }

    /**
     * @return the singleton.
     */
    public static GameLogic getSingleton() {
        if (singleton == null) {
            singleton = new GameLogic();
        }

        return singleton;
    }
}