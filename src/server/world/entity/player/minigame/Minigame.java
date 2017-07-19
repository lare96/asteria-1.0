package server.world.entity.player.minigame;

import server.world.entity.player.Player;
import server.world.map.Location;

/**
 * Useful for implementation of a never ending minigame, like jad and bounty
 * hunter.
 * 
 * @author lare96
 */
public abstract class Minigame {

    /**
     * On login inside of the minigame.
     * 
     * @param player
     *            the player logging in.
     */
    public abstract void login(Player player);

    /**
     * On logout inside of the minigame.
     * 
     * @param player
     *            the player logging out.
     */
    public abstract void logout(Player player);

    /**
     * Starts the minigame.
     */
    public void start() {

    }

    /**
     * Ends the minigame.
     */
    public void end() {

    }

    /**
     * The main area(s) of the minigame, where the effects of everything in here
     * will apply.
     * 
     * @return the area(s) this minigame is in.
     */
    public abstract Location[] minigameLocation();

    /**
     * When the player dies inside the minigame.
     * 
     * @param player
     *            the player who died.
     */
    public void onDeath(Player player) {

    }

    /**
     * If consuming food/potions is allowed.
     * 
     * @return true if players can eat or drink.
     */
    public boolean canEat() {
        return true;
    }

    /**
     * If teleporting is allowed.
     * 
     * @return true if players can teleport.
     */
    public boolean canTeleport() {
        return false;
    }

    /**
     * If you can keep your items on death.
     * 
     * @return true if items are kept on death.
     */
    public boolean canKeepItemsOnDeath() {
        // FIXME: Needs to be integrated with death. This isn't possible yet
        // because an "items kept on death" system is not yet made.
        return true;
    }

    /**
     * If player vs player combat is allowed.
     * 
     * @return true if players can fight.
     */
    public boolean canFight() {
        return true;
    }
}
