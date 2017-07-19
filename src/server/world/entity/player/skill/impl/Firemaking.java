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
import server.world.item.WorldItem;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * Class that handles the firemaking skill. This contains support for walking to
 * the west when lit, fires burning out, and the inability to light fires on top
 * of another fire or item.
 * 
 * @author lare96
 */
public class Firemaking extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Firemaking singleton;

    /**
     * A list of the positions of all the fires lit. We need this so we can keep
     * track of which positions have fires on them. This information is
     * <b>extremely</b> useful because it enables us to prevent players from
     * lighting fires on the same position, and makes it way easier to replace
     * fires with ashes.
     */
    private static List<Position> fires = new ArrayList<Position>();

    /**
     * All of the data for logs we are able to light.
     * 
     * @author lare96
     */
    public enum Logs {
        NORMAL(1511, 1, 1, 50, 30),
        OAK(1521, 1, 15, 75, 50),
        WILLOW(1519, 2, 30, 150, 80),
        MAPLE(1517, 2, 45, 900, 100),
        YEW(1515, 5, 60, 950, 150),
        MAGIC(1513, 6, 75, 1500, 200);

        /**
         * The id of the log.
         */
        private int logId;

        /**
         * The speed it takes to light this log.
         */
        private int lightSpeed;

        /**
         * The level needed to light this log.
         */
        private int level;

        /**
         * The experience given when burned.
         */
        private int experience;

        /**
         * The time it takes to finish burning and turn into ashes (in seconds).
         */
        private int burnTime;

        /**
         * Construct new data for logs.
         * 
         * @param logId
         *            the log id.
         * @param lightSpeed
         *            how long it takes to light this log.
         * @param level
         *            the level needed to light this log.
         * @param experience
         *            the experienced gained from lighting this log.
         * @param burnTime
         *            the time before this log turns into ashes.
         */
        Logs(int logId, int lightSpeed, int level, int experience, int burnTime) {
            this.setLogId(logId);
            this.setLightSpeed(lightSpeed);
            this.setLevel(level);
            this.setExperience(experience);
            this.setBurnTime(burnTime);
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
         * @return the lightSpeed.
         */
        public int getLightSpeed() {
            return lightSpeed;
        }

        /**
         * @param lightSpeed
         *            the lightSpeed to set.
         */
        public void setLightSpeed(int lightSpeed) {
            this.lightSpeed = lightSpeed;
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
         * @return the burnTime.
         */
        public int getBurnTime() {
            return burnTime;
        }

        /**
         * @param burnTime
         *            the burnTime to set.
         */
        public void setBurnTime(int burnTime) {
            this.burnTime = burnTime;
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
     * Performs checks and then lights a log for the designated player from
     * their inventory.
     * 
     * @param player
     *            the player lighting this log.
     * @param log
     *            the log being lit.
     */
    public void lightLog(final Player player, final Logs log) {

        /** If we are already skilling, block. */
        if (player.getSkillingAction()[index()]) {
            reset(player);
            return;
        }

        /** If we aren't a high enough level to light this log, block. */
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(log.getLevel())) {
            player.getServerPacketBuilder().sendMessage("You need a firemaking level of " + log.getLevel() + " to light " + log.name().toLowerCase().replaceAll("_", " ") + " logs.");
            return;
        }

        /** If an item or fire exists on the spot you are trying to light, block. */
        if (WorldItem.itemExistsOnPosition(player.getPosition()) || getFires().contains(player.getPosition())) {
            player.getServerPacketBuilder().sendMessage("You cannot light a fire here!");
            reset(player);
            return;
        }

        player.getMovementQueue().reset();
        player.getSkillingAction()[index()] = true;

        GameLogic.getSingleton().submit(new Task((Misc.getRandom().nextInt(getLightTime(player, log)) + 1), false, Time.SECOND) {
            @Override
            public void logic() {

                /** If we are disconnected, stop. */
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If for some reason we are no longer firemaking (walked,
                 * dropped something, equipped, etc.), stop.
                 */
                if (!player.getSkillingAction()[index()]) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /** Light the log */
                player.getMovementQueue().walk(-1, 0);
                player.getInventory().removeItem(new Item(log.getLogId()));
                exp(player, log.getExperience());
                burnLog(player, log);
                reset(player);
                this.cancel();
            }
        });

        /**
         * Task that handles the animation. This needs to be in a separate task
         * because the animation runs on a strict time of 3 ticks.
         */
        GameLogic.getSingleton().submit(new Task(3, true, Time.TICK) {
            @Override
            public void logic() {
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                if (!player.getSkillingAction()[index()]) {
                    reset(player);
                    this.cancel();
                    return;
                }

                player.animation(new Animation(733));
            }
        });
    }

    /**
     * Schedules a task that will turn the log into ashes.
     * 
     * @param player
     *            the player who lit this log.
     * @param log
     *            the log being lit and turned into ashes.
     */
    private void burnLog(Player player, Logs log) {
        final Position logPosition = new Position(player.getPosition().getX(), player.getPosition().getY());

        /** Make and register the fire. */
        getFires().add(logPosition);
        WorldObject.register(new WorldObject(2732, logPosition, Rotation.SOUTH, 10));

        /** Unregister the fire and replace it with ashes after the delay */
        GameLogic.getSingleton().submit(new Task(log.getBurnTime(), false, Time.SECOND) {
            @Override
            public void logic() {
                getFires().remove(logPosition);
                WorldObject.unregister(new WorldObject(2732, logPosition, Rotation.SOUTH, 10));
                WorldItem.registerStaticItem(new WorldItem(new Item(592, 1), logPosition, null));
            }
        });
    }

    /**
     * Get the time it takes to light the log based on the log you're lighting
     * and your firemaking level.
     * 
     * @param player
     *            the player lighting this log.
     * @param log
     *            the log being lit.
     * @return the time it will take to light the log.
     */
    private int getLightTime(Player player, Logs log) {
        if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() <= 45) {
            return log.getLightSpeed() * 3;
        } else if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() > 45 && player.getSkills().getTrainable()[skill().ordinal()].getLevel() <= 85) {
            return log.getLightSpeed() * 2;
        } else if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() > 85) {
            return log.getLightSpeed();
        }

        return log.getLightSpeed() * 3;
    }

    /**
     * @return the fires.
     */
    public List<Position> getFires() {
        return fires;
    }

    /**
     * @param singleton
     *            the singleton to set.
     */
    public static Firemaking getSingleton() {
        if (singleton == null) {
            singleton = new Firemaking();
        }

        return singleton;
    }

    @Override
    public void reset(Player player) {
        player.getSkillingAction()[index()] = false;
        player.getServerPacketBuilder().resetAnimation();
    }

    @Override
    public int index() {
        return SkillManager.FIREMAKING;
    }

    @Override
    public Skill skill() {
        return Skill.FIREMAKING;
    }
}
