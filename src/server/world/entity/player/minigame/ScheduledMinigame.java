package server.world.entity.player.minigame;

import server.logic.task.Task;

/**
 * Useful for implementation of a scheduled minigame that runs on ticks, like
 * pest control and fight pits.
 */
public abstract class ScheduledMinigame extends Minigame {

    /**
     * The task that will run this minigame.
     * 
     * @return the task.
     */
    public abstract Task task();
}
