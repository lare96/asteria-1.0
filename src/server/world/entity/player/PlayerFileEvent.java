package server.world.entity.player;

import java.io.File;

/**
 * Used for reading and writing operations to character files.
 * 
 * @author lare96
 */
public abstract class PlayerFileEvent {

    /**
     * The player taking part in this operation.
     */
    private Player player;

    /**
     * Construct a new player file operation.
     * 
     * @param player
     *            the player taking part in this operation.
     */
    public PlayerFileEvent(Player player) {
        this.setPlayer(player);
    }

    /**
     * The operation to perform.
     */
    public abstract void run();

    /**
     * The file to perform the operation on.
     */
    public abstract File file();

    /**
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param player
     *            the player to set.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
}
