package server.world.entity.player.skill.impl;

import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.item.Item;

/**
 * Skill class that handles the burying and usage on altar of bones. This skill
 * is trained a little differently in asteria, being that there are a higher
 * variety of bones to bury/use on an altar than in your average server. This
 * skill also makes use of the cooking skill, where you can 'cook' normal bones
 * at level 99 and receive burnt bones. You can use burnt bones on an altar for
 * extremely high experience. When using burnt bones on an altar you have a
 * random chance of getting bonus experience.
 * 
 * @author lare96
 */
public class Prayer extends TrainableSkill {

    /**
     * The singleton instance.
     */
    private static Prayer singleton;

    /**
     * The delay between burying bones.
     */
    private static final int BURY_DELAY = 1200;

    /**
     * The delay between using bones on an altar.
     */
    private static final int ALTAR_DELAY = 3500;

    /**
     * The animation sent when burying a bone.
     */
    private static final Animation BURY_BONE = new Animation(827);

    /**
     * The animation sent when using a bone on an altar.
     */
    private static final Animation BONE_ON_ALTAR = new Animation(896);

    /**
     * All possible bones that can be buried or used on an altar.
     * 
     * @author lare96
     */
    public enum Bone {
        BONES(526, 25),
        BAT_BONES(530, 50),
        MONKEY_BONES(3179, 75),
        WOLF_BONES(2859, 100),
        BIG_BONES(532, 125),
        BABYDRAGON_BONES(534, 150),
        DRAGON_BONES(536, 300),
        JOGRE_BONES(3125, 315),
        ZOGRE_BONES(4812, 330),
        FAYRG_BONES(4830, 345),
        RAURG_BONES(4832, 360),
        OURG_BONES(4834, 375),
        BURNT_BONES(528, 700),
        SHAIKAHAN_BONES(3123, 1000);

        /**
         * The id of the bone.
         */
        private int boneId;

        /**
         * The experience you get for burying this bone.
         */
        private int experience;

        /**
         * Construct new data for a bone.
         * 
         * @param boneId
         *            the bone id.
         * @param experience
         *            the experience from burying this bone or using it on an
         *            altar.
         */
        Bone(int boneId, int experience) {
            this.setBoneId(boneId);
            this.setExperience(experience);
        }

        /**
         * @return the boneId.
         */
        public int getBoneId() {
            return boneId;
        }

        /**
         * @param boneId
         *            the boneId to set.
         */
        public void setBoneId(int boneId) {
            this.boneId = boneId;
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
     * A method that determines what happens when a player buries a bone.
     * 
     * @param player
     *            the player burying the bone.
     * @param bone
     *            the bone being buried.
     * @param slot
     *            the inventory slot the bone is in.
     */
    public void bury(Player player, Bone bone, int slot) {
        if (player.getBuryTimer().elapsed() > BURY_DELAY) {
            /** Check if we have the bone in our inventory. */
            if (player.getInventory().getItemContainer().contains(bone.getBoneId())) {

                /** Bury the bone. */
                player.getMovementQueue().reset();
                player.getSkillingAction()[index()] = true;
                player.animation(BURY_BONE);
                player.getServerPacketBuilder().sendMessage("You bury the " + bone.name().toLowerCase().replaceAll("_", " ") + ".");
                exp(player, bone.getExperience());
                player.getInventory().removeItemSlot(new Item(bone.getBoneId()), slot);

                /** Check for random event. */
                if (Misc.getRandom().nextInt(100) <= 1) {

                    /** Execute random event if eligible. */
                    player.gfx(new Gfx(265));
                    player.getServerPacketBuilder().sendMessage("You have laid a soul to rest! You feel exceptionally blessed by the gods.");
                    exp(player, Misc.getRandom().nextInt(bone.getExperience()));
                }

                /** Reset skill. */
                player.getBuryTimer().reset();
                reset(player);
            }
        }
    }

    /**
     * A method that determines what happens when a player uses a bone on an
     * altar.
     * 
     * @param player
     *            the player using the bone on the altar.
     * @param bone
     *            the bone being used.
     * @param slot
     *            the inventory slot the bone is in.
     */
    public void altar(Player player, Bone bone, int slot) {
        if (player.getAltarTimer().elapsed() > ALTAR_DELAY) {

            /** Check if we have the bone in our inventory. */
            if (player.getInventory().getItemContainer().contains(bone.getBoneId())) {

                /** Use the bone on the altar. */
                player.getMovementQueue().reset();
                player.getSkillingAction()[index()] = true;
                player.animation(BONE_ON_ALTAR);
                player.gfx(new Gfx(247));
                player.getServerPacketBuilder().sendMessage("You use the " + bone.name().toLowerCase().replaceAll("_", " ") + " on the altar.");
                exp(player, (bone.getExperience() * 2));
                player.getInventory().removeItemSlot(new Item(bone.getBoneId()), slot);

                /** Check for random event. */
                if (Misc.getRandom().nextInt(100) <= 1) {

                    /** Execute random event if eligible. */
                    player.gfx(new Gfx(265));
                    player.getServerPacketBuilder().sendMessage("You have laid a soul to rest! You feel exceptionally blessed by the gods.");
                    exp(player, Misc.getRandom().nextInt((bone.getExperience() * 2)));
                }

                /** Reset skill. */
                player.getAltarTimer().reset();
                reset(player);
            }
        }
    }

    /**
     * @return the singleton.
     */
    public static Prayer getSingleton() {
        if (singleton == null) {
            singleton = new Prayer();
        }

        return singleton;
    }

    @Override
    public void reset(Player player) {
        player.getSkillingAction()[index()] = false;
    }

    @Override
    public int index() {
        return SkillManager.PRAYER;
    }

    @Override
    public Skill skill() {
        return Skill.PRAYER;
    }
}
