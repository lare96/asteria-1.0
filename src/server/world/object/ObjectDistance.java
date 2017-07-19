package server.world.object;

/**
 * Used for calculating the correct distance between the object and the player.
 * 
 * @author lare96
 */
public class ObjectDistance {

    // FIXME: Find a way to calculate the distance needed for an object using
    // the object size, the object position, and the player's position. For now
    // the default is 1.

    /**
     * Gets the default distance needed to click an object.
     * 
     * @return the default distance.
     */
    public static int getDefault() {
        return 1;
    }
}
