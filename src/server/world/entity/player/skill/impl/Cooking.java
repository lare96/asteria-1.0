package server.world.entity.player.skill.impl;

import java.util.Random;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.item.Item;
import server.world.item.ItemDefinition;

/**
 * A child class that handles the cooking skill. This has support for cooking 1,
 * 5, 10, and all, has support for burning formulas with a higher chance of
 * burning on a fire, and the ability to stop burning certain foods at certain
 * levels. A different version of this was released on mopar and rune-server by
 * me, but this version is much better and was re-designed to fit this server
 * specifically.
 * 
 * @author lare96
 */
public class Cooking extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Cooking singleton;

    /**
     * Used to help determine when the player will burn food.
     */
    private static Random cookingRandom = new Random();

    /**
     * Data for all of the foods we are able to cook.
     * 
     * @author lare96
     */
    public enum Cook {
        BONES(526, 99, 528, 1, 528, 5000),
        SHRIMP(317, 1, 315, 34, 323, 50),
        SARDINE(327, 5, 325, 38, 369, 100),
        HERRING(345, 10, 347, 37, 357, 150),
        ANCHOVIES(321, 15, 319, 34, 323, 200),
        MACKEREL(353, 16, 355, 45, 357, 225),
        TROUT(335, 20, 333, 50, 343, 400),
        COD(341, 23, 339, 39, 343, 450),
        PIKE(349, 25, 351, 52, 343, 500),
        SLIMY_EEL(3379, 28, 3381, 56, 3383, 700),
        SALMON(331, 30, 329, 58, 343, 600),
        TUNA(359, 35, 361, 63, 367, 600),
        CAVE_EEL(5001, 38, 5003, 72, 5002, 800),
        LOBSTER(377, 40, 379, 74, 381, 600),
        BASS(363, 46, 365, 80, 367, 650),
        SWORDFISH(371, 50, 373, 86, 375, 950),
        LAVA_EEL(2148, 53, 2149, 89, 3383, 1500),
        SHARK(383, 76, 385, 94, 387, 2000);

        /**
         * Id of raw fish.
         */
        private int id;

        /**
         * Level to cook the raw fish.
         */
        private int level;

        /**
         * Id of cooked fish.
         */
        private int newId;

        /**
         * Level you stop burning this fish at.
         */
        private int burnLevel;

        /**
         * The id of the burnt fish.
         */
        private int burntFish;

        /**
         * The experience you get when you properly cook this fish.
         */
        private int experience;

        /**
         * Creates data for a fish.
         * 
         * @param id
         *            the raw fish.
         * @param level
         *            the level needed to cook the fish.
         * @param newId
         *            the cooked fish.
         * @param burnLevel
         *            the level you stop burning this fish at.
         * @param burntFish
         *            the burnt fish.
         * @param experience
         *            the experience for successfully cooking this fish.
         */
        Cook(int id, int level, int newId, int burnLevel, int burntFish, int experience) {
            this.setId(id);
            this.setLevel(level);
            this.setNewId(newId);
            this.setBurnLevel(burnLevel);
            this.setBurntFish(burntFish);
            this.setExperience(experience);
        }

        /**
         * @return the id.
         */
        public int getId() {
            return id;
        }

        /**
         * @param id
         *            the id to set.
         */
        public void setId(int id) {
            this.id = id;
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

        /**
         * @return the newId.
         */
        public int getNewId() {
            return newId;
        }

        /**
         * @param newId
         *            the newId to set.
         */
        public void setNewId(int newId) {
            this.newId = newId;
        }

        /**
         * @return the burnLevel.
         */
        public int getBurnLevel() {
            return burnLevel;
        }

        /**
         * @param burnLevel
         *            the burnLevel to set.
         */
        public void setBurnLevel(int burnLevel) {
            this.burnLevel = burnLevel;
        }

        /**
         * @return the burntFish.
         */
        public int getBurntFish() {
            return burntFish;
        }

        /**
         * @param burntFish
         *            the burntFish to set.
         */
        public void setBurntFish(int burntFish) {
            this.burntFish = burntFish;
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
    }

    /**
     * Opens the cooking interface.
     * 
     * @param player
     *            the player to open the interface for.
     * @param item
     *            the item to display.
     */
    public void viewCookInterface(Player player, int item) {
        player.getServerPacketBuilder().sendChatInterface(1743);
        player.getServerPacketBuilder().sendItemOnInterface(13716, 190, item);
        player.getServerPacketBuilder().sendString("\\n\\n\\n\\n\\n" + ItemDefinition.getDefinitions()[item].getItemName() + "", 13717);
    }

    /**
     * This method being the main core of this class, cooks the specified food
     * at the specified amount for the subscriber.
     * 
     * @param player
     *            the player cooking.
     * @param cook
     *            the food the player is cooking.
     * @param amount
     *            the amount of food the player is cooking.
     */
    public void cook(final Player player, final Cook cook, final int amount) {

        /** If we are already cooking, stop. */
        if (player.getSkillingAction()[index()]) {
            player.getServerPacketBuilder().sendMessage("You are already cooking!");
            reset(player);
            return;
        }

        /** If we don't have the items in our inventory, stop. */
        if (!player.getInventory().getItemContainer().contains(cook.getId())) {
            player.getServerPacketBuilder().sendMessage("You do not have any " + ItemDefinition.getDefinitions()[cook.getId()].getItemName() + " in your inventory...");
            player.getServerPacketBuilder().closeWindows();
            return;
        }

        /** If we do not have the required cooking level, stop. */
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(cook.getLevel())) {
            player.getServerPacketBuilder().sendMessage("You need a Cooking level of " + cook.getLevel() + " to cook this " + ItemDefinition.getDefinitions()[cook.getId()].getItemName() + ".");
            player.getServerPacketBuilder().closeWindows();
            return;
        }

        /** We are now skilling! */
        player.getSkillingAction()[index()] = true;

        /** Close the cooking interface! */
        player.getServerPacketBuilder().closeWindows();

        /** Stop movement. */
        player.getMovementQueue().reset();

        /** Reset the cooking amount. */
        player.setCookAmount(0);

        /** And start the cooking task :) */
        GameLogic.getSingleton().submit(new Task(4, true, Time.TICK) {
            @Override
            public void logic() {

                /** If we are disconnected, stop the task and reset cooking. */
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If we have reached the amount we are cooking, stop the task
                 * and reset cooking.
                 */
                if (player.getCookAmount() == amount) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If we have ran out of raw food to cook, stop the task and
                 * reset cooking.
                 */
                if (!player.getInventory().getItemContainer().contains(cook.getId())) {
                    player.getServerPacketBuilder().sendMessage("You've ran out of " + ItemDefinition.getDefinitions()[cook.getId()].getItemName() + ".");
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If for some reason we are no longer cooking (walked, dropped
                 * something, equipped, etc.), stop the task and reset cooking.
                 */
                if (!player.getSkillingAction()[index()]) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /** Close the windows. */
                player.getServerPacketBuilder().closeWindows();

                /** Perform animation based on if we are using a stove or not. */
                player.animation(!player.isUsingStove() ? new Animation(897) : new Animation(896));

                /** Cook food with burn formula. */
                if (player.getSkills().getTrainable()[skill().ordinal()].reqLevel(cook.getLevel())) {
                    if (!burn(player, cook)) {
                        exp(player, cook.getExperience());
                        player.getInventory().replaceItemWithItem(new Item(cook.getId(), 1), new Item(cook.getNewId(), 1));
                    } else {
                        player.getInventory().replaceItemWithItem(new Item(cook.getId(), 1), new Item(cook.getBurntFish(), 1));
                    }
                } else {
                    player.getServerPacketBuilder().sendMessage("You need a cooking level of " + cook.getLevel() + " to cook this " + ItemDefinition.getDefinitions()[cook.getId()].getItemName() + ".");
                    reset(player);
                    this.cancel();
                    return;
                }

                player.addCookAmount();
            }
        });
    }

    /**
     * Our burn formula that will be used to calculate whether the food will be
     * burnt or not. Credits to some guy on rune-server.
     * 
     * @param player
     *            the player cooking.
     * @param cook
     *            the food the player is cooking.
     * @param amount
     *            the amount of food the player is cooking.
     * @return true if you burned the food.
     */
    public boolean burn(Player player, Cook cook) {

        /**
         * Change the 55.0 to whatever you want, the lower it goes the less
         * chance of burning.
         */
        double burn_chance = (55.0 - (player.isUsingStove() ? 4.0 : 0.0));
        double cook_level = (double) player.getSkills().getTrainable()[skill().ordinal()].getLevel();
        double lev_needed = (double) cook.getLevel();
        double burn_stop = (double) cook.getBurnLevel();
        double multi_a = (burn_stop - lev_needed);
        double burn_dec = (burn_chance / multi_a);
        double multi_b = (cook_level - lev_needed);
        burn_chance -= (multi_b * burn_dec);

        double randNum = cookingRandom.nextDouble() * 100.0;

        if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() >= cook.getBurnLevel()) {
            player.getServerPacketBuilder().sendMessage("You successfully cook the " + ItemDefinition.getDefinitions()[cook.getId()].getItemName() + ".");
            return false;
        }

        if (burn_chance <= randNum) {
            player.getServerPacketBuilder().sendMessage("You successfully cook the " + ItemDefinition.getDefinitions()[cook.getId()].getItemName() + ".");
            return false;
        } else {
            player.getServerPacketBuilder().sendMessage("Oops! You accidently burn the " + ItemDefinition.getDefinitions()[cook.getId()].getItemName() + ".");
            return true;
        }
    }

    @Override
    public void reset(Player player) {
        player.getServerPacketBuilder().resetAnimation();
        player.setCook(null);
        player.setCookAmount(0);
        player.getSkillingAction()[index()] = false;
    }

    @Override
    public int index() {
        return SkillManager.COOKING;
    }

    @Override
    public Skill skill() {
        return Skill.COOKING;
    }

    /**
     * @return the singleton.
     */
    public static Cooking getSingleton() {
        if (singleton == null) {
            singleton = new Cooking();
        }

        return singleton;
    }
}
