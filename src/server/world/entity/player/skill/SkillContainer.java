package server.world.entity.player.skill;

import server.world.entity.player.skill.SkillManager.Skill;

/**
 * A container class that holds a full set of skills and utility methods for
 * those skills.
 * 
 * @author lare96
 */
public class SkillContainer {

    /**
     * An array of all the trainable skills.
     */
    private Trainable[] trainable = new Trainable[21];

    /**
     * The current players combat level.
     */
    private double combatLevel;

    /**
     * Calculates this players combat level.
     * 
     * @return the players combat level.
     */
    public int getCombatLevel() {
        double mag = trainable[Skill.MAGIC.ordinal()].getLevelForExperience() * 1.5;
        double ran = trainable[Skill.RANGED.ordinal()].getLevelForExperience() * 1.5;
        double attstr = trainable[Skill.ATTACK.ordinal()].getLevelForExperience() + trainable[Skill.STRENGTH.ordinal()].getLevelForExperience();

        combatLevel = 0;

        if (ran > attstr) {
            combatLevel = ((trainable[Skill.DEFENCE.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[Skill.HITPOINTS.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[Skill.PRAYER.ordinal()].getLevelForExperience()) * 0.125) + ((trainable[Skill.RANGED.ordinal()].getLevelForExperience()) * 0.4875);
        } else if (mag > attstr) {
            combatLevel = (((trainable[Skill.DEFENCE.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[Skill.HITPOINTS.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[Skill.RANGED.ordinal()].getLevelForExperience()) * 0.125) + ((trainable[Skill.MAGIC.ordinal()].getLevelForExperience()) * 0.4875));
        } else {
            combatLevel = (((trainable[Skill.DEFENCE.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[Skill.HITPOINTS.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[Skill.PRAYER.ordinal()].getLevelForExperience()) * 0.125) + ((trainable[Skill.ATTACK.ordinal()].getLevelForExperience()) * 0.325) + ((trainable[Skill.STRENGTH.ordinal()].getLevelForExperience()) * 0.325));
        }

        return (int) combatLevel;
    }

    /**
     * @return the trainable skills.
     */
    public Trainable[] getTrainable() {
        return trainable;
    }

    /**
     * @param trainable
     *            the trainable to set.
     */
    public void setTrainable(Trainable[] trainable) {
        this.trainable = trainable;
    }
}
