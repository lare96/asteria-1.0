package server.world.entity.player.skill;

/**
 * Holds experience and a level for a single trainable skill.
 * 
 * @author lare96
 */
public class Trainable {

    /**
     * The current level.
     */
    private int level;

    /**
     * The current experience.
     */
    private int experience;

    /**
     * Creates a new trainable skill.
     */
    public Trainable() {
        this.setLevel(1);
        this.setExperience(0);
    }

    /**
     * @return your level based on the amount of experience you have.
     */
    public int getLevelForExperience() {
        int points = 0;
        int output = 0;

        for (int lvl = 1; lvl <= 99; lvl++) {
            points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
            output = (int) Math.floor(points / 4);
            if (output >= this.getExperience())
                return lvl;
        }
        return 99;
    }

    /**
     * @return your experience based on the next level you have.
     */
    public int getExperienceForNextLevel() {
        int points = 0;
        int output = 0;

        for (int lvl = 1; lvl <= (this.getLevel() + 1); lvl++) {
            points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
            if (lvl >= (this.getLevelForExperience() + 1))
                return output;
            output = (int) Math.floor(points / 4);
        }

        return 0;
    }

    /**
     * Gets if your level is equal too or above the number provided.
     * 
     * @param level
     *            the level to compare yours to.
     * @return true if your level is above the level provided.
     */
    public boolean reqLevel(int level) {
        return this.getLevel() >= level ? true : false;
    }

    /**
     * Increments this level by the speicified amount.
     * 
     * @param amount
     *            the amount to increase by.
     */
    public void increaseLevel(int amount) {
        if ((level + amount) > 120) {
            level = 120;
            return;
        }

        level += amount;
    }

    /**
     * Decrements this level by the speicified amount.
     * 
     * @param amount
     *            the amount to decrease by.
     */
    public void decreaseLevel(int amount) {
        if ((level - amount) < 0) {
            level = 0;
            return;
        }

        level -= amount;
    }

    /**
     * @return the experience.
     */
    public int getExperience() {
        return experience;
    }

    /**
     * @param experience
     *            the experience to set.
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    /**
     * @return the level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level
     *            the level to set.
     */
    public void setLevel(int level) {
        this.level = level;
    }
}
