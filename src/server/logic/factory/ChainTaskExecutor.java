package server.logic.factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;

/**
 * Chain executors are a queue of runnable tasks that can be ran in first in
 * first out order. When a task is executed the next task waits for its
 * specified delay and then executes, and so on (like a chain) until the end is
 * reached and the executor automatically shuts down (and can be re-ran after).
 * Chain executors can be set to delay the execution of tasks in ticks, seconds,
 * minutes, hours, or even days! <br>
 * <br>
 * 
 * Chain executors can prove to be extremely useful when doing things that
 * require a lengthy amount of precisely executed tasks in a specific order.
 * This is because it only uses <b>one</b> worker to run however many tasks it
 * needs to run (rather than having the same amount of workers as there are
 * tasks, which can be a lot!). The benefits of this are: less stress on the
 * <code>taskExecutor</code> and more precise task execution. <br>
 * <br>
 * 
 * An example of usage is provided below: <br>
 * <br>
 * <br>
 * 
 * 
 * If you had this chain executor and following tasks appended:
 * 
 * <pre>
 * ChainTaskExecutor executor = new ChainTaskExecutor(&quot;our-task-executor&quot;, Time.SECONDS);
 * 
 * executor.append(new ChainTask() {
 *     ... // lets just say the delay was 3 and it printed &quot;Hello world!&quot;.
 * });
 * 
 * And
 * 
 * executor.append(new ChainTask() {
 *      ... // lets just say the delay was 5 and it printed &quot;Goodbye world!&quot;.
 * });
 * </pre>
 * 
 * If you ran the executor using <code>executor.run()</code> it would result
 * in:
 * 
 * <pre>
 * ... delay for three seconds
 * 
 * print &quot;Hello world!&quot;
 * 
 * ... delay for five seconds
 * 
 * print &quot;Goodbye world!&quot;
 * </pre>
 * 
 * And the executor would shut down, allowing for more tasks to be appended to
 * the internal queue and the chance to be ran again.
 * 
 * @author lare96
 */
public class ChainTaskExecutor {

    /**
     * Queue of internal tasks in this chain executor.
     */
    private Queue<ChainTask> internalTasks = new LinkedList<ChainTask>();

    /**
     * A temporary queue of tasks that will be use for polling operations.
     */
    private Queue<ChainTask> tasks = new LinkedList<ChainTask>();

    /**
     * The name of this chain executor.
     */
    private String name = "chain-executor";

    /**
     * If this chain executor is running.
     */
    private boolean runningExecutor;

    /**
     * If the internal queue should be emptied on cancellation or completion.
     */
    private boolean shouldEmpty;

    /**
     * The time unit this chain executor is running on.
     */
    private Time time = Time.TICK;

    /**
     * The amount of delays passed.
     */
    private int delayPassed;

    /**
     * Create a new chain executor.
     * 
     * @param name
     *            the name desired for this chain executor.
     */
    public ChainTaskExecutor(String name) {
        this.setName(name);
    }

    /**
     * Create a new chain executor.
     * 
     * @param time
     *            the time unit desired for this chain executor.
     */
    public ChainTaskExecutor(Time time) {
        this.setTime(time);
    }

    /**
     * Create a new chain executor.
     * 
     * @param name
     *            the name desired for this chain executor.
     * @param time
     *            the time unit desired for this chain executor.
     */
    public ChainTaskExecutor(String name, Time time) {
        this.setName(name);
        this.setTime(time);
    }

    /**
     * Create a new chain executor with the default settings.
     */
    public ChainTaskExecutor() {

    }

    /**
     * Runs this chain executor by using a single delayed task to schedule and
     * execute the entire chain. Once the chain executor is ran, no new tasks
     * can be appended to the internal queue unless the chain executor is
     * canceled or shutdown.
     */
    public void run() {

        /** Makes sure we aren't running an empty executor. */
        if (this.getInternalTasks().isEmpty()) {
            throw new IllegalStateException("[" + this.getName() + "]: Empty task executors cannot be ran!");
        }

        /** Sets the flag that determines if this chain executor is running. */
        this.setRunningExecutor(true);

        /** Sets the temporary tasks to the internal queue. */
        this.getTasks().addAll(this.getInternalTasks());

        /** Schedules all of the temporary tasks in chronological order. */
        GameLogic.getSingleton().submit(new Task(1, false, this.getTime()) {
            @Override
            public void logic() {

                /** Shutdown if this executor has been canceled. */
                if (!isRunningExecutor()) {
                    this.cancel();
                    shutdown();
                    return;
                }

                /** Retrieves the next task in this chain without removing it. */
                ChainTask e = getTasks().peek();

                /**
                 * If a task exists, check if it is ready for execution. If the
                 * task is ready to be executed do so and remove the task from
                 * the chain.
                 */
                if (e != null) {
                    delayPassed++;

                    if (delayPassed == e.delay() && isRunningExecutor()) {
                        e.run();
                        getTasks().remove();
                        delayPassed = 0;
                    }

                    /**
                     * If a task does not exist, the chain has finished and
                     * therefore will shutdown.
                     */
                } else {
                    this.cancel();
                    shutdown();
                    return;
                }
            }
        });
    }

    /**
     * Cancels and shuts down this chain executor during runtime. It is
     * recommended that you avoid prematurely canceling chain executors and wait
     * for them to complete and shutdown. The <code>shutdown()</code> method
     * is called shortly after this is invoked.
     */
    public void cancel() {

        /**
         * Make sure this executor isn't already canceled or shutdown before
         * canceling.
         */
        if (!this.isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: You cannot cancel an executor which has already been shutdown!");
        }

        /** Cancels this executor. */
        this.setRunningExecutor(false);
    }

    /**
     * Completely shuts this executor down. This allows it to accept more tasks
     * and may clear the internal queue depending on the conditions set.
     */
    private void shutdown() {

        /** Cancels this chain executor. */
        this.setRunningExecutor(false);

        /**
         * Empties the internal queue depending on if the condition was set.
         */
        if (this.isShouldEmpty()) {
            this.getInternalTasks().clear();
        }

        /** Clears the temporary tasks. */
        this.getTasks().clear();
    }

    /**
     * Append a new task to the executor's chain.
     * 
     * @param task
     *            the task to append to the chain.
     */
    public void append(ChainTask task) {

        /** Make sure this executor isn't running. */
        if (this.isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add task to a running executor!");
        }

        /** Make sure the task being appended isn't malformed. */
        if (task == null) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add malformed task with a value of null to a executor!");
        }

        /** Make sure the task being appended has a positive delay. */
        if (task.delay() < 1) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add task with a delay value of below 1 to a executor!");
        }

        /** Append the new task to the chain. */
        this.getInternalTasks().add(task);
    }

    /**
     * Append new tasks to the executor's chain.
     * 
     * @param tasks
     *            the tasks to append to the chain.
     */
    public void appendAll(Collection<? extends ChainTask> tasks) {

        /** Make sure this executor isn't running. */
        if (this.isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add tasks to a running executor!");
        }

        /** Make sure the tasks being appended aren't malformed. */
        if (tasks == null || tasks.contains(null)) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add malformed tasks with a value of null to a executor!");
        }

        /** Make sure the tasks being appended have a positive delay. */
        for (ChainTask e : tasks) {
            if (e.delay() < 1) {
                throw new IllegalStateException("[" + this.getName() + "]: Cannot add tasks with delay values of below 1 to a executor!");
            }
        }

        /** Append the new tasks to the chain. */
        this.getInternalTasks().addAll(tasks);
    }

    /**
     * Append new tasks to the executor's chain.
     * 
     * @param tasks
     *            the tasks to append to the chain.
     */
    public void appendAll(ChainTask[] tasks) {

        /** Make sure this executor isn't running. */
        if (this.isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add tasks to a running executor!");
        }

        for (ChainTask e : tasks) {

            /** Make sure the tasks being appended aren't malformed. */
            if (e == null) {
                throw new IllegalStateException("[" + this.getName() + "]: Cannot add malformed tasks with a value of null to a executor!");
            }

            /** Make sure the tasks being appended have a positive delay. */
            if (e.delay() < 1) {
                throw new IllegalStateException("[" + this.getName() + "]: Cannot add tasks with delay values of below 1 to a executor!");
            }

            /** Append the new tasks to the chain. */
            this.getInternalTasks().add(e);
        }
    }

    /**
     * @return the internalTasks.
     */
    private Queue<ChainTask> getInternalTasks() {
        return internalTasks;
    }

    /**
     * @return the tasks.
     */
    private Queue<ChainTask> getTasks() {
        return tasks;
    }

    /**
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the shouldEmpty.
     */
    public boolean isShouldEmpty() {
        return shouldEmpty;
    }

    /**
     * @param shouldEmpty
     *            the shouldEmpty to set.
     */
    public void setShouldEmpty(boolean shouldEmpty) {
        this.shouldEmpty = shouldEmpty;
    }

    /**
     * @return the time.
     */
    public Time getTime() {
        return time;
    }

    /**
     * @param time
     *            the time to set.
     */
    private void setTime(Time time) {
        this.time = time;
    }

    /**
     * @return the runningExecutor.
     */
    public boolean isRunningExecutor() {
        return runningExecutor;
    }

    /**
     * @param runningExecutor
     *            the runningExecutor to set.
     */
    private void setRunningExecutor(boolean runningExecutor) {
        this.runningExecutor = runningExecutor;
    }
}
