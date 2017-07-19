package server.world.entity.player.skill.impl;

import java.util.ArrayList;
import java.util.List;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.item.Item;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * Class which handles everything to do with the woodcutting skill. This
 * supports multiple players cutting on the same tree, being able to get more
 * than one log from every tree except normal, and tree respawning with stumps
 * (not all correct).
 * 
 * @author lare96
 */
public class Woodcutting extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Woodcutting singleton;

    /**
     * A list of the positions of the stumps currently placed throughout the
     * world.
     */
    private static List<Position> stumps = new ArrayList<Position>();

    /**
     * Holds data for all of the possible trees that can be cut.
     * 
     * @author lare96
     */
    public enum Tree {
        NORMAL(1, 1511, 10, 100, 1, 2, new StumpObject[] { new StumpObject(1276, 1342), new StumpObject(1277, 1341), new StumpObject(1278, 1342), new StumpObject(1279, 1341), new StumpObject(1280, 1341), new StumpObject(1282, 1347), new StumpObject(1283, 1347), new StumpObject(1284, 1350), new StumpObject(1285, 1341), new StumpObject(1286, 1352), new StumpObject(1287, 1341), new StumpObject(1288, 1341), new StumpObject(1289, 1352), new StumpObject(1290, 1341), new StumpObject(1291, 1352), new StumpObject(1301, 1341), new StumpObject(1303, 1341), new StumpObject(1304, 1341), new StumpObject(1305, 1341), new StumpObject(1318, 1355), new StumpObject(1319, 1355), new StumpObject(1315, 1342), new StumpObject(1316, 1355), new StumpObject(1330, 1355), new StumpObject(1331, 1355), new StumpObject(1332, 1355), new StumpObject(1333, 1341), new StumpObject(1383, 1341), new StumpObject(1384, 1352), new StumpObject(2409, 1341), new StumpObject(2447, 1341), new StumpObject(2448, 1341), new StumpObject(3033, 1341), new StumpObject(3034, 1341), new StumpObject(3035, 1341), new StumpObject(3036, 1341), new StumpObject(3879, 1341), new StumpObject(3881, 1341), new StumpObject(3883, 1341), new StumpObject(3893, 1341), new StumpObject(3885, 1341), new StumpObject(3886, 1341), new StumpObject(3887, 1341), new StumpObject(3888, 1341), new StumpObject(3892, 1341), new StumpObject(3889, 1341), new StumpObject(3890, 1341), new StumpObject(3891, 1341), new StumpObject(3928, 1341), new StumpObject(3967, 1341), new StumpObject(3968, 1341), new StumpObject(4048, 1341), new StumpObject(4049, 1341), new StumpObject(4050, 1341), new StumpObject(4051, 1341), new StumpObject(4052, 1341), new StumpObject(4053, 1341), new StumpObject(4054, 1341), new StumpObject(4060, 1341), new StumpObject(5004, 1341), new StumpObject(5005, 1341), new StumpObject(5045, 1341), new StumpObject(5902, 1341), new StumpObject(5903, 1341), new StumpObject(5904, 1341), new StumpObject(8973, 1341), new StumpObject(8974, 1341) }),
        OAK(15, 1521, 15, 250, 7, 3, new StumpObject[] { new StumpObject(1281, 1356), new StumpObject(3037, 1341), new StumpObject(8462, 1341), new StumpObject(8463, 1341), new StumpObject(8464, 1341), new StumpObject(8465, 1341), new StumpObject(8466, 1341), new StumpObject(8467, 1341), new StumpObject(10083, 1341), new StumpObject(13413, 1341), new StumpObject(13420, 1341) }),
        WILLOW(30, 1519, 25, 500, 20, 3, new StumpObject[] { new StumpObject(1308, 7399), new StumpObject(5551, 5554), new StumpObject(5552, 5554), new StumpObject(5553, 5554), new StumpObject(8481, 1341), new StumpObject(8482, 1341), new StumpObject(8483, 1341), new StumpObject(8484, 1341), new StumpObject(8485, 1341), new StumpObject(8486, 1341), new StumpObject(8487, 1341), new StumpObject(8488, 1341), new StumpObject(8496, 1341), new StumpObject(8497, 1341), new StumpObject(8498, 1341), new StumpObject(8499, 1341), new StumpObject(8500, 1341), new StumpObject(8501, 1341) }),
        MAPLE(45, 1517, 45, 550, 25, 4, new StumpObject[] { new StumpObject(1307, 1342), new StumpObject(4674, 1342), new StumpObject(8435, 1341), new StumpObject(8436, 1341), new StumpObject(8437, 1341), new StumpObject(8438, 1341), new StumpObject(8439, 1341), new StumpObject(8440, 1341), new StumpObject(8441, 1341), new StumpObject(8442, 1341), new StumpObject(8443, 1341), new StumpObject(8444, 1341), new StumpObject(8454, 1341), new StumpObject(8455, 1341), new StumpObject(8456, 1341), new StumpObject(8457, 1341), new StumpObject(8458, 1341), new StumpObject(8459, 1341), new StumpObject(8460, 1341), new StumpObject(8461, 1341) }),
        YEW(60, 1515, 80, 800, 40, 6, new StumpObject[] { new StumpObject(1309, 7402), new StumpObject(8503, 1341), new StumpObject(8504, 1341), new StumpObject(8505, 1341), new StumpObject(8506, 1341), new StumpObject(8507, 1341), new StumpObject(8508, 1341), new StumpObject(8509, 1341), new StumpObject(8510, 1341), new StumpObject(8511, 1341), new StumpObject(8512, 1341), new StumpObject(8513, 1341) }),
        MAGIC(75, 1513, 120, 1500, 50, 8, new StumpObject[] { new StumpObject(1306, 1341), new StumpObject(8396, 1341), new StumpObject(8397, 1341), new StumpObject(8398, 1341), new StumpObject(8399, 1341), new StumpObject(8400, 1341), new StumpObject(8401, 1341), new StumpObject(8402, 1341), new StumpObject(8403, 1341), new StumpObject(8404, 1341), new StumpObject(8405, 1341), new StumpObject(8406, 1341), new StumpObject(8407, 1341), new StumpObject(8408, 1341), new StumpObject(840, 1341) });

        /**
         * The level needed to cut this type of tree.
         */
        private int level;

        /**
         * The item id of the log obtained after cutting this type of tree.
         */
        private int logId;

        /**
         * The time it takes for this type of tree to respawn in seconds.
         */
        private int respawnTime;

        /**
         * The experience received when you get a log from this type of tree.
         */
        private int experience;

        /**
         * The maximum amount of logs in a single tree of this type.
         */
        private int logsInTree;

        /**
         * The speed in getting logs from this type of tree.
         */
        private int speed;

        /**
         * The tree object ids and the stump object ids that replace them.
         */
        private StumpObject[] trees;

        /**
         * Construct data for a new tree type.
         * 
         * @param level
         *            the level.
         * @param logId
         *            the log id.
         * @param respawnTime
         *            the respawn time.
         * @param experience
         *            the experience.
         * @param logsInTree
         *            the amount of logs in this tree.
         * @param speed
         *            the speed.
         * @param trees
         *            the trees and corresponding stumps.
         */
        Tree(int level, int logId, int respawnTime, int experience, int logsInTree, int speed, StumpObject[] trees) {
            this.setLevel(level);
            this.setLogId(logId);
            this.setRespawnTime(respawnTime);
            this.setExperience(experience);
            this.setLogsInTree(logsInTree);
            this.setSpeed(speed);
            this.setTrees(trees);
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
         * @return the logId.
         */
        public int getLogId() {
            return logId;
        }

        /**
         * @param logId
         *            the logId to set.
         */
        public void setLogId(int logId) {
            this.logId = logId;
        }

        /**
         * @return the respawnTime.
         */
        public int getRespawnTime() {
            return respawnTime;
        }

        /**
         * @param respawnTime
         *            the respawnTime to set.
         */
        public void setRespawnTime(int respawnTime) {
            this.respawnTime = respawnTime;
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
         * @return the logsInTree.
         */
        public int getLogsInTree() {
            return logsInTree;
        }

        /**
         * @param logsInTree
         *            the logsInTree to set.
         */
        public void setLogsInTree(int logsInTree) {
            this.logsInTree = logsInTree;
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
         * @return the trees.
         */
        public StumpObject[] getTrees() {
            return trees;
        }

        /**
         * @param trees
         *            the trees to set.
         */
        public void setTrees(StumpObject[] trees) {
            this.trees = trees;
        }
    }

    /**
     * Holds data for all of the possible axes that can be used to cut a tree.
     * 
     * @author lare96
     */
    public enum Axe {
        BRONZE(1351, 1, 879, 7),

        IRON(1349, 1, 877, 7),

        STEEL(1353, 6, 875, 6),

        BLACK(1361, 6, 873, 5),

        MITHRIL(1355, 21, 871, 4),

        ADAMANT(1357, 31, 869, 3),

        RUNE(1359, 41, 867, 2),

        DRAGON(6739, 61, 2846, 0);

        /**
         * The id of the axe.
         */
        private int axeId;

        /**
         * The level of the axe.
         */
        private int level;

        /**
         * The animation that will be used when cutting with this axe.
         */
        private int animation;

        /**
         * The speed of this axe.
         */
        private int speed;

        /**
         * Construct data for each axe.
         * 
         * @param axeId
         *            the axe.
         * @param level
         *            the level.
         * @param animation
         *            the animation
         * @param speed
         *            the speed.
         */
        Axe(int axeId, int level, int animation, int speed) {
            this.setAxeId(axeId);
            this.setLevel(level);
            this.setAnimation(animation);
            this.setSpeed(speed);
        }

        /**
         * @return the axeId
         */
        public int getAxeId() {
            return axeId;
        }

        /**
         * @param axeId
         *            the axeId to set
         */
        public void setAxeId(int axeId) {
            this.axeId = axeId;
        }

        /**
         * @return the level
         */
        public int getLevel() {
            return level;
        }

        /**
         * @param level
         *            the level to set
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * @return the animation
         */
        public int getAnimation() {
            return animation;
        }

        /**
         * @param animation
         *            the animation to set
         */
        public void setAnimation(int animation) {
            this.animation = animation;
        }

        /**
         * @return the speed
         */
        public int getSpeed() {
            return speed;
        }

        /**
         * @param speed
         *            the speed to set
         */
        public void setSpeed(int speed) {
            this.speed = speed;
        }
    }

    /**
     * Begin cutting the the designated tree for the player.
     * 
     * @param player
     *            the player cutting this tree.
     * @param tree
     *            the tree this player is cutting.
     * @param axe
     *            the axe being used.
     * @param position
     *            the position of this tree.
     * @param objectId
     *            the id of this tree.
     */
    public void cut(final Player player, final Tree tree, final Axe axe, final Position position, final int objectId) {

        /** If we are already skilling, block. */
        if (player.getSkillingAction()[index()]) {
            reset(player);
            return;
        }

        /** If we have no space in our inventory, block. */
        if (player.getInventory().getItemContainer().freeSlots() < 1) {
            player.getServerPacketBuilder().sendMessage("You do not have any space left in your inventory.");
            return;
        }

        /** If we aren't a high enough level to cut this tree, block. */
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(tree.getLevel())) {
            player.getServerPacketBuilder().sendMessage("You need a woodcutting level of " + tree.getLevel() + " to cut " + tree.name().toLowerCase().replaceAll("_", " ") + " trees.");
            return;
        }

        /** Begin woodcutting. */
        player.getServerPacketBuilder().sendMessage("You swing your axe at the tree...");
        player.getSkillingAction()[index()] = true;
        player.animation(new Animation(axe.getAnimation()));
        player.setWoodcuttingLogAmount(getLogsInTree(tree));
        player.getMovementQueue().reset();

        GameLogic.getSingleton().submit(new Task((Misc.getRandom().nextInt(getWoodcuttingTime(player, tree, axe)) + 1), false, Time.SECOND) {
            @Override
            public void logic() {

                /** If we are not online, block and stop the task. */
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If there is a stump where you are cutting (someone cut the
                 * tree faster than you) than block and stop the task.
                 */
                if (checkStump(position)) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If a random integer between 0-15 is equal to 0, block and
                 * stop the task (used for making the player randomly stop
                 * mining).
                 */
                if (Misc.getRandom().nextInt(15) == 0) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /** If the skill has been stopped, block, and stop the task. */
                if (!player.getSkillingAction()[index()]) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /** Get some logs. */
                player.getServerPacketBuilder().sendMessage("You recieve some " + tree.name().toLowerCase().replaceAll("_", " ") + " logs.");
                player.getInventory().addItem(new Item(tree.getLogId()));
                exp(player, tree.getExperience());
                player.decrementWoodcuttingLogAmount();

                /** Respawn the tree */
                if (player.getWoodcuttingLogAmount() == 0) {
                    respawnTree(tree, position, objectId);
                    reset(player);
                    this.cancel();
                    return;
                }

                /** Another check for inventory space. */
                if (player.getInventory().getItemContainer().freeSlots() < 1) {
                    player.getServerPacketBuilder().sendMessage("You do not have any space left in your inventory.");
                    reset(player);
                    this.cancel();
                    return;
                }
            }
        });

        /**
         * Because the woodcutting animation is based on a strict time of 4
         * seconds, we use a separate task for the animation.
         */
        GameLogic.getSingleton().submit(new Task(3, true, Time.SECOND) {
            @Override
            public void logic() {

                /** If we are not online, block and stop the task. */
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /** If the skill has been stopped, block, and stop the task. */
                if (!player.getSkillingAction()[index()]) {
                    reset(player);
                    cancel();
                    return;
                }

                /** Perform the animation */
                player.animation(new Animation(axe.getAnimation()));
            }
        });
    }

    /**
     * Replaces the cut tree with the stump, then schedules a worker to respawn
     * the tree.
     * 
     * @param tree
     *            the tree to respawn.
     * @param position
     *            the position of the stump.
     * @param objectId
     *            the tree to respawn.
     */
    private void respawnTree(Tree tree, final Position position, int objectId) {
        final StumpObject respawn = getStumpObject(tree, objectId);

        /** Register a stump. */
        WorldObject.register(new WorldObject(respawn.getStump(), position, Rotation.SOUTH, 10));
        stumps.add(position);

        /** Schedule a task to respawn the tree in place of the stump. */
        GameLogic.getSingleton().submit(new Task(tree.getRespawnTime(), false, Time.SECOND) {
            @Override
            public void logic() {
                WorldObject.register(new WorldObject(respawn.getTreeId(), position, Rotation.SOUTH, 10));
                stumps.remove(position);
            }
        });
    }

    /**
     * Gets the correct stump for the specified tree.
     * 
     * @param tree
     *            the tree.
     * @param objectId
     *            the objectId.
     * @return the stump object.
     */
    private StumpObject getStumpObject(Tree tree, int objectId) {
        for (StumpObject object : tree.getTrees()) {
            if (object == null) {
                continue;
            }

            if (object.getTreeId() == objectId) {
                return object;
            }
        }
        return null;
    }

    /**
     * Checks if there is a stump where you are cutting.
     * 
     * @param position
     *            the position to check.
     * @return true if there is on stump on the position.
     */
    private boolean checkStump(Position position) {
        for (Position p : stumps) {
            if (p == null) {
                continue;
            }

            if (p.equals(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the time it takes to cut a tree.
     * 
     * @param player
     *            the player.
     * @param tree
     *            the tree.
     * @param axe
     *            the axe.
     */
    private int getWoodcuttingTime(Player player, Tree tree, Axe axe) {
        if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() <= 45) {
            return (tree.getSpeed() + axe.getSpeed()) * 3;
        } else if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() > 45 && player.getSkills().getTrainable()[skill().ordinal()].getLevel() <= 85) {
            return (tree.getSpeed() + axe.getSpeed()) * 2;
        } else if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() > 85) {
            return (tree.getSpeed() + axe.getSpeed());
        }

        return (tree.getSpeed() + axe.getSpeed()) * 3;
    }

    /**
     * Get the amount of logs in the tree for the player.
     * 
     * @param tree
     *            the tree.
     * @return the amount of logs in the tree.
     */
    private int getLogsInTree(Tree tree) {
        int amount = Misc.getRandom().nextInt(tree.getLogsInTree());

        return amount == 0 ? 1 : amount;
    }

    /**
     * Checks if you have an axe and if you have the required level to use it.
     * If you do then it returns an instance of it as an <code>Axe</code>
     * object.
     * 
     * @param player
     *            the player.
     * @return the axe you are wielding.
     */
    public Axe getAxe(Player player) {
        for (Axe axe : Axe.values()) {
            if (axe == null) {
                continue;
            }

            if (player.getInventory().getItemContainer().contains(axe.getAxeId()) || player.getEquipment().getItemContainer().contains(axe.getAxeId())) {
                if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() >= axe.getLevel()) {
                    return axe;
                } else {
                    player.getServerPacketBuilder().sendMessage("You are not a high enough level to use the " + axe.name().toLowerCase().replaceAll("_", " ") + " axe.");
                    return null;
                }
            }
        }

        player.getServerPacketBuilder().sendMessage("You need an axe in order to cut trees!");
        return null;
    }

    /**
     * @return the singleton.
     */
    public static Woodcutting getSingleton() {
        if (singleton == null) {
            singleton = new Woodcutting();
        }

        return singleton;
    }

    @Override
    public void reset(Player player) {
        player.getSkillingAction()[index()] = false;
        player.getServerPacketBuilder().resetAnimation();
        player.setWoodcuttingLogAmount(0);
    }

    @Override
    public int index() {
        return SkillManager.WOODCUTTING;
    }

    @Override
    public Skill skill() {
        return Skill.WOODCUTTING;
    }

    /**
     * Holds data for a tree and its corresponding stump.
     * 
     * @author lare96
     */
    public static class StumpObject {

        /**
         * The trees that will be replaced by this specified stump.
         */
        private int treeId;

        /**
         * The stump that will replace the tree.
         */
        private int stump;

        /**
         * Construct a new stump object.
         * 
         * @param treeId
         *            the id of the tree.
         * @param stump
         *            the id of the stump.
         */
        public StumpObject(int treeId, int stump) {
            this.setTreeId(treeId);
            this.setStump(stump);
        }

        /**
         * @return the treeId.
         */
        public int getTreeId() {
            return treeId;
        }

        /**
         * @param treeId
         *            the treeId to set.
         */
        public void setTreeId(int treeId) {
            this.treeId = treeId;
        }

        /**
         * @return the stump.
         */
        public int getStump() {
            return stump;
        }

        /**
         * @param stump
         *            the stump to set.
         */
        public void setStump(int stump) {
            this.stump = stump;
        }
    }
}
