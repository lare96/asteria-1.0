package server.world.entity.player.skill.impl;

import server.logic.task.Task.Time;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.combat.Hit;
import server.world.entity.combat.Hit.DamageType;
import server.world.entity.mob.Mob;
import server.world.entity.mob.MobDefinition;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.item.Item;

/**
 * Thieving skill which supports being able to pickpocket from mobs with the
 * chance of being caught and stunned. This also supports stealing from stalls
 * with a random chance of a someone within the area noticing and attacking you.
 * 
 * @author lare96
 */
public class Thieving extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Thieving singleton;

    /**
     * The size of the area to check when the player steals from a stall.
     */
    private static int AREA_RADIUS_TO_CHECK = 7;

    /**
     * The delay between stealing from mobs.
     */
    private static int MOB_THIEVING_DELAY = 4500;

    /**
     * All of the possible mobs to steal from.
     * 
     * @author lare96
     */
    public enum TheftMob {
        MAN_AND_WOMAN(1, 50, 5, new Item(995, 15), new Item(995, 10), new Item(995, 5)),
        FARMER(10, 75, 5, new Item(5318), new Item(5319)),
        FEMALE_HAM(15, 100, 6, new Item(1265), new Item(1267), new Item(1269), new Item(1205), new Item(1203), new Item(1207), new Item(1627), new Item(1625), new Item(688), new Item(697), new Item(7758), new Item(882, 15), new Item(884, 15), new Item(886, 15), new Item(1351), new Item(1349), new Item(1353), new Item(995, 15), new Item(1733), new Item(1734, 15), new Item(440), new Item(453), new Item(4302), new Item(4298), new Item(4300), new Item(4304), new Item(4306), new Item(4308), new Item(4310), new Item(319), new Item(314), new Item(1131), new Item(1739), new Item(2511), new Item(4289)),
        MALE_HAM(20, 150, 6, new Item(1265), new Item(1267), new Item(1269), new Item(1205), new Item(1203), new Item(1207), new Item(1627), new Item(1625), new Item(688), new Item(697), new Item(7758), new Item(882, 15), new Item(884, 15), new Item(886, 15), new Item(1351), new Item(1349), new Item(1353), new Item(995, 15), new Item(1733), new Item(1734, 15), new Item(440), new Item(453), new Item(4302), new Item(4298), new Item(4300), new Item(4304), new Item(4306), new Item(4308), new Item(4310), new Item(319), new Item(314), new Item(1131), new Item(1739), new Item(2511), new Item(4289)),
        WARRIOR_WOMAN(25, 175, 5, new Item(995, 150)),
        WARRIOR(25, 190, 5, new Item(995, 150)),
        ROGUE(32, 210, 5, new Item(556, 8), new Item(995, 50), new Item(1523), new Item(1993), new Item(5668)),
        CAVE_GOBLIN(36, 230, 5, new Item(995, 15), new Item(440), new Item(590), new Item(1939), new Item(4535), new Item(595), new Item(4544)),
        MASTER_FARMER(38, 250, 5, new Item(5096), new Item(5097), new Item(5098), new Item(5100), new Item(5101), new Item(5102), new Item(5103), new Item(5104), new Item(5106), new Item(5321), new Item(5309), new Item(5311), new Item(5322), new Item(5324), new Item(5320), new Item(5323), new Item(5307), new Item(5281), new Item(5280), new Item(5295), new Item(5308), new Item(5305), new Item(5310), new Item(5319), new Item(7548)),
        GUARD(40, 285, 5, new Item(995, 300)),
        RELLEKKA_CITIZEN(45, 315, 5, new Item(995, 400), new Item(1539)),
        BEARDED_POLLNIVNIAN_BANDIT(45, 315, 4, new Item(995, 450)),
        DESERT_BANDIT(53, 400, 5, new Item(995, 350), new Item(1523), new Item(2446)),
        KNIGHT_OF_ARDOUGNE(55, 475, 5, new Item(995, 500)),
        POLLNIVNIAN_BANDIT(55, 450, 5, new Item(995, 500)),
        WATCHMAN(65, 550, 5, new Item(995, 600), new Item(2309)),
        MENAPHITE_THUG(65, 500, 5, new Item(995, 600)),
        PALADIN(70, 550, 5, new Item(995, 800), new Item(562, 15)),
        GNOME(75, 600, 5, new Item(2150), new Item(2162), new Item(569), new Item(557, 15)),
        HERO(80, 650, 6, new Item(569), new Item(1993), new Item(560, 30), new Item(565, 15), new Item(1601)),
        ELF(85, 700, 6, new Item(569), new Item(1993), new Item(560, 45), new Item(565, 30), new Item(1601));

        /**
         * The required level to steal from this stall.
         */
        private int requiredLevel;

        /**
         * The experience you gain when stealing from this stall.
         */
        private int experience;

        /**
         * The time in seconds that you are stunned for when caught.
         */
        private int stunDelay;

        /**
         * The possible items to receive when stealing.
         */
        private Item[] theftItem;

        /**
         * Create new data for a mob.
         * 
         * @param requiredLevel
         *            the required level to steal from this stall.
         * @param experience
         *            the experience you gain when stealing from this stall.
         * @param stunDelay
         *            the time in seconds that you are stunned for.
         * @param theftItem
         *            the possible items to receive when stealing.
         */
        TheftMob(int requiredLevel, int experience, int stunDelay, Item... theftItem) {
            this.setRequiredLevel(requiredLevel);
            this.setExperience(experience);
            this.setStunDelay(stunDelay);
            this.setTheftItem(theftItem);
        }

        /**
         * @return the requiredLevel.
         */
        public int getRequiredLevel() {
            return requiredLevel;
        }

        /**
         * @param requiredLevel
         *            the requiredLevel to set.
         */
        public void setRequiredLevel(int requiredLevel) {
            this.requiredLevel = requiredLevel;
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
         * @return the stunDelay.
         */
        public int getStunDelay() {
            return stunDelay;
        }

        /**
         * @param stunDelay
         *            the stunDelay to set.
         */
        public void setStunDelay(int stunDelay) {
            this.stunDelay = stunDelay;
        }

        /**
         * @return the theftItem.
         */
        public Item[] getTheftItem() {
            return theftItem;
        }

        /**
         * @param theftItem
         *            the theftItem to set.
         */
        public void setTheftItem(Item[] theftItem) {
            this.theftItem = theftItem;
        }
    }

    /**
     * All of the possible objects to steal from.
     * 
     * @author lare96
     */
    public enum TheftObject {
        // FIXME: Needs seed and market stalls.
        VEGETABLE_STALL(2, 50, 2, new Item(1957), new Item(1965), new Item(1942), new Item(1982), new Item(1550)),
        BAKER_STALL(5, 100, 3, new Item(1891), new Item(1901), new Item(2309)),
        CRAFTING_STALL(5, 110, 4, new Item(5601), new Item(1592), new Item(1597)),
        MONKEY_FOOD_STALL(5, 125, 5, new Item(1963)),
        MONKEY_GENERAL_STALL(5, 140, 5, new Item(1931), new Item(2347), new Item(590)),
        TEA_STALL(5, 150, 5, new Item(712)),
        SILK_STALL(20, 250, 6, new Item(950)),
        WINE_STALL(22, 270, 6, new Item(1937), new Item(1993), new Item(1987), new Item(1935), new Item(7919)),
        FUR_STALL(35, 300, 7, new Item(6814), new Item(958)),
        FISH_STALL(42, 350, 7, new Item(331), new Item(359), new Item(377)),
        CROSSBOW_STALL(49, 400, 7, new Item(877, 3), new Item(877, 5), new Item(877, 7), new Item(877, 10)),
        SILVER_STALL(50, 450, 7, new Item(442)),
        SPICE_STALL(65, 500, 7, new Item(2007)),
        MAGIC_STALL(65, 500, 10, new Item(556, 5), new Item(556, 15), new Item(557, 5), new Item(557, 15), new Item(554, 5), new Item(554, 15), new Item(555, 5), new Item(555, 15), new Item(563, 5)),
        SCIMITAR_STALL(65, 675, 15, new Item(1321), new Item(1323), new Item(1325), new Item(1327), new Item(1329), new Item(1331), new Item(1333)),
        GEM_STALL(75, 800, 20, new Item(1623), new Item(1621), new Item(1619), new Item(1617));

        /**
         * The required level to steal from this stall.
         */
        private int requiredLevel;

        /**
         * The experience you gain when stealing from this stall.
         */
        private int experience;

        /**
         * The delay between stealing.
         */
        private int theftDelay;

        /**
         * The possible items to receive when stealing.
         */
        private Item[] theftItem;

        /**
         * Create new data for a stall.
         * 
         * @param requiredLevel
         *            the required level to steal from this stall.
         * @param experience
         *            the experience you gain when stealing from this stall.
         * @param theftDelay
         *            the delay between stealing.
         * @param theftItem
         *            the possible items to receive when stealing.
         */
        TheftObject(int requiredLevel, int experience, int theftDelay, Item... theftItem) {
            this.setRequiredLevel(requiredLevel);
            this.setExperience(experience);
            this.setTheftDelay(theftDelay);
            this.setTheftItem(theftItem);
        }

        /**
         * @return the requiredLevel.
         */
        public int getRequiredLevel() {
            return requiredLevel;
        }

        /**
         * @param requiredLevel
         *            the requiredLevel to set.
         */
        public void setRequiredLevel(int requiredLevel) {
            this.requiredLevel = requiredLevel;
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
         * @return the theftDelay.
         */
        public int getTheftDelay() {
            return theftDelay;
        }

        /**
         * @param theftDelay
         *            the theftDelay to set.
         */
        public void setTheftDelay(int theftDelay) {
            this.theftDelay = theftDelay;
        }

        /**
         * @return the theftItem.
         */
        public Item[] getTheftItem() {
            return theftItem;
        }

        /**
         * @param theftItem
         *            the theftItem to set.
         */
        public void setTheftItem(Item[] theftItem) {
            this.theftItem = theftItem;
        }
    }

    /**
     * Stealing from an active mob.
     * 
     * @param player
     *            the player stealing from the mob.
     * @param theftMob
     *            the data of the mob you're stealing from.
     * @param mob
     *            the actual mob you're stealing from.
     */
    public void stealFromMob(Player player, TheftMob theftMob, Mob mob) {
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(theftMob.getRequiredLevel())) {
            player.getServerPacketBuilder().sendMessage("You need a thieving level of " + theftMob.getRequiredLevel() + " to pickpocket this " + MobDefinition.getMobDefinition()[mob.getMobId()].getName() + "");
            return;
        }

        if (player.getMobTheftTimer().elapsed() > MOB_THIEVING_DELAY) {
            player.getMovementQueue().reset();
            player.getSkillingAction()[index()] = true;

            if (Misc.getRandom().nextInt(10) != 10) {
                Item stole = Misc.randomElement(theftMob.getTheftItem());

                if (player.getInventory().getItemContainer().hasRoomFor(stole)) {
                    player.animation(new Animation(881));
                    player.getInventory().addItem(stole);
                    exp(player, theftMob.getExperience());
                    player.getServerPacketBuilder().sendMessage("You successfully pickpocket the " + MobDefinition.getMobDefinition()[mob.getMobId()].getName() + ".");
                } else {
                    player.getServerPacketBuilder().sendMessage("You should make room in your inventory before trying to pickpocket someone!");
                }
            } else {
                mob.facePosition(player.getPosition());
                mob.forceChat("What do you think you're doing?!?");
                mob.animation(new Animation(MobDefinition.getMobDefinition()[mob.getMobId()].getAttackAnimation()));
                player.primaryHit(new Hit(Misc.getRandom().nextInt(5), DamageType.NORMAL));
                player.gfx(new Gfx(426));
                player.getMovementQueue().lockMovementFor(theftMob.getStunDelay(), Time.SECOND);
                player.getServerPacketBuilder().sendMessage("You fail to pickpocket the " + MobDefinition.getMobDefinition()[mob.getMobId()].getName() + ".");
            }

            player.getMobTheftTimer().reset();
            reset(player);
        } else {
            player.getServerPacketBuilder().sendMessage("You should wait before trying to pickpocket again or you'll look suspicious!");
        }
    }

    /**
     * Stealing from an active object.
     * 
     * @param player
     *            the player stealing from the object.
     * @param object
     *            the object this player is stealing from.
     */
    public void stealFromObject(Player player, TheftObject object) {
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(object.getRequiredLevel())) {
            player.getServerPacketBuilder().sendMessage("You need a thieving level of " + object.getRequiredLevel() + " to steal from this stall.");
            return;
        }

        if (player.getObjectTheftTimer().elapsed() > player.getLastTheftDelay()) {
            player.getMovementQueue().reset();
            player.getSkillingAction()[index()] = true;

            Item stole = Misc.randomElement(object.getTheftItem());

            if (player.getInventory().getItemContainer().hasRoomFor(stole)) {
                checkSurroundingArea(player);
                player.animation(new Animation(881));
                player.getInventory().addItem(stole);
                exp(player, object.getExperience());
                player.getServerPacketBuilder().sendMessage("You successfully steal an item from the stall.");
            } else {
                player.getServerPacketBuilder().sendMessage("You should make room in your inventory before trying to pickpocket someone!");
            }

            player.setLastTheftDelay(object.getTheftDelay());
            player.getObjectTheftTimer().reset();
            reset(player);
        } else {
            player.getServerPacketBuilder().sendMessage("You should wait before trying to steal again or you'll look suspicious!");
        }
    }

    /**
     * Checks the surrounding area for any entities able to attack. If any
     * entities are found, they start attacking the player. The size of the area
     * checked depends on the value of <code>AREA_RADIUS_TO_CHECK</code>.
     * 
     * @param player
     *            the player who's area should be checked.
     */
    private void checkSurroundingArea(Player player) {
        if (Misc.getRandom().nextInt(14) == 0) {
            for (Mob mob : World.getNpcs()) {
                if (mob == null) {
                    continue;
                }

                if (mob.getPosition().withinDistance(player.getPosition(), AREA_RADIUS_TO_CHECK)) {
                    if (Mob.getDefinition(mob.getMobId()).getName().equals("Man") || Mob.getDefinition(mob.getMobId()).getName().equals("Woman")) {
                        /** Weak mobs will not attack. */

                        mob.forceChat("... Hey! Someone is stealing from that stall! Help!");
                        mob.facePosition(player.getPosition());
                        mob.animation(new Animation(859));

                        // FIXME: Make this mob follow the player.
                    } else {
                        /** Anyone else will attack. */

                        mob.forceChat("What do you think you're doing?!?");
                        // Combat.getSingleton().startCombat(mob, player);
                    }
                }
            }
        }
    }

    /**
     * @return the singleton instance.
     */
    public static Thieving getSingleton() {
        if (singleton == null) {
            singleton = new Thieving();
        }

        return singleton;
    }

    @Override
    public void reset(Player player) {
        player.getSkillingAction()[index()] = false;
    }

    @Override
    public int index() {
        return SkillManager.THIEVING;
    }

    @Override
    public Skill skill() {
        return Skill.THIEVING;
    }
}
