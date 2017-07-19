package server.world.entity;

/**
 * Holds update flags for all entities.
 * 
 * @author lare96
 */
public class UpdateFlags {

    /**
     * The update flags.
     */
    private boolean[] flags = new boolean[10];

    /**
     * Flag an update flag.
     * 
     * @param flag
     *            the update flag you are flagging.
     */
    public void flag(Flag flag) {
        flags[flag.ordinal()] = true;
    }

    /**
     * Gets the value of an update flag.
     * 
     * @param flag
     *            the flag to get the value of.
     * @return the value of the flag.
     */
    public boolean get(Flag flag) {
        return flags[flag.ordinal()];
    }

    /**
     * @return if an update is required.
     */
    public boolean isUpdateRequired() {
        for (int i = 0; i < flags.length; i++)
            if (flags[i])
                return true;
        return false;
    }

    /**
     * Resets the update flags.
     */
    public void reset() {
        for (int i = 0; i < flags.length; i++) {
            flags[i] = false;
        }
    }

    /**
     * Enum which holds data for each update flag.
     * 
     * @author lare96
     */
    public enum Flag {

        /**
         * Appearance update.
         */
        APPEARANCE,

        /**
         * Chat update.
         */
        CHAT,

        /**
         * Graphics update.
         */
        GRAPHICS,

        /**
         * Animation update.
         */
        ANIMATION,

        /**
         * Forced chat update.
         */
        FORCED_CHAT,

        /**
         * Interacting entity update.
         */
        FACE_ENTITY,

        /**
         * Face coordinate entity update.
         */
        FACE_COORDINATE,

        /**
         * Hit update.
         */
        HIT,

        /**
         * Hit 2 update.
         */
        HIT_2,

        /**
         * Update flag used to transform npc to another.
         */
        TRANSFORM
    }
}
