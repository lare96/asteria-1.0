package server.world.entity.player.skill;

import server.world.entity.Gfx;
import server.world.entity.player.Player;

/**
 * A utility class for managing skills.
 * 
 * @author lare96
 */
public class SkillManager {

    /**
     * The singleton instance for this class.
     */
    private static SkillManager singleton;

    /**
     * The indexes for all of the non-combat skills.
     */
    public static final int

    PRAYER = 0,

    COOKING = 1,

    WOODCUTTING = 2,

    FLETCHING = 3,

    FISHING = 4,

    FIREMAKING = 5,

    CRAFTING = 6,

    SMITHING = 7,

    MINING = 8,

    HERBLORE = 9,

    AGILITY = 10,

    THIEVING = 11,

    SLAYER = 12,

    FARMING = 13,

    RUNECRAFTING = 14;

    /**
     * Holds important data for each skill.
     */
    public enum Skill {
        ATTACK(6248, 6249, 6247, true, 4004, 4005, 4044, 4045),

        DEFENCE(6254, 6255, 6253, false, 4008, 4009, 4056, 4057),

        STRENGTH(6207, 6208, 6206, false, 4006, 4007, 4050, 4051),

        HITPOINTS(6217, 6218, 6216, false, 4016, 4017, 4080, 4081),

        RANGED(5453, 6114, 4443, false, 4010, 4011, 4062, 4063),

        PRAYER(6243, 6244, 6242, false, 4012, 4013, 4068, 4069),

        MAGIC(6212, 6213, 6211, false, 4014, 4015, 4074, 4075),

        COOKING(6227, 6228, 6226, false, 4034, 4035, 4134, 4135),

        WOODCUTTING(4273, 4274, 4272, false, 4038, 4039, 4146, 4147),

        FLETCHING(6232, 6233, 6231, false, 4026, 4027, 4110, 4111),

        FISHING(6259, 6260, 6258, false, 4032, 4033, 4128, 4129),

        FIREMAKING(4283, 4284, 4282, false, 4036, 4037, 4140, 4141),

        CRAFTING(6264, 6265, 6263, false, 4024, 4025, 4104, 4105),

        SMITHING(6222, 6223, 6221, false, 4030, 4031, 4122, 4123),

        MINING(4417, 4438, 4416, false, 4028, 4029, 4116, 4117),

        HERBLORE(6238, 6239, 6237, false, 4020, 4021, 4092, 4093),

        AGILITY(4278, 4279, 4277, true, 4018, 4019, 4086, 4087),

        THIEVING(4263, 4264, 4261, false, 4022, 4023, 4098, 4099),

        SLAYER(12123, 12124, 12122, false, 12166, 12167, 12171, 12172),

        FARMING(4889, 4890, 4887, false, 13926, 13927, 13921, 13922),

        RUNECRAFTING(4268, 4269, 4267, false, 4152, 4153, 4157, 4158);

        /**
         * The level up line.
         */
        private int firstLine, secondLine;

        /**
         * The chatbox interface.
         */
        private int sendChatbox;

        /**
         * If 'a' or 'an' should be used (true for 'an', false for 'a').
         */
        private boolean grammar;

        /**
         * The four refresh lines.
         */
        private int refreshOne, refreshTwo, refreshThree, refreshFour;

        /**
         * Construct the data.
         * 
         * @param firstLine
         *            the id of the first line sent when leveling up.
         * @param secondLine
         *            the id of the second line sent when leveling up.
         * @param sendChatbox
         *            the chatbox interface.
         * @param grammar
         *            if 'a' or 'an' should be used (true for 'an', false for
         *            'a').
         * @param refreshOne
         *            the id of the first refresh line.
         * @param refreshTwo
         *            the id of the second refresh line.
         * @param refreshThree
         *            the id of the third refresh line.
         * @param refreshFour
         *            the id of the fourth refresh line.
         */
        Skill(int firstLine, int secondLine, int sendChatbox, boolean grammar, int refreshOne, int refreshTwo, int refreshThree, int refreshFour) {
            this.setFirstLine(firstLine);
            this.setSecondLine(secondLine);
            this.setSendChatbox(sendChatbox);
            this.setGrammar(grammar);
            this.setRefreshOne(refreshOne);
            this.setRefreshTwo(refreshTwo);
            this.setRefreshThree(refreshThree);
            this.setRefreshFour(refreshFour);
        }

        /**
         * @return the firstLine.
         */
        public int getFirstLine() {
            return firstLine;
        }

        /**
         * @param firstLine
         *            the firstLine to set.
         */
        public void setFirstLine(int firstLine) {
            this.firstLine = firstLine;
        }

        /**
         * @return the secondLine.
         */
        public int getSecondLine() {
            return secondLine;
        }

        /**
         * @param secondLine
         *            the secondLine to set.
         */
        public void setSecondLine(int secondLine) {
            this.secondLine = secondLine;
        }

        /**
         * @return the sendChatbox.
         */
        public int getSendChatbox() {
            return sendChatbox;
        }

        /**
         * @param sendChatbox
         *            the sendChatbox to set.
         */
        public void setSendChatbox(int sendChatbox) {
            this.sendChatbox = sendChatbox;
        }

        /**
         * @return the grammar.
         */
        public boolean isGrammar() {
            return grammar;
        }

        /**
         * @param grammar
         *            the grammar to set.
         */
        public void setGrammar(boolean grammar) {
            this.grammar = grammar;
        }

        /**
         * @return the refreshOne.
         */
        public int getRefreshOne() {
            return refreshOne;
        }

        /**
         * @param refreshOne
         *            the refreshOne to set.
         */
        public void setRefreshOne(int refreshOne) {
            this.refreshOne = refreshOne;
        }

        /**
         * @return the refreshTwo.
         */
        public int getRefreshTwo() {
            return refreshTwo;
        }

        /**
         * @param refreshTwo
         *            the refreshTwo to set.
         */
        public void setRefreshTwo(int refreshTwo) {
            this.refreshTwo = refreshTwo;
        }

        /**
         * @return the refreshThree.
         */
        public int getRefreshThree() {
            return refreshThree;
        }

        /**
         * @param refreshThree
         *            the refreshThree to set.
         */
        public void setRefreshThree(int refreshThree) {
            this.refreshThree = refreshThree;
        }

        /**
         * @return the refreshFour.
         */
        public int getRefreshFour() {
            return refreshFour;
        }

        /**
         * @param refreshFour
         *            the refreshFour to set.
         */
        public void setRefreshFour(int refreshFour) {
            this.refreshFour = refreshFour;
        }
    }

    /**
     * Level up a certain skill.
     * 
     * @param player
     *            the player leveling up.
     * @param skill
     *            the skill advancing a level.
     */
    public void levelUp(Player player, Skill skill) {
        /** Calculate the new total level. */
        int totalLevel = 0;

        for (Skill s : Skill.values()) {
            totalLevel += player.getSkills().getTrainable()[s.ordinal()].getLevelForExperience();
        }

        /** Send the player an indication that they have leveled up. */
        player.getServerPacketBuilder().sendString("Total Lvl: " + totalLevel, 3984);
        player.getServerPacketBuilder().sendString(skill.isGrammar() ? "Congratulations, you've just advanced an " + skill.name().toLowerCase().replaceAll("_", " ") + " level!" : "Congratulations, you've just advanced a " + skill.name().toLowerCase().replaceAll("_", " ") + " level!", skill.getFirstLine());
        player.getServerPacketBuilder().sendString("Your " + skill.name().toLowerCase().replaceAll("_", " ") + " level is now " + player.getSkills().getTrainable()[skill.ordinal()].getLevel() + ".", skill.getSecondLine());
        player.getServerPacketBuilder().sendMessage(skill.isGrammar() ? "Congratulations, you've just advanced an " + skill.name().toLowerCase().replaceAll("_", " ") + " level!" : "Congratulations, you've just advanced a " + skill.name().toLowerCase().replaceAll("_", " ") + " level!");
        player.getServerPacketBuilder().sendChatInterface(skill.getSendChatbox());
    }

    /**
     * Refreshes a players skill.
     * 
     * @param player
     *            the player refreshing the skill.
     * @param skill
     *            the skill being refreshed.
     */
    public void refresh(Player player, Skill skill) {
        if (player.getSkills().getTrainable()[skill.ordinal()] == null) {
            player.getSkills().getTrainable()[skill.ordinal()] = new Trainable();
            if (skill.ordinal() == 3) {
                player.getSkills().getTrainable()[skill.ordinal()].setLevel(10);
                player.getSkills().getTrainable()[skill.ordinal()].setExperience(1300);
            }
        }

        player.getServerPacketBuilder().sendString("" + player.getSkills().getTrainable()[skill.ordinal()].getLevel() + "", skill.getRefreshOne());
        player.getServerPacketBuilder().sendString("" + player.getSkills().getTrainable()[skill.ordinal()].getLevelForExperience() + "", skill.getRefreshTwo());
        player.getServerPacketBuilder().sendString("" + player.getSkills().getTrainable()[skill.ordinal()].getExperience() + "", skill.getRefreshThree());
        player.getServerPacketBuilder().sendString("" + player.getSkills().getTrainable()[skill.ordinal()].getExperienceForNextLevel() + "", skill.getRefreshFour());
    }

    /**
     * Adds experience.
     * 
     * @param amount
     *            the amount of exp being given.
     * @param skill
     *            the skill the exp is added too.
     * @param player
     *            the player being granted the exp.
     */
    public void addExperience(int amount, Skill skill, Player player) {
        if (amount + player.getSkills().getTrainable()[skill.ordinal()].getExperience() < 0 || player.getSkills().getTrainable()[skill.ordinal()].getExperience() > 2000000000) {
            return;
        }
        int oldLevel = player.getSkills().getTrainable()[skill.ordinal()].getLevelForExperience();
        int experience = player.getSkills().getTrainable()[skill.ordinal()].getExperience();

        player.getSkills().getTrainable()[skill.ordinal()].setExperience(experience + amount);
        if (oldLevel < player.getSkills().getTrainable()[skill.ordinal()].getLevelForExperience()) {
            if (skill.ordinal() != 3) { // hp doesn't level
                player.getSkills().getTrainable()[skill.ordinal()].setLevel(player.getSkills().getTrainable()[skill.ordinal()].getLevelForExperience());
            } else {
                int old = player.getSkills().getTrainable()[skill.ordinal()].getLevel();

                player.getSkills().getTrainable()[skill.ordinal()].setLevel(old + 1);
            }
            levelUp(player, skill);
            player.gfx(new Gfx(199));
        }

        player.getServerPacketBuilder().sendSkill(skill.ordinal(), player.getSkills().getTrainable()[skill.ordinal()].getLevel(), player.getSkills().getTrainable()[skill.ordinal()].getExperience());
        SkillManager.getSingleton().refresh(player, skill);
    }

    /**
     * Calculate the total level.
     * 
     * @param player
     *            the player to calculate the total level of.
     * @return the total level.
     */
    public int totalLevel(Player player) {
        /** Calculate the new total level. */
        int totalLevel = 0;

        for (Skill s : Skill.values()) {
            totalLevel += player.getSkills().getTrainable()[s.ordinal()].getLevelForExperience();
        }

        return totalLevel;
    }

    /**
     * Refreshes all the skills.
     * 
     * @param player
     *            the player to refresh all skills for.
     */
    public void refreshAll(Player player) {
        /** New local variable. */
        int totalLevel = 0;

        /** Refresh total level and stats. */
        for (Skill s : Skill.values()) {
            refresh(player, s);
            totalLevel += player.getSkills().getTrainable()[s.ordinal()].getLevelForExperience();
        }

        /** Send new total level. */
        player.getServerPacketBuilder().sendString("Total Lvl: " + totalLevel, 3984);
    }

    /**
     * Refreshes skills on login.
     * 
     * @param player
     *            the player to refresh.
     */
    public void login(Player player) {
        player.setSkills(new SkillContainer());

        for (int i = 0; i < player.getSkills().getTrainable().length; i++) {
            player.getSkills().getTrainable()[i] = new Trainable();

            if (i == 3) {
                player.getSkills().getTrainable()[i].setLevel(10);
                player.getSkills().getTrainable()[i].setExperience(1300);
            }
        }
    }

    /**
     * @return the singleton instance.
     */
    public static SkillManager getSingleton() {
        if (singleton == null) {
            singleton = new SkillManager();
        }

        return singleton;
    }
}
