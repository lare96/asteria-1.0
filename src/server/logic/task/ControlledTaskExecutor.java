package server.logic.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import server.logic.GameLogic;

/**
 * Carries out delayed tasks in a controlled environment where tasks that have
 * been submitted are stored and can be managed easily during runtime.
 * 
 * @author lare96
 * @param <T>
 *            the type of task in this executor (if you want an executor to only
 *            manage a certain type of task).
 */
public class ControlledTaskExecutor<T extends Task> {

    /**
     * The list of tasks that are currently active and have been submitted to
     * this executor.
     */
    private List<T> tasks = new ArrayList<T>();

    /**
     * If this executor is currently resetting. We need this flag to ensure that
     * no internal tasks are being modified while this executor is being reset.
     */
    private boolean reset;

    /**
     * Submits a task to this executor.
     * 
     * @param task
     *            the task to submit.
     * @return the instance of the task that was submitted.
     */
    public T submit(T task) {

        /** Malformed tasks cannot be submitted. */
        if (task == null) {
            throw new IllegalArgumentException("Cannot submit a malformed task!");
        }

        /** Tasks must have a positive delay. */
        if (task.getDelay() < 1) {
            throw new IllegalArgumentException("Task must have a positive delay!");
        }

        /** Sets this tasks executor. */
        task.setTaskExecutor(this);

        /** Adds this task to the internal list. */
        tasks.add(task);

        /** Schedules this task. */
        GameLogic.getSingleton().submit(task);

        /** Returns an instance of the task submitted. */
        return task;
    }

    /**
     * Removes a task from the internal list.
     * 
     * @param task
     *            the task to remove.
     */
    protected void remove(Task task) {

        /** We cannot remove a task that doesn't exist. */
        if (!tasks.contains(task)) {
            throw new IllegalArgumentException("Task does not exist!");
        }

        /** Remove the task from the internal list. */
        tasks.remove(task);
    }

    /**
     * Stops all active tasks and removes them from the internal list.
     */
    public void reset() {

        /** There are no tasks to remove. */
        if (tasks.isEmpty()) {
            throw new IllegalStateException("There are no tasks running!");
        }

        /** Flag this executor as resetting. */
        this.setReset(true);

        /**
         * Iterate through all of the currently running tasks, stop and remove
         * them.
         */
        for (Iterator<T> iterator = tasks.iterator(); iterator.hasNext();) {
            T t = iterator.next();

            if (t.isRunning()) {
                t.cancel();
                iterator.remove();
            }
        }

        /** Flag this executor as not resetting. */
        this.setReset(false);
    }

    /**
     * Gets the task at the specified index.
     * 
     * @param index
     *            the index to retrieve the task at.
     * @return the task.
     */
    public T getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Gets the amount of tasks in this executor.
     * 
     * @return the amount of tasks.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Gets the index of a task in the internal list (if it exists). If no such
     * task exists -1 is returned.
     * 
     * @param task
     *            the task to get the index of.
     * @return the index of the task, -1 if the task does not exist.
     */
    public int indexOf(T task) {

        /** Loop through the active tasks. */
        for (int i = 0; i < tasks.size(); i++) {

            /** Gets the task on this index. */
            T indexTask = getTask(i);

            /**
             * If the task on this index is equal to the task in the parameter
             * return the index.
             */
            if (indexTask.equals(task) || indexTask == task) {
                return i;
            }
        }

        /** If no match was found return -1. */
        return -1;
    }

    /**
     * Checks if the internal list contains this task.
     * 
     * @param task
     *            the task to check the list for.
     * @return true if the list contains the task.
     */
    public boolean contains(T task) {
        return indexOf(task) != -1;
    }

    /**
     * @return the reset.
     */
    public boolean isReset() {
        return reset;
    }

    /**
     * @param reset
     *            the reset to set.
     */
    private void setReset(boolean reset) {
        this.reset = reset;
    }
}
