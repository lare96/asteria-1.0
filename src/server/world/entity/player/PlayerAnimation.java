package server.world.entity.player;

/**
 * Represents all of the standard player animations for updating.
 * 
 * @author lare96
 */
public class PlayerAnimation {

    /**
     * The standing animation.
     */
    private static int standEmote = 0x328;

    /**
     * The turning animation.
     */
    private static int standTurnEmote = 0x337;

    /**
     * The walking animation.
     */
    private static int walkEmote = 0x333;

    /**
     * The turning 180 degrees animation.
     */
    private static int turn180Emote = 0x334;

    /**
     * The turning 90 degrees clockwise animation.
     */
    private static int turn90CWEmote = 0x335;

    /**
     * The turning 90 degrees counter-clockwise animation.
     */
    private static int turn90CCWEmote = 0x336;

    /**
     * The running animation.
     */
    private static int runEmote = 0x338;

    /**
     * @return the standEmote.
     */
    public static int getStandEmote() {
        return standEmote;
    }

    /**
     * @return the standTurnEmote.
     */
    public static int getStandTurnEmote() {
        return standTurnEmote;
    }

    /**
     * @return the walkEmote.
     */
    public static int getWalkEmote() {
        return walkEmote;
    }

    /**
     * @return the turn180Emote.
     */
    public static int getTurn180Emote() {
        return turn180Emote;
    }

    /**
     * @return the turn90CWEmote.
     */
    public static int getTurn90CWEmote() {
        return turn90CWEmote;
    }

    /**
     * @return the turn90CCWEmote.
     */
    public static int getTurn90CCWEmote() {
        return turn90CCWEmote;
    }

    /**
     * @return the runEmote.
     */
    public static int getRunEmote() {
        return runEmote;
    }
}
