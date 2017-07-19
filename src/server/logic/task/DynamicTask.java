package server.logic.task;

/**
 * A dynamic task that runs on intervals of 600ms (referred to as ticks, or
 * cycles). These are very useful because unlike the standard <code>Task</code>
 * they allow for dynamic runtime changes to the delay. These type of tasks
 * <b>can</b> also be used in instances where a runtime change isn't required,
 * but it is recommended to use the standard <code>Task</code> instead when
 * only basic execution is needed.
 * 
 * @author lare96
 */
public abstract class DynamicTask extends Task {

    /**
     * The amount of ticks this task has to wait for before being executed.
     */
    private int cycles;

    /**
     * The amount of cycles that have passed. We use this to record how many
     * cycles more we need before executing the task.
     */
    private int cyclesPassed;

    /**
     * If this task has executed first.
     */
    private boolean taskExecutedFirst;

    /**
     * Create a new dynamic task.
     * 
     * @param delay
     *            the delay for this task in ticks.
     * @param executeFirst
     *            if this task should execute before being scheduled.
     */
    public DynamicTask(int delay, boolean executeFirst) {
        super(1, executeFirst, Time.TICK);
        this.setCycles(delay);
    }

    /**
     * The logic that will be executed.
     */
    public abstract void run();

    @Override
    public void logic() {

        /**
         * Execute the logic if this task needs to be executed before being ran.
         * If we have already executed the logic or we do not need to execute
         * the logic then proceed as normal.
         */
        if (this.isExecuteFirst() && !this.isTaskExecutedFirst()) {
            run();
            this.setTaskExecutedFirst(true);
            return;
        }

        /** Increase the amount of cycles passed. */
        this.cyclesPassed++;

        /** Check if this task is ready to execute. */
        if (this.isRunning() && this.getCyclesPassed() == this.getCycles()) {

            /**
             * If it is, execute it and reset the amount of cycles needed to
             * execute it.
             */
            this.run();
            this.setCyclesPassed(0);
        }
    }

    /**
     * @return the cycles.
     */
    public int getCycles() {
        return cycles;
    }

    /**
     * @param cycles
     *            the cycles to set.
     */
    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    /**
     * @return the cyclesPassed.
     */
    public int getCyclesPassed() {
        return cyclesPassed;
    }

    /**
     * @param cyclesPassed
     *            the cyclesPassed to set.
     */
    private void setCyclesPassed(int cyclesPassed) {
        this.cyclesPassed = cyclesPassed;
    }

    /**
     * @return the taskExecutedFirst.
     */
    public boolean isTaskExecutedFirst() {
        return taskExecutedFirst;
    }

    /**
     * @param taskExecutedFirst
     *            the taskExecutedFirst to set.
     */
    private void setTaskExecutedFirst(boolean taskExecutedFirst) {
        this.taskExecutedFirst = taskExecutedFirst;
    }
}
