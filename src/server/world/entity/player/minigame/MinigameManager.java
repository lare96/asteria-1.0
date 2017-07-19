package server.world.entity.player.minigame;

import java.util.HashMap;
import java.util.Map;

import server.world.entity.player.Player;
import server.world.entity.player.minigame.impl.ExampleMinigame;
import server.world.map.Location;

/**
 * Instantiates new minigames and manages previously instantiated minigames.
 * 
 * @author lare96
 */
public class MinigameManager {

    /**
     * Map holding data for all instantiated minigames.
     */
    private static Map<String, Minigame> minigames = new HashMap<String, Minigame>();

    /**
     * Puts all of the minigames into a map.
     */
    public static void load() {
        minigames.put("Example Minigame", new ExampleMinigame());
    }

    /**
     * Checks if the designated player is participating in any minigames, and
     * returns the instance of the minigame the player is in (returns null if
     * the player is not in a minigame).
     * 
     * @param player
     *            the player to check for.
     * @return the minigame the player is in.
     */
    public static Minigame inAnyMinigame(Player player) {
        for (Minigame m : minigames.values()) {
            if (m == null) {
                continue;
            }

            if (player.getPosition().inLocation(getLocations(m))) {
                return m;
            }
        }
        return null;
    }

    /**
     * Gets all of the locations of a certain minigame.
     * 
     * @param m
     *            the minigame to get the locations of.
     * @return the locations.
     */
    public static Location getLocations(Minigame m) {
        if (m.minigameLocation() != null) {
            for (Location l : m.minigameLocation()) {
                if (l == null) {
                    continue;
                }

                return l;
            }
        }
        return null;
    }

    /**
     * Gets the instances of all of the loaded minigames.
     * 
     * @return the minigames.
     */
    public static Minigame getMinigames() {
        for (Minigame all : minigames.values()) {
            if (all == null) {
                continue;
            }

            return all;
        }
        return null;
    }
}
