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
 * Mining class that handles the mining skill. This supports mining almost all
 * ores with rock respawning, and my own custom formula for speed.
 * 
 * @author lare96
 */
public class Mining extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Mining singleton;

    /**
     * The positions of all the empty rocks.
     */
    private static List<Position> rocks = new ArrayList<Position>();

    /**
     * Holds data for all of the ores able to be mined.
     * 
     * @author lare96
     */
    public enum Ore {
        CLAY(new OreObject[] { new OreObject(2108, 450), new OreObject(2109, 451) }, 1, 434, 1, 1, 150),
        RUNE_ESSENCE(new OreObject[] { new OreObject(2491, -1) }, 1, 1436, 0, 1, 150),
        TIN(new OreObject[] { new OreObject(2094, 450), new OreObject(2095, 451) }, 1, 438, 5, 1, 150),
        COPPER(new OreObject[] { new OreObject(2090, 450), new OreObject(2091, 451) }, 1, 436, 5, 1, 150),
        IRON(new OreObject[] { new OreObject(2092, 450), new OreObject(2093, 451) }, 15, 440, 10, 1, 500),
        SILVER(new OreObject[] { new OreObject(2100, 450), new OreObject(2101, 451) }, 20, 443, 120, 3, 600),
        COAL(new OreObject[] { new OreObject(2096, 450), new OreObject(2097, 451) }, 30, 453, 60, 4, 700),
        GOLD(new OreObject[] { new OreObject(2098, 450), new OreObject(2099, 451) }, 40, 444, 120, 5, 800),
        MITHRIL(new OreObject[] { new OreObject(2102, 450), new OreObject(2103, 451) }, 55, 447, 180, 6, 1000),
        ADAMANTITE(new OreObject[] { new OreObject(2104, 450), new OreObject(2105, 451) }, 70, 449, 420, 7, 1200),
        RUNITE(new OreObject[] { new OreObject(2106, 450), new OreObject(2107, 451) }, 85, 451, 840, 10, 1500);

        /**
         * The object id of the ore and its replacement.
         */
        private OreObject[] objectOre;

        /**
         * The level needed to mine the ore.
         */
        private int level;

        /**
         * The item id of the ore.
         */
        private int itemId;

        /**
         * The respawn time of the ore.
         */
        private int respawnTime;

        /**
         * How hard this ore is to mine.
         */
        private int speed;

        /**
         * The experience you gain from mining this rock.
         */
        private int experience;

        /**
         * Construct the data.
         * 
         * @param objectOre
         *            the ore and it's empty rock.
         * @param level
         *            the level needed to mine this rock.
         * 
         * @param itemId
         *            the item id of this rock.
         * @param respawnTime
         *            the respawn time for this rock.
         * @param speed
         *            the speed to mine to rock.
         * @param experience
         *            the experience gained from mining this rock.
         */
        Ore(OreObject[] objectOre, int level, int itemId, int respawnTime, int speed, int experience) {
            this.setObjectOre(objectOre);
            this.setLevel(level);
            this.setItemId(itemId);
            this.setRespawnTime(respawnTime);
            this.setSpeed(speed);
            this.setExperience(experience);
        }

        /**
         * @return the objectId.
         */
        public OreObject[] getObjectOre() {
            return objectOre;
        }

        /**
         * @param objectId
         *            the objectId to set.
         */
        public void setObjectOre(OreObject[] objectOre) {
            this.objectOre = objectOre;
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
         * @return the itemId.
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * @param itemId
         *            the itemId to set.
         */
        public void setItemId(int itemId) {
            this.itemId = itemId;
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
     * Holds data for all of the possible pickaxes that can be used used to mine
     * ores.
     */
    public enum Pickaxe {
        BRONZE(1265, 1, 625, 3),

        IRON(1267, 1, 626, 3),

        STEEL(1269, 5, 627, 3),

        MITHRIL(1273, 20, 629, 2),

        ADAMANT(1271, 30, 628, 1),

        RUNE(1275, 40, 624, 0);

        /**
         * The id of the pickaxe.
         */
        private int id;

        /**
         * The level needed to use this pickaxe.
         */
        private int level;

        /**
         * The animation when mining with this pickaxe.
         */
        private int animation;

        /**
         * The speed of the pickaxe.
         */
        private int speed;

        /**
         * Construct new data.
         * 
         * @param id
         *            the pickaxe id.
         * @param level
         *            the level needed to use this pickaxe.
         * @param speed
         *            the speed when using this pickaxe.
         */
        Pickaxe(int id, int level, int animation, int speed) {
            this.setId(id);
            this.setLevel(level);
            this.setAnimation(animation);
            this.setSpeed(speed);
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
     * Mine the specified ore for the designated player with the selected
     * pickaxe.
     * 
     * @param player
     *            the player mining.
     * @param ore
     *            the ore you are mining.
     * @param pick
     *            the pickaxe you are using.
     * @param position
     *            the position this rock is on.
     */
    public void mine(final Player player, final Ore ore, final Pickaxe pick, final Position rockPosition, final int objectId) {

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

        /** If we aren't a high enough level to mine this ore, block. */
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(ore.getLevel())) {
            player.getServerPacketBuilder().sendMessage("You need a mining level of " + ore.getLevel() + " to mine " + ore.name().toLowerCase().replaceAll("_", " ") + ".");
            return;
        }

        /** Begin mining. */
        player.getServerPacketBuilder().sendMessage("You mine the rocks...");
        player.getSkillingAction()[index()] = true;
        player.animation(new Animation(pick.getAnimation()));
        player.getMovementQueue().reset();

        GameLogic.getSingleton().submit(new Task((Misc.getRandom().nextInt(getMiningTime(player, ore, pick)) + 1), false, Time.TICK) {
            @Override
            public void logic() {

                /** If we are not online, block and stop the task. */
                if (player.getNetwork().isDisconnected()) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If there is an empty rock (someone mined faster than you)
                 * than block and stop the task.
                 */
                if (checkRock(rockPosition) && ore != Ore.RUNE_ESSENCE) {
                    reset(player);
                    this.cancel();
                    return;
                }

                /**
                 * If a random integer between 0-15 is equal to 0, block and
                 * stop the task (used for making the player randomly stop
                 * mining).
                 */
                if (Misc.getRandom().nextInt(15) == 0 && ore != Ore.RUNE_ESSENCE) {
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

                /** Mine the ore. */
                player.getServerPacketBuilder().sendMessage("You recieve the ore that crumbles off of the rocks.");
                player.getInventory().addItem(new Item(ore.getItemId()));
                exp(player, ore.getExperience());

                /** Respawn the rock */
                if (ore != Ore.RUNE_ESSENCE) {
                    respawnRock(ore, rockPosition, objectId);
                }

                reset(player);
                this.cancel();

                /** Another check for inventory space. */
                if (player.getInventory().getItemContainer().freeSlots() < 1) {
                    player.getServerPacketBuilder().sendMessage("You do not have any space left in your inventory.");
                    return;
                }
            }
        });

        /**
         * Because the mining animation is based on a strict time of 4 seconds,
         * we use a separate task for the animation.
         */
        GameLogic.getSingleton().submit(new Task(4, true, Time.SECOND) {
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
                player.animation(new Animation(pick.getAnimation()));
            }
        });
    }

    /**
     * Replaces the ore with the empty rocks and respawns the ore.
     * 
     * @param ore
     *            the ore you are mining.
     * @param position
     *            the position this rock is on.
     * @param objectId
     *            the id of this rock.
     */
    private void respawnRock(Ore ore, final Position position, int objectId) {
        final OreObject respawn = getOreObject(ore, objectId);

        /** Register an empty rock. */
        WorldObject.register(new WorldObject(respawn.getEmpty(), position, Rotation.SOUTH, 10));
        rocks.add(position);

        /** Schedule a task to respawn the proper ore in place of the empty rock. */
        GameLogic.getSingleton().submit(new Task(ore.getRespawnTime(), false, Time.SECOND) {
            @Override
            public void logic() {
                WorldObject.register(new WorldObject(respawn.getOre(), position, Rotation.SOUTH, 10));
                rocks.remove(position);
            }
        });
    }

    /**
     * Gets the correct ore object.
     * 
     * @param ore
     *            the ore.
     * @param objectId
     *            the ore object id.
     * @return the ore object.
     */
    private OreObject getOreObject(Ore ore, int objectId) {
        for (OreObject object : ore.getObjectOre()) {
            if (object == null) {
                continue;
            }

            if (object.getOre() == objectId) {
                return object;
            }
        }
        return null;
    }

    /**
     * Sends the player a message of what ore is concealed within the rock.
     * 
     * @param player
     *            the player to send the message to.
     * @param ore
     *            the ore being prospected.
     */
    public void prospect(Player player, Ore ore) {
        player.getServerPacketBuilder().sendMessage("This rock contains " + ore.name().toLowerCase().replaceAll("_", " ") + " ore.");
    }

    /**
     * Checks if there's an empty rock where you're mining.
     * 
     * @param position
     *            the position to check.
     * @return true if there is an empty rock.
     */
    private boolean checkRock(Position position) {
        for (Position p : rocks) {
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
     * Gets the time it takes to mine a rock.
     * 
     * @param player
     *            the player mining.
     * @param ore
     *            the ore being mined.
     * @param pick
     *            the pickaxe being used.
     * @return the mining time.
     */
    private int getMiningTime(Player player, Ore ore, Pickaxe pick) {
        if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() <= 45) {
            return (ore.getSpeed() + pick.getSpeed()) * 3;
        } else if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() > 45 && player.getSkills().getTrainable()[skill().ordinal()].getLevel() <= 85) {
            return (ore.getSpeed() + pick.getSpeed()) * 2;
        } else if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() > 85) {
            return (ore.getSpeed() + pick.getSpeed());
        }

        return (ore.getSpeed() + pick.getSpeed()) * 3;
    }

    /**
     * Checks if you have an pickaxe and if you have the required level to use
     * it. If you do then it returns an instance of it as an
     * <code>Pickaxe</code> object.
     * 
     * @param player
     *            the player to check.
     * @return the pickaxe in the inventory or equipment weapon slot.
     */
    public Pickaxe getPickaxe(Player player) {
        for (Pickaxe pick : Pickaxe.values()) {
            if (pick == null) {
                continue;
            }

            if (player.getInventory().getItemContainer().contains(pick.getId()) || player.getEquipment().getItemContainer().contains(pick.getId())) {
                if (player.getSkills().getTrainable()[skill().ordinal()].getLevel() >= pick.getLevel()) {
                    return pick;
                } else {
                    player.getServerPacketBuilder().sendMessage("You are not a high enough level to use the " + pick.name().toLowerCase().replaceAll("_", " ") + " pickaxe.");
                    return null;
                }
            }
        }

        player.getServerPacketBuilder().sendMessage("You need a pickaxe in order to mine rocks!");
        return null;
    }

    /**
     * @return the singleton.
     */
    public static Mining getSingleton() {
        if (singleton == null) {
            singleton = new Mining();
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
        return SkillManager.MINING;
    }

    @Override
    public Skill skill() {
        return Skill.MINING;
    }

    /**
     * A mining object and its corresponding empty replacement.
     * 
     * @author lare96
     */
    public static class OreObject {

        /**
         * The actual ore.
         */
        private int ore;

        /**
         * The empty replacement.
         */
        private int empty;

        /**
         * The class constructor.
         * 
         * @param ore
         *            the actual ore.
         * @param empty
         *            the empty replacement.
         */
        public OreObject(int ore, int empty) {
            this.setOre(ore);
            this.setEmpty(empty);
        }

        /**
         * @return the ore.
         */
        public int getOre() {
            return ore;
        }

        /**
         * @param ore
         *            the ore to set.
         */
        public void setOre(int ore) {
            this.ore = ore;
        }

        /**
         * @return the empty.
         */
        public int getEmpty() {
            return empty;
        }

        /**
         * @param empty
         *            the empty to set.
         */
        public void setEmpty(int empty) {
            this.empty = empty;
        }
    }
}
