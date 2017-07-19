package server.logic.task;

import java.util.concurrent.ScheduledFuture;

import server.logic.Logic;

/**
 * These are delayed tasks in their most primitive form. They are created to
 * carry out and execute very basic logic based on a set of conditions. Each
 * task is wrapped with its own individual future which can be accessed through
 * this container. These type of tasks are very flexible and can be used in just
 * about any general situation. It is recommended to create new implementations
 * of this task when more specific logic needs to be carried out.
 * 
 * @author lare96
 */
public abstract class Task implements Logic {

    /**
     * The delay in the time unit specified.
     */
    private int delay;

    /**
     * The <code>ScheduledFuture</code> object assigned to this task.
     */
    private ScheduledFuture<?> future;

    /**
     * The task executor for this task.
     */
    private ControlledTaskExecutor<? extends Task> taskExecutor;

    /**
     * If the task should execute first before being scheduled.
     */
    private boolean executeFirst;

    /**
     * If the task was stopped on the first execution.
     */
    private boolean stoppedOnFirstExecution;

    /**
     * The time unit this task is using.
     */
    private Time timeUnit;

    /**
     * The time units and their respective delays in milliseconds.
     * 
     * @author lare96
     */
    public enum Time {
        TICK(600), SECOND(1000), MINUTE(60000), HOUR(3600000), DAY(86400000);

        /**
         * The milliseconds in this time unit.
         */
        private int time;

        /**
         * Create a new time unit.
         * 
         * @param time
         *            the milliseconds in this time unit.
         */
        Time(int time) {
            this.setTime(time);
        }

        /**
         * @return the time.
         */
        public int getTime() {
            return time;
        }

        /**
         * @param time
         *            the time to set.
         */
        public void setTime(int time) {
            this.time = time;
        }
    }

    /**
     * Create a new task to carry out a logic.
     * 
     * @param delay
     *            the delay of this task.
     * @param executeFirst
     *            if this task should execute the logic once before scheduling
     *            it.
     * @param timeUnit
     *            the time unit this task is running on.
     */
    public Task(int delay, boolean executeFirst, Time timeUnit) {
        this.setDelay(delay);
        this.setExecuteFirst(executeFirst);
        this.setTimeUnit(timeUnit);
    }

    /**
     * The logic that will be carried out by the task.
     */
    public abstract void logic();

    /**
     * Cancel the task using the <code>ScheduledFuture</code> object. It is
     * extremely crucial to cancel tasks once they are completed to ensure that
     * unnecessary stress isn't being put on the <code>taskExecutor</code>.
     */
    public void cancel() {

        /** Block if this task doesn't have a <code>ScheduledFuture</code>. */
        if (this.getFuture() == null) {
            this.setStoppedOnFirstExecution(true);
            return;
        }

        /** Throw an exception if this task has already been canceled. */
        if (!this.isRunning()) {
            throw new IllegalStateException("Task already cancelled!");
        }

        /** Remove this task from its executor if needed. */
        if (this.isControlledTask()) {
            if (!this.getTaskExecutor().isReset()) {
                this.getTaskExecutor().remove(this);
            }
        }

        /** Finally cancel the task. */
        this.getFuture().cancel(true);
    }

    /**
     * @return the delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @param delay
     *            the delay to set.
     */
    private void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * @return the running.
     */
    public boolean isRunning() {
        if (this.getFuture() == null) {
            return true;
        }

        return !this.getFuture().isCancelled();
    }

    /**
     * @return the worker.
     */
    private ScheduledFuture<?> getFuture() {
        return future;
    }

    /**
     * @param future
     *            the furue to set.
     */
    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }

    /**
     * @return the executeFirst.
     */
    public boolean isExecuteFirst() {
        return executeFirst;
    }

    /**
     * @param executeFirst
     *            the executeFirst to set.
     */
    private void setExecuteFirst(boolean executeFirst) {
        this.executeFirst = executeFirst;
    }

    /**
     * @return the timeUnit.
     */
    public Time getTimeUnit() {
        return timeUnit;
    }

    /**
     * Set a new time unit.
     */
    private void setTimeUnit(Time timeUnit) {
        this.timeUnit = timeUnit;
    }

    /**
     * @return the stoppedOnFirstExecution.
     */
    public boolean isStoppedOnFirstExecution() {
        return stoppedOnFirstExecution;
    }

    /**
     * @param stoppedOnFirstExecution
     *            the stoppedOnFirstExecution to set.
     */
    private void setStoppedOnFirstExecution(boolean stoppedOnFirstExecution) {
        this.stoppedOnFirstExecution = stoppedOnFirstExecution;
    }

    /**
     * @return true if this task is controlled by an executor.
     */
    public boolean isControlledTask() {
        return !(taskExecutor == null);
    }

    /**
     * @return the taskExecutor.
     */
    protected ControlledTaskExecutor<? extends Task> getTaskExecutor() {
        if (taskExecutor == null) {
            throw new IllegalStateException("This task does not have a ControlledTaskExecutor!");
        }

        return taskExecutor;
    }

    /**
     * @param taskExecutor
     *            the taskExecutor to set.
     */
    protected void setTaskExecutor(ControlledTaskExecutor<? extends Task> taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
