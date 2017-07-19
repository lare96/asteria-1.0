package server.world.entity.player.skill;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager.Skill;

/**
 * A parent class that handles the main functions of non-combat skills.
 * 
 * @author lare96
 */
public abstract class TrainableSkill {

    /**
     * List of all the instanced skills.
     */
    private static List<TrainableSkill> skills = new ArrayList<TrainableSkill>();

    /**
     * Reset and stop the skill.
     * 
     * @param player
     *            the player to reset the skill for.
     */
    public abstract void reset(Player player);

    /**
     * Gets the value of the index in the players <code>skillingAction</code>
     * array.
     * 
     * @return the value of the index in the players <code>skillingAction</code>
     *         array.
     */
    public abstract int index();

    /**
     * An instance of the skill itself.
     * 
     * @return the instance of the skill.
     */
    public abstract Skill skill();

    /**
     * Loads all skills automatically by creating a new instance of each class
     * in the <code>server.world.entity.player.skill.impl</code> and adding it
     * to a list.
     */
    public static void load() {
        // int parsed = 0;

        File[] files = new File("./src/server/world/entity/player/skill/impl").listFiles();

        for (File f : files) {
            if (f == null) {
                continue;
            }

            try {
                try {
                    try {
                        Object o = Class.forName("server.world.entity.player.skill.impl." + f.getName().replace(".java", "")).newInstance();

                        if (o instanceof TrainableSkill) {
                            TrainableSkill skill = (TrainableSkill) o;

                            skills.add(skill);
                            // parsed++;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

        // Server.print("Instantiated " + parsed + " new dialogues!");
    }

    /**
     * Add exp for the specified skill.
     * 
     * @param player
     *            the player to add exp for.
     * @param amount
     *            the amount of exp to add.
     */
    public void exp(Player player, int amount) {
        SkillManager.getSingleton().addExperience(amount, skill(), player);
    }

    /**
     * Static utility method that checks a player for any skill needs to be
     * reset, and does so if needed.
     * 
     * @param player
     *            the player to check for.
     */
    public static void check(Player player) {
        for (TrainableSkill skill : skills) {
            if (skill == null) {
                continue;
            }

            if (player.getSkillingAction()[skill.index()]) {
                skill.reset(player);
            }
        }
    }
}
