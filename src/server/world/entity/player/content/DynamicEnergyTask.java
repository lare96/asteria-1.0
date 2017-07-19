package server.world.entity.player.content;

import server.logic.task.DynamicTask;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager.Skill;

/**
 * Restores run energy for a player.
 * 
 * @author lare96
 */
public class DynamicEnergyTask extends DynamicTask {

    /**
     * The player we are restoring run energy for.
     */
    private Player player;

    /**
     * Construct a new worker.
     * 
     * @param player
     *            the player we are restoring run energy for.
     */
    public DynamicEnergyTask(Player player) {
        super(restorationRate(player), false);
        this.player = player;
    }

    @Override
    public void run() {
        if (player.getNetwork().isDisconnected()) {
            this.cancel();
            return;
        }

        if (player.getRunEnergy() == 100) {
            return;
        }

        this.setCycles(restorationRate(player));

        if (player.getMovementQueue().isMovementDone() || !player.getMovementQueue().isRunPath()) {
            player.incrementRunEnergy();
        }
    }

    /**
     * Calculate the rate of restoration based on your agility level.
     * 
     * @param player
     *            the player we are restoring run energy for.
     */
    private static int restorationRate(Player player) {
        int level = player.getSkills().getTrainable()[Skill.AGILITY.ordinal()].getLevel();

        if (level > 0 && level <= 25) {
            return 7;
        } else if (level > 25 && level <= 50) {
            return 5;
        } else if (level > 50 && level <= 75) {
            return 3;
        } else if (level > 75) {
            return 2;
        }

        return 7;
    }
}
