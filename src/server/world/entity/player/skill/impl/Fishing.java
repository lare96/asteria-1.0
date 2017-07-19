package server.world.entity.player.skill.impl;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.util.Misc;
import server.util.Misc.Rarity;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.item.Item;
import server.world.item.ItemDefinition;

/**
 * Singleton-based class that handles the fishing skill. Has support for a
 * variety of fish with all of the correct tools, and is pretty user-friendly
 * (even allowing the user to edit how rare a certain fish is).
 * 
 * @author lare96
 */
public class Fishing extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Fishing singleton;

    /**
     * All possible tools that can be used to fish with.
     * 
     * @author lare96
     */
    public enum Tools {
        NET(303, 1, -1, 3, 621, Fish.SHRIMP, Fish.ANCHOVY),
        BIG_NET(305, 16, -1, 3, 620, Fish.MACKEREL, Fish.OYSTER, Fish.COD, Fish.BASS, Fish.CASKET),
        FISHING_ROD(307, 5, 313, 1, 622, Fish.SARDINE, Fish.HERRING, Fish.PIKE, Fish.SLIMY_EEL, Fish.CAVE_EEL),
        OILY_FISHING_ROD(1585, 53, 313, 1, 622, Fish.LAVA_EEL),
        FLY_FISHING_ROD(309, 20, 314, 1, 622, Fish.TROUT, Fish.SALMON),
        HARPOON(311, 35, -1, 4, 618, Fish.TUNA, Fish.SWORDFISH, Fish.SHARK),
        LOBSTER_POT(301, 40, -1, 4, 619, Fish.LOBSTER);

        /**
         * The item id of the tool.
         */
        private int id;

        /**
         * The level you need to be to use this tool.
         */
        private int level;

        /**
         * The item needed to fish, -1 for nothing.
         */
        private int needed;

        /**
         * The speed of this tool.
         */
        private int speed;

        /**
         * The animation performed when using this tool.
         */
        private int animation;

        /**
         * All possible fish you can catch with this tool.
         */
        private Fish[] fish;

        /**
         * Default constructor.
         * 
         * @param id
         *            the id.
         * @param level
         *            the level.
         * @param speed
         *            the speed.
         * @param animation
         *            the animation.
         * @param fish
         *            the fish that can be caught with this tool.
         */
        Tools(int id, int level, int needed, int speed, int animation, Fish... fish) {
            this.setId(id);
            this.setLevel(level);
            this.setNeeded(needed);
            this.setSpeed(speed);
            this.setAnimation(animation);
            this.setFish(fish);
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
         * @return the fish.
         */
        public Fish[] getFish() {
            return fish;
        }

        /**
         * @param fish
         *            the fish to set.
         */
        public void setFish(Fish[] fish) {
            this.fish = fish;
        }

        /**
         * @return the needed.
         */
        public int getNeeded() {
            return needed;
        }

        /**
         * @param needed
         *            the needed to set.
         */
        public void setNeeded(int needed) {
            this.needed = needed;
        }

        /**
         * @return the speed.
         */
        public int getSpeed() {
            return speed;
        }

        /**
         * @param speed
         *            the speed to set.
         */
        public void setSpeed(int speed) {
            this.speed = speed;
        }

        /**
         * @return the animation.
         */
        public int getAnimation() {
            return animation;
        }

        /**
         * @param animation
         *            the animation to set.
         */
        public void setAnimation(int animation) {
            this.animation = animation;
        }
    }

    /**
     * Holds data for all the possible things that can be caught while fishing.
     * 
     * @author lare96
     */
    public enum Fish {
        SHRIMP(317, 1, Rarity.VERY_COMMON, 15),
        SARDINE(327, 5, Rarity.VERY_COMMON, 50),
        HERRING(345, 10, Rarity.VERY_COMMON, 90),
        ANCHOVY(321, 15, Rarity.SOMETIMES, 120),
        MACKEREL(353, 16, Rarity.VERY_COMMON, 150),
        CASKET(405, 16, Rarity.ALMOST_IMPOSSIBLE, 7000),
        OYSTER(407, 16, Rarity.EXTREMELY_RARE, 3000),
        TROUT(335, 20, Rarity.VERY_COMMON, 300),
        COD(341, 23, Rarity.VERY_COMMON, 310),
        PIKE(349, 25, Rarity.VERY_COMMON, 325),
        SLIMY_EEL(3379, 28, Rarity.EXTREMELY_RARE, 1000),
        SALMON(331, 30, Rarity.VERY_COMMON, 350),
        TUNA(359, 35, Rarity.VERY_COMMON, 350),
        CAVE_EEL(5001, 38, Rarity.SOMETIMES, 500),
        LOBSTER(377, 40, Rarity.VERY_COMMON, 500),
        BASS(363, 46, Rarity.SOMETIMES, 600),
        SWORDFISH(371, 50, Rarity.COMMON, 600),
        LAVA_EEL(2148, 53, Rarity.VERY_COMMON, 600),
        SHARK(383, 76, Rarity.COMMON, 800);

        /**
         * Id of the fish.
         */
        private int id;

        /**
         * Level to be able to fish this.
         */
        private int level;

        /**
         * Rarity of this fish.
         */
        private Rarity rarity;

        /**
         * Experience from catching this fish.
         */
        private int experience;

        /**
         * Creates data for this fish.
         * 
         * @param id
         *            the id.
         * @param level
         *            the level.
         * @param rarity
         *            the rarity of this fish.
         * @param experience
         *            the experience.
         */
        Fish(int id, int level, Rarity rarity, int experience) {
            this.setId(id);
            this.setLevel(level);
            this.setRarity(rarity);
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
         * @return the rarity.
         */
        public Rarity getRarity() {
            return rarity;
        }

        /**
         * @param rarity
         *            the rarity to set.
         */
        public void setRarity(Rarity rarity) {
            this.rarity = rarity;
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
     * Method that allows a player to begin fishing.
     * 
     * @param player
     *            the player fishing.
     * @param fishingWith
     *            the tool this player is fishing with.
     */
    public void fish(final Player player, final Tools fishingWith) {

        /** If we are already skilling, block. */
        if (player.getSkillingAction()[index()]) {
            reset(player);
            return;
        }

        /** If we do not have the tools, block. */
        if (!player.getInventory().getItemContainer().contains(fishingWith.getId())) {
            return;
        }

        /** If we do not have items needed for the tools, block. */
        if (fishingWith.getNeeded() > 0) {
            if (!player.getInventory().getItemContainer().contains(fishingWith.getNeeded())) {
                player.getServerPacketBuilder().sendMessage("You do not have any " + ItemDefinition.getDefinitions()[fishingWith.getId()].getItemName() + ".");
                return;
            }
        }

        /** If we have no space in our inventory, block. */
        if (player.getInventory().getItemContainer().freeSlots() < 1) {
            player.getServerPacketBuilder().sendMessage("You do not have any space left in your inventory.");
            return;
        }

        /** If we aren't a high enough level to use this tool, block. */
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(fishingWith.getLevel())) {
            player.getServerPacketBuilder().sendMessage("You are not a high enough level to fish with this. You must have a fishing level of " + fishingWith.getLevel() + ".");
            return;
        }

        /** Begin fishing. */
        player.getServerPacketBuilder().sendMessage("You begin to fish...");
        player.getSkillingAction()[index()] = true;
        player.animation(new Animation(fishingWith.getAnimation()));
        player.getMovementQueue().reset();

        GameLogic.getSingleton().submit(new Task(fishingWith.getSpeed() + playerFishingAddition(player), false, Time.TICK) {
            @Override
            public void logic() {
                /** If we are not online, block and stop the task. */
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /** If we do not have the tools, block and stop the task. */
                if (!player.getInventory().getItemContainer().contains(fishingWith.getId())) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If we do not have the items needed for the tools, block and
                 * stop the task.
                 */
                if (fishingWith.getNeeded() > 0) {
                    if (!player.getInventory().getItemContainer().contains(fishingWith.getNeeded())) {
                        player.getServerPacketBuilder().sendMessage("You do not have anymore " + ItemDefinition.getDefinitions()[fishingWith.getId()].getItemName() + ".");
                        reset(player);
                        this.cancel();
                        return;
                    }
                }

                /**
                 * If we have no space in our inventory, block and stop the
                 * task.
                 */
                if (player.getInventory().getItemContainer().freeSlots() < 1) {
                    player.getServerPacketBuilder().sendMessage("You do not have any space left in your inventory.");
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If we aren't a high enough level to use this tool,block and
                 * stop the task.
                 */
                if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(fishingWith.getLevel())) {
                    player.getServerPacketBuilder().sendMessage("You are not a high enough level to fish with this. You must have a fishing level of " + fishingWith.getLevel() + ".");
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If a random integer between 0-15 is equal to 0, block and
                 * stop the task (used for making the player randomly stop
                 * fishing).
                 */
                if (Misc.getRandom().nextInt(15) == 0) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If the skill has been stopped, block, stop the task and
                 * reset.
                 */
                if (!player.getSkillingAction()[index()]) {
                    reset(player);
                    this.cancel();
                    return;
                }

                if (player.getSkillingAction()[index()]) {

                    /**
                     * Get a random fish for us to catch based on rarity and
                     * level.
                     */
                    Fish caught = determineFish(player, fishingWith);

                    /** Catch the fish. */
                    player.getServerPacketBuilder().sendMessage("You catch a " + caught.name().toLowerCase().replace("_", " ") + ".");
                    player.getInventory().addItem(new Item(caught.getId()));
                    exp(player, caught.getExperience());

                    /** Remove item needed for the tools. */
                    if (fishingWith.getNeeded() > 0) {
                        player.getInventory().removeItem(new Item(fishingWith.getNeeded()));
                    }

                    /** Another check for fishing tools. */
                    if (!player.getInventory().getItemContainer().contains(fishingWith.getId())) {
                        reset(player);
                        this.cancel();
                        return;
                    }

                    /** Another check for items needed for tools. */
                    if (fishingWith.getNeeded() > 0) {
                        if (!player.getInventory().getItemContainer().contains(fishingWith.getNeeded())) {
                            player.getServerPacketBuilder().sendMessage("You do not have anymore " + ItemDefinition.getDefinitions()[fishingWith.getId()].getItemName() + ".");
                            reset(player);
                            this.cancel();
                            return;
                        }
                    }

                    /** Another check for inventory space. */
                    if (player.getInventory().getItemContainer().freeSlots() < 1) {
                        player.getServerPacketBuilder().sendMessage("You do not have any space left in your inventory.");
                        reset(player);
                        this.cancel();
                        return;
                    }
                }
            }
        });

        /**
         * Because the fishing animation is based on a strict cycle of 4 ticks,
         * we use a seperate task for the animation.
         */
        GameLogic.getSingleton().submit(new Task(4, true, Time.TICK) {
            @Override
            public void logic() {
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                if (!player.getSkillingAction()[index()]) {
                    cancel();
                    return;
                }

                player.animation(new Animation(fishingWith.getAnimation()));
            }
        });
    }

    /**
     * Determines a random fish to be caught for the player based on fishing
     * level and the rarity of a fish.
     * 
     * @param player
     *            the player fishing.
     * @param fishingWith
     *            the tool this player is fishing with.
     */
    public Fish determineFish(Player player, Tools fishingWith) {

        /** Determine which fish are able to be caught. */
        for (Fish f : fishingWith.getFish()) {
            if (f.getLevel() <= player.getSkills().getTrainable()[skill().ordinal()].getLevel()) {
                player.getFish().add(f);
            }
        }

        /** Get a random fish from that selection. */
        Fish random = (Fish) Misc.randomElement(player.getFish());

        /** Perform rarity check. */
        if (Misc.getRandom().nextInt(100) <= random.getRarity().getPercentage()) {
            return random;
        } else {
            return fishingWith.getFish()[0];
        }
    }

    /**
     * An addition to the fishing timer based on your fishing level.
     * 
     * @param player
     *            the player fishing.
     */
    private int playerFishingAddition(Player player) {
        return (10 - (int) Math.floor(player.getSkills().getTrainable()[skill().ordinal()].getLevel() / 10));
    }

    @Override
    public void reset(Player player) {
        player.getServerPacketBuilder().resetAnimation();
        player.getSkillingAction()[index()] = false;
    }

    @Override
    public int index() {
        return SkillManager.FISHING;
    }

    @Override
    public Skill skill() {
        return Skill.FISHING;
    }

    /**
     * @return the singleton.
     */
    public static Fishing getSingleton() {
        if (singleton == null) {
            singleton = new Fishing();
        }

        return singleton;
    }
}
