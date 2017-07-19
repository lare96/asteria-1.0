package server.world.entity.player.skill.impl;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.mob.Mob;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.item.Item;
import server.world.map.Position;

/**
 * Class that contains almost everything needed for a decent, runecrafting
 * skill. Has support for crafting single and multiple runes, and rather than
 * using talisman based entry into an altar there is a mob at draynor that
 * teleports players to a variety of places related to runecrafting. Obtaining
 * essence is included as part of the {@link Mining} skill.
 * 
 * @author lare96
 */
public class Runecrafting extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Runecrafting singleton;

    /**
     * The position of the rune essence mine.
     */
    public static final Position RUNE_ESSENCE_MINE = new Position(2890, 4813);

    /**
     * The id of rune essence.
     */
    private static final int RUNE_ESSENCE = 1436;

    /**
     * All of the possible altars to craft runes on.
     * 
     * @author lare96
     */
    public enum Altar {
        AIR(2478, Rune.AIR, new Position(2841, 4829)),
        MIND(2479, Rune.MIND, new Position(2793, 4828)),
        WATER(2480, Rune.WATER, new Position(2726, 4832)),
        EARTH(2481, Rune.EARTH, new Position(2655, 4830)),
        FIRE(2482, Rune.FIRE, new Position(2574, 4849)),
        BODY(2483, Rune.BODY, new Position(2523, 4826)),
        COSMIC(2484, Rune.COSMIC, new Position(2162, 4833)),
        CHAOS(2487, Rune.CHAOS, new Position(2281, 4837)),
        NATURE(2486, Rune.NATURE, new Position(2400, 4835)),
        LAW(2485, Rune.LAW, new Position(2464, 4818)),
        DEATH(2488, Rune.DEATH, new Position(2208, 4830)),
        BLOOD(7141, Rune.BLOOD, new Position(3027, 4834)),
        SOUL(7138, Rune.SOUL, new Position(3050, 4829));

        /**
         * The altar you are crafting runes on.
         */
        private int altarId;

        /**
         * The type of rune that can be crafted on this altar.
         */
        private Rune rune;

        /**
         * The position the player will be teleported onto upon entering the
         * altar.
         */
        private Position teleport;

        /**
         * The constructor for this enum.
         * 
         * @param altarId
         *            the if of the altar.
         * @param rune
         *            the rune that can be crafted on this altar.
         * @param teleport
         *            the position of this altar.
         */
        Altar(int altarId, Rune rune, Position teleport) {
            this.setAltarId(altarId);
            this.setRune(rune);
            this.setTeleport(teleport);
        }

        /**
         * @return the altarId.
         */
        public int getAltarId() {
            return altarId;
        }

        /**
         * @param altarId
         *            the altarId to set.
         */
        public void setAltarId(int altarId) {
            this.altarId = altarId;
        }

        /**
         * @return the rune.
         */
        public Rune getRune() {
            return rune;
        }

        /**
         * @param rune
         *            the rune to set.
         */
        public void setRune(Rune rune) {
            this.rune = rune;
        }

        /**
         * @return the teleport.
         */
        public Position getTeleport() {
            return teleport;
        }

        /**
         * @param teleport
         *            the teleport to set.
         */
        public void setTeleport(Position teleport) {
            this.teleport = teleport;
        }
    }

    /**
     * All of the possible runes to craft.
     * 
     * @author lare96
     */
    public enum Rune {
        AIR(1, 556, 7, new RunecraftingMultiplier(11, 2), new RunecraftingMultiplier(22, 3), new RunecraftingMultiplier(33, 4), new RunecraftingMultiplier(44, 5), new RunecraftingMultiplier(55, 6), new RunecraftingMultiplier(66, 7), new RunecraftingMultiplier(77, 8), new RunecraftingMultiplier(88, 9), new RunecraftingMultiplier(99, 10)),

        MIND(2, 558, 9, new RunecraftingMultiplier(14, 2), new RunecraftingMultiplier(28, 3), new RunecraftingMultiplier(42, 4), new RunecraftingMultiplier(56, 5), new RunecraftingMultiplier(70, 6), new RunecraftingMultiplier(84, 7), new RunecraftingMultiplier(98, 8)),

        WATER(5, 555, 12, new RunecraftingMultiplier(19, 2), new RunecraftingMultiplier(38, 3), new RunecraftingMultiplier(57, 4), new RunecraftingMultiplier(76, 5), new RunecraftingMultiplier(95, 6)),

        EARTH(9, 557, 15, new RunecraftingMultiplier(26, 2), new RunecraftingMultiplier(52, 3), new RunecraftingMultiplier(78, 4)),

        FIRE(14, 554, 18, new RunecraftingMultiplier(35, 2), new RunecraftingMultiplier(70, 3)),

        BODY(20, 559, 20, new RunecraftingMultiplier(46, 2), new RunecraftingMultiplier(92, 3)),

        COSMIC(27, 564, 50, new RunecraftingMultiplier(59, 2)),

        CHAOS(35, 562, 55, new RunecraftingMultiplier(74, 2)),

        NATURE(44, 561, 60, new RunecraftingMultiplier(91, 2)),

        LAW(54, 563, 75, new RunecraftingMultiplier(-1, -1)),

        DEATH(65, 560, 80, new RunecraftingMultiplier(-1, -1)),

        BLOOD(80, 565, 90, new RunecraftingMultiplier(-1, -1)),

        SOUL(95, 566, 100, new RunecraftingMultiplier(-1, -1));

        /**
         * The level needed in order to craft this rune.
         */
        private int levelRequired;

        /**
         * The id of the rune being crafted.
         */
        private int runeId;

        /**
         * Experience you recieve for crafting the rune.
         */
        private int experience;

        /**
         * The levels needed to craft multiple runes.
         */
        private RunecraftingMultiplier[] multipleLevelsRequired;

        /**
         * The constructor for this enum.
         * 
         * @param levelRequired
         *            the level required to craft this rune.
         * @param runeId
         *            the id of the rune.
         * @param experience
         *            the experience from crafting this rune.
         * @param multipleLevelsRequired
         *            the levels needed to craft multiple runes.
         */
        Rune(int levelRequired, int runeId, int experience, RunecraftingMultiplier... multipleLevelsRequired) {
            this.setLevelRequired(levelRequired);
            this.setRuneId(runeId);
            this.setExperience(experience);
            this.setMultipleLevelsRequired(multipleLevelsRequired);
        }

        /**
         * @return the levelRequired.
         */
        public int getLevelRequired() {
            return levelRequired;
        }

        /**
         * @param levelRequired
         *            the levelRequired to set.
         */
        public void setLevelRequired(int levelRequired) {
            this.levelRequired = levelRequired;
        }

        /**
         * @return the runeId.
         */
        public int getRuneId() {
            return runeId;
        }

        /**
         * @param runeId
         *            the runeId to set.
         */
        public void setRuneId(int runeId) {
            this.runeId = runeId;
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
         * @return the multipleLevelsRequired.
         */
        public RunecraftingMultiplier[] getMultipleLevelsRequired() {
            return multipleLevelsRequired;
        }

        /**
         * @param multipleLevelsRequired
         *            the multipleLevelsRequired to set.
         */
        public void setMultipleLevelsRequired(RunecraftingMultiplier[] multipleLevelsRequired) {
            this.multipleLevelsRequired = multipleLevelsRequired;
        }
    }

    /**
     * A mob teleporting a player into an elemental altar or essence mine.
     * 
     * @param player
     *            the player being teleported.
     * @param mob
     *            the mob teleporting the player.
     * @param position
     *            the position the mob is teleporting the player on.
     */
    public void teleport(final Player player, final Mob mob, final Position position) {

        /**
         * Random text that will be said by the mob when teleporting the player.
         */
        final String[] spell = { "Flipendo! scherriko, alast... Gah!", "Leto... Ona... Dupa... Gah!", "Genta... Gah!", "Megatele... Gah!" };

        GameLogic.getSingleton().submit(new Task(1, false, Time.TICK) {
            @Override
            public void logic() {

                /**
                 * Force random text and start animations and graphics for the
                 * player/mob.
                 */
                mob.forceChat((String) Misc.randomElement(spell));
                mob.animation(new Animation(1979));
                mob.gfx(new Gfx(343));

                player.gfx(new Gfx(342));
                this.cancel();
            }
        });

        GameLogic.getSingleton().submit(new Task(3, false, Time.TICK) {
            @Override
            public void logic() {

                /** Teleport the player. */
                player.move(position);
                this.cancel();
            }
        });
    }

    /**
     * A player crafting runes using an altar.
     * 
     * @param player
     *            the player crafting the runes.
     * @param rune
     *            the rune being crafted.
     */
    public void craftRunes(Player player, Rune rune) {

        /** Checks if we are a high enough level to craft these runes. */
        if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(rune.getLevelRequired())) {
            player.getServerPacketBuilder().sendMessage("You need a runecrafting level of " + rune.getLevelRequired() + " to craft " + rune.name().toLowerCase() + " runes.");
            reset(player);
            return;
        }

        /** Checks if we even have rune essence. */
        if (!player.getInventory().getItemContainer().contains(RUNE_ESSENCE)) {
            player.getServerPacketBuilder().sendMessage("You need rune essence to craft " + rune.name().toLowerCase() + " runes.");
            reset(player);
            return;
        }

        /** Checks if there is a multiplier for this rune. */
        if (rune.getMultipleLevelsRequired()[0].getLevelRequired() != -1) {

            /**
             * If there is, check if we are a high enough level for the lowest
             * one.
             */
            if (player.getSkills().getTrainable()[skill().ordinal()].reqLevel(rune.getMultipleLevelsRequired()[0].getLevelRequired())) {

                /**
                 * If we are, make a copy of the multiplier data so we can
                 * modify it.
                 */
                RunecraftingMultiplier[] multiples = rune.getMultipleLevelsRequired().clone();

                /**
                 * Now determine which multipliers we are a high enough level to
                 * craft, and choose the highest one (that we are able to
                 * craft).
                 */
                for (int i = 0; i < multiples.length; i++) {
                    if (!player.getSkills().getTrainable()[skill().ordinal()].reqLevel(multiples[i].getLevelRequired())) {
                        multiples[i].setLevelRequired(0);
                        continue;
                    }

                    if (multiples[i].getLevelRequired() > multiples[0].getLevelRequired()) {
                        multiples[0].setLevelRequired(multiples[i].getLevelRequired());
                    }
                }

                /** Craft the rune with the determined multiplier. */
                int count = player.getInventory().getItemContainer().getCount(RUNE_ESSENCE);

                player.animation(new Animation(791));
                player.gfx(new Gfx(186));
                exp(player, rune.getExperience() * (count * multiples[0].getMultiply()));
                player.getInventory().removeItem(new Item(RUNE_ESSENCE, count));
                player.getInventory().addItem(new Item(rune.getRuneId(), count * multiples[0].getMultiply()));
                reset(player);
                return;
            }
        }

        /** Craft the runes normally with no multipliers. */
        int count = player.getInventory().getItemContainer().getCount(RUNE_ESSENCE);

        player.animation(new Animation(791));
        player.gfx(new Gfx(186));
        exp(player, rune.getExperience() * count);
        player.getInventory().removeItem(new Item(RUNE_ESSENCE, count));
        player.getInventory().addItem(new Item(rune.getRuneId(), count));
        reset(player);
    }

    @Override
    public void reset(Player player) {
        player.getSkillingAction()[index()] = false;
    }

    @Override
    public int index() {
        return SkillManager.RUNECRAFTING;
    }

    @Override
    public Skill skill() {
        return Skill.RUNECRAFTING;
    }

    /**
     * @return the singleton.
     */
    public static Runecrafting getSingleton() {
        if (singleton == null) {
            singleton = new Runecrafting();
        }

        return singleton;
    }

    /**
     * Represents a multiplier for crafting runes.
     * 
     * @author lare96
     */
    public static class RunecraftingMultiplier {

        /**
         * The level required to craft multiple runes.
         */
        private int levelRequired;

        /**
         * The amount to multiply by.
         */
        private int multiply;

        /**
         * The class constructor.
         * 
         * @param levelRequired
         *            the level required for this multiple.
         * @param multiply
         *            the amount to multiply by.
         */
        public RunecraftingMultiplier(int levelRequired, int multiply) {
            this.setLevelRequired(levelRequired);
            this.setMultiply(multiply);
        }

        /**
         * @return the levelRequired.
         */
        public int getLevelRequired() {
            return levelRequired;
        }

        /**
         * @param levelRequired
         *            the levelRequired to set.
         */
        public void setLevelRequired(int levelRequired) {
            this.levelRequired = levelRequired;
        }

        /**
         * @return the multiply.
         */
        public int getMultiply() {
            return multiply;
        }

        /**
         * @param multiply
         *            the multiply to set.
         */
        public void setMultiply(int multiply) {
            this.multiply = multiply;
        }
    }
}
