package server.world.entity.mob;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import server.world.entity.player.Player;

/**
 * A conversation between a player and mob. This dialogue system is designed so
 * each whole conversation has its own single id, rather than in winterlove
 * based servers where different parts of every conversation are mashed together
 * in a massive switch statement.
 * 
 * @author lare96
 */
public abstract class MobDialogue {

    // XXX: Was supposed to write my own .txt based scripting syntax that could
    // be parsed with a scanner, rather than creating a new class for every
    // dialogue. I didn't have the time :( most likely in the next release :)

    /**
     * The dialogue id's mapped to the conversation instances.
     */
    private static Map<Integer, MobDialogue> dialogues = new HashMap<Integer, MobDialogue>();

    /**
     * The expressions that an entity can take on during a dialogue.
     */
    public enum Expression {
        HAPPY(588),

        CALM(589),

        CALM_CONTINUED(590),

        DEFAULT(591),

        EVIL(592),

        EVIL_CONTINUED(593),

        DELIGHTED_EVIL(594),

        ANNOYED(595),

        DISTRESSED(596),

        DISTRESSED_CONTINUED(597),

        DISORIENTED_LEFT(600),

        DISORIENTED_RIGHT(601),

        UNINTERESTED(602),

        SLEEPY(603),

        PLAIN_EVIL(604),

        LAUGHING(605),

        LAUGHING_2(608),

        LONGER_LAUGHING(606),

        LONGER_LAUGHING_2(607),

        EVIL_LAUGH_SHORT(609),

        SLIGHTLY_SAD(610),

        SAD(599),

        VERY_SAD(611),

        OTHER(612),

        NEAR_TEARS(598),

        NEAR_TEARS_2(613),

        ANGRY_1(614),

        ANGRY_2(615),

        ANGRY_3(616),

        ANGRY_4(617);

        /**
         * The id of the expression.
         */
        private int id;

        /**
         * Construct a new static expression.
         * 
         * @param id
         *            the id of the expression.
         */
        Expression(int id) {
            this.setId(id);
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
    }

    /**
     * The entire conversation between the player and mob.
     * 
     * @param player
     *            the player taking part in this dialogue.
     */
    public abstract void dialogue(Player player);

    /**
     * The dialogue id that will be mapped to the instance of itself.
     * 
     * @return the id of this dialogue.
     */
    protected abstract int dialogueId();

    /**
     * Loads all dialogues automatically by creating a new instance of each
     * class in the <code>server.world.entity.mob.dialogue</code> and mapping
     * it's id to the instance of itself.
     */
    @SuppressWarnings("unused")
    public static void load() {
        int parsed = 0;

        File[] files = new File("./src/server/world/entity/mob/dialogue/").listFiles();

        for (File f : files) {
            if (f == null) {
                continue;
            }

            try {
                try {
                    try {
                        Object o = Class.forName("server.world.entity.mob.dialogue." + f.getName().replace(".java", "")).newInstance();

                        if (o instanceof MobDialogue) {
                            MobDialogue dialogue = (MobDialogue) o;

                            dialogues.put(dialogue.dialogueId(), dialogue);
                            parsed++;
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
     * Displays one line of npc dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this mob will make.
     * @param text
     *            the text that will be displayed.
     * @param mob
     *            the mob speaking.
     */
    public static void oneLineMobDialogue(Player player, Expression expression, String text, int mob) {
        player.getServerPacketBuilder().interfaceAnimation(4883, expression.getId());
        player.getServerPacketBuilder().sendString(MobDefinition.getMobDefinition()[mob].getName(), 4884);
        player.getServerPacketBuilder().sendString(text, 4885);
        player.getServerPacketBuilder().sendMobHeadModel(mob, 4883);
        player.getServerPacketBuilder().sendChatInterface(4882);
    }

    /**
     * Displays two lines of npc dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this mob will make.
     * @param text
     *            the text that will be displayed.
     * @param text2
     *            the text that will be displayed.
     * @param mob
     *            the mob speaking.
     */
    public static void twoLineMobDialogue(Player player, Expression expression, String text, String text2, int mob) {
        player.getServerPacketBuilder().interfaceAnimation(4888, expression.getId());
        player.getServerPacketBuilder().sendString(MobDefinition.getMobDefinition()[mob].getName(), 4889);
        player.getServerPacketBuilder().sendString(text, 4890);
        player.getServerPacketBuilder().sendString(text2, 4891);
        player.getServerPacketBuilder().sendMobHeadModel(mob, 4901);
        player.getServerPacketBuilder().sendChatInterface(4887);
    }

    /**
     * Displays three lines of npc dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this mob will make.
     * @param text
     *            the text that will be displayed.
     * @param text2
     *            the text that will be displayed.
     * @param text3
     *            the text that will be displayed.
     * @param mob
     *            the mob speaking.
     */
    public static void threeLineMobDialogue(Player player, Expression expression, String text, String text2, String text3, int mob) {
        player.getServerPacketBuilder().interfaceAnimation(4894, expression.getId());
        player.getServerPacketBuilder().sendString(MobDefinition.getMobDefinition()[mob].getName(), 4895);
        player.getServerPacketBuilder().sendString(text, 4896);
        player.getServerPacketBuilder().sendString(text2, 4897);
        player.getServerPacketBuilder().sendString(text3, 4898);
        player.getServerPacketBuilder().sendMobHeadModel(mob, 4894);
        player.getServerPacketBuilder().sendChatInterface(4893);
    }

    /**
     * Displays four lines of npc dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this mob will make.
     * @param text
     *            the text that will be displayed.
     * @param text2
     *            the text that will be displayed.
     * @param text3
     *            the text that will be displayed.
     * @param text4
     *            the text that will be displayed.
     * @param mob
     *            the mob speaking.
     */
    public static void fourLineMobDialogue(Player player, Expression expression, String text1, String text2, String text3, String text4, int mob) {
        player.getServerPacketBuilder().interfaceAnimation(4901, expression.getId());
        player.getServerPacketBuilder().sendString(MobDefinition.getMobDefinition()[mob].getName(), 4902);
        player.getServerPacketBuilder().sendString(text1, 4903);
        player.getServerPacketBuilder().sendString(text2, 4904);
        player.getServerPacketBuilder().sendString(text3, 4905);
        player.getServerPacketBuilder().sendString(text4, 4906);
        player.getServerPacketBuilder().sendString("Click here to continue", 4907);
        player.getServerPacketBuilder().sendMobHeadModel(mob, 4901);
        player.getServerPacketBuilder().sendChatInterface(4900);
    }

    /**
     * Displays one line of player dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this player will make.
     * @param text
     *            the text that will be displayed.
     */
    public static void oneLinePlayerDialogue(Player player, Expression expression, String text) {
        player.getServerPacketBuilder().interfaceAnimation(969, expression.getId());
        player.getServerPacketBuilder().sendString(player.getUsername(), 970);
        player.getServerPacketBuilder().sendString(text, 971);
        player.getServerPacketBuilder().sendString("Click here to continue", 972);
        player.getServerPacketBuilder().sendPlayerHeadModel(969);
        player.getServerPacketBuilder().sendChatInterface(968);
    }

    /**
     * Displays two lines of player dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this player will make.
     * @param text
     *            the text that will be displayed.
     * @param text2
     *            the text that will be displayed.
     */
    public static void twoLinePlayerDialogue(Player player, Expression expression, String text1, String text2) {
        player.getServerPacketBuilder().interfaceAnimation(974, expression.getId());
        player.getServerPacketBuilder().sendString(player.getUsername(), 975);
        player.getServerPacketBuilder().sendString(text1, 976);
        player.getServerPacketBuilder().sendString(text2, 977);
        player.getServerPacketBuilder().sendString("Click here to continue", 978);
        player.getServerPacketBuilder().sendPlayerHeadModel(974);
        player.getServerPacketBuilder().sendChatInterface(973);
    }

    /**
     * Displays three lines of player dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this player will make.
     * @param text
     *            the text that will be displayed.
     * @param text2
     *            the text that will be displayed.
     * @param text3
     *            the text that will be displayed.
     */
    public static void threeLinePlayerDialogue(Player player, Expression expression, String text1, String text2, String text3) {
        player.getServerPacketBuilder().interfaceAnimation(980, expression.getId());
        player.getServerPacketBuilder().sendString(player.getUsername(), 981);
        player.getServerPacketBuilder().sendString(text1, 982);
        player.getServerPacketBuilder().sendString(text2, 983);
        player.getServerPacketBuilder().sendString(text3, 984);
        player.getServerPacketBuilder().sendString("Click here to continue", 985);
        player.getServerPacketBuilder().sendPlayerHeadModel(980);
        player.getServerPacketBuilder().sendChatInterface(979);
    }

    /**
     * Displays four lines of player dialogue to the player.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this player will make.
     * @param text
     *            the text that will be displayed.
     * @param text2
     *            the text that will be displayed.
     * @param text3
     *            the text that will be displayed.
     * @param text4
     *            the text that will be displayed.
     */
    public static void fourLinePlayerDialogue(Player player, Expression expression, String text1, String text2, String text3, String text4) {
        player.getServerPacketBuilder().interfaceAnimation(987, expression.getId());
        player.getServerPacketBuilder().sendString(player.getUsername(), 988);
        player.getServerPacketBuilder().sendString(text1, 989);
        player.getServerPacketBuilder().sendString(text2, 990);
        player.getServerPacketBuilder().sendString(text3, 991);
        player.getServerPacketBuilder().sendString(text4, 992);
        player.getServerPacketBuilder().sendString("Click here to continue", 993);
        player.getServerPacketBuilder().sendPlayerHeadModel(987);
        player.getServerPacketBuilder().sendChatInterface(986);
    }

    /**
     * Displays two options to the player.
     * 
     * @param player
     *            the player to display the options for.
     * @param option
     *            the first option to display.
     * @param option2
     *            the second option to display.
     */
    public static void twoOptions(Player player, String option, String option2) {
        player.getServerPacketBuilder().sendString("Select an Option", 14444);
        player.getServerPacketBuilder().sendString(option, 14445);
        player.getServerPacketBuilder().sendString(option2, 14446);
        player.getServerPacketBuilder().sendChatInterface(14443);
    }

    /**
     * Displays three options to the player.
     * 
     * @param player
     *            the player to display the options for.
     * @param option
     *            the first option to display.
     * @param option2
     *            the second option to display.
     * @param option3
     *            the third option to display.
     */
    public static void threeOptions(Player player, String option, String option2, String option3) {
        player.getServerPacketBuilder().sendString("Select an Option", 2470);
        player.getServerPacketBuilder().sendString(option, 2471);
        player.getServerPacketBuilder().sendString(option2, 2472);
        player.getServerPacketBuilder().sendString(option3, 2473);
        player.getServerPacketBuilder().sendChatInterface(2469);
    }

    /**
     * Displays four options to the player.
     * 
     * @param player
     *            the player to display the options for.
     * @param option
     *            the first option to display.
     * @param option2
     *            the second option to display.
     * @param option3
     *            the third option to display.
     * @param option4
     *            the fourth option to display.
     */
    public static void fourOptions(Player player, String option, String option2, String option3, String option4) {
        player.getServerPacketBuilder().sendString("Select an Option", 8208);
        player.getServerPacketBuilder().sendString(option, 8209);
        player.getServerPacketBuilder().sendString(option2, 8210);
        player.getServerPacketBuilder().sendString(option3, 8211);
        player.getServerPacketBuilder().sendString(option4, 8212);
        player.getServerPacketBuilder().sendChatInterface(8207);
    }

    /**
     * Displays five options to the player.
     * 
     * @param player
     *            the player to display the options for.
     * @param option
     *            the first option to display.
     * @param option2
     *            the second option to display.
     * @param option3
     *            the third option to display.
     * @param option4
     *            the fourth option to display.
     * @param option5
     *            the five option to display.
     */
    public static void fiveOptions(Player player, String option, String option2, String option3, String option4, String option5) {
        player.getServerPacketBuilder().sendString("Select an Option", 8220);
        player.getServerPacketBuilder().sendString(option, 8221);
        player.getServerPacketBuilder().sendString(option2, 8222);
        player.getServerPacketBuilder().sendString(option3, 8223);
        player.getServerPacketBuilder().sendString(option4, 8224);
        player.getServerPacketBuilder().sendString(option5, 8225);
        player.getServerPacketBuilder().sendChatInterface(8219);
    }

    /**
     * Forwards this conversation by one stage.
     * 
     * @param player
     *            the player to forward the dialogue for.
     */
    public void next(Player player) {
        int nextStage = (player.getConversationStage() + 1);

        player.setConversationStage(nextStage);
    }

    /**
     * Resets this dialogue for the player.
     * 
     * @param player
     *            the player to reset the dialogue for.
     */
    public void stop(Player player) {
        player.setConversationStage(0);
        player.setMobDialogue(0);
    }

    /**
     * @return the dialogues.
     */
    public static Map<Integer, MobDialogue> getDialogues() {
        return dialogues;
    }
}
