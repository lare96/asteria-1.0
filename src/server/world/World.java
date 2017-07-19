package server.world;

import server.logic.GameLogic;
import server.util.ServerGUI;
import server.world.entity.Entity;
import server.world.entity.mob.Mob;
import server.world.entity.mob.MobUpdate;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerUpdate;
import server.world.entity.player.file.WritePlayerFileEvent;

/**
 * Handles all logged in entities.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class World {

    /** All registered players. */
    private static final Player[] players = new Player[2048];

    /** All registered NPCs. */
    private static final Mob[] npcs = new Mob[8192];

    /** The names of registered players. */
    private static final String[] names = new String[2048];

    /**
     * Flag that determines whether items can be dropped.
     */
    private static boolean canDrop = true;

    /**
     * Flag that determines whether items can be traded.
     */
    private static boolean canTrade = true;

    /**
     * Flag that determines whether players can shop for items.
     */
    private static boolean canShop = true;

    /**
     * Flag that determines whether items can be picked up.
     */
    private static boolean canPickup = true;

    /**
     * Flag that determines if a shutdown is still in progress.
     */
    private static boolean shutdownInProgress = false;

    /**
     * Performs the processing of all entities.
     * 
     * @throws Exception
     *             if any general errors occur during processing.
     */
    public static void process() throws Exception {
        // TODO: Maybe we could implement loop fusion to speed this up.

        /** Perform any logic processing for players. */
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (player == null) {
                continue;
            }
            try {
                player.engineWork();
            } catch (Exception ex) {
                ex.printStackTrace();
                player.getNetwork().disconnect();
            }
        }

        /** Perform any logic processing for NPCs. */
        for (int i = 0; i < npcs.length; i++) {
            Mob npc = npcs[i];
            if (npc == null) {
                continue;
            }
            try {
                npc.engineWork();
            } catch (Exception ex) {
                ex.printStackTrace();
                unregister(npc);
            }
        }

        /** Update all players. */
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (player == null) {
                continue;
            }
            try {
                PlayerUpdate.update(player);
                MobUpdate.update(player);
            } catch (Exception ex) {
                ex.printStackTrace();
                player.getNetwork().disconnect();
            }
        }

        /** Reset all players after cycle. */
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (player == null) {
                continue;
            }
            try {
                player.reset();
            } catch (Exception ex) {
                ex.printStackTrace();
                player.getNetwork().disconnect();
            }
        }

        /** Reset all NPCs after cycle. */
        for (int i = 0; i < npcs.length; i++) {
            Mob npc = npcs[i];
            if (npc == null) {
                continue;
            }
            try {
                npc.reset();
            } catch (Exception ex) {
                ex.printStackTrace();
                unregister(npc);
            }
        }
    }

    /**
     * Registers an entity for processing.
     * 
     * @param entity
     *            the entity to register.
     */
    public static void register(Entity entity) {
        entity.register();
    }

    /**
     * Unregisters an entity from processing.
     * 
     * @param entity
     *            the entity to unregister.
     */
    public static void unregister(Entity entity) {
        entity.unregister();
    }

    /**
     * Sends a message to all online players.
     * 
     * @param message
     *            the message to send.
     */
    public static void sendMessage(String message) {
        for (Player p : players) {
            if (p == null) {
                continue;
            }

            p.getServerPacketBuilder().sendMessage(message);
        }
    }

    /**
     * Initiates the shutdown sequence for this server that will shutdown the
     * server in a <b>safe</b>, orderly fashion. If the server is not shutdown
     * this way, important data that needs to be saved might be lost.
     */
    public static void shutdown() throws InterruptedException {
        if (isShutdownInProgress()) {
            return;
        }

        setShutdownInProgress(true);

        for (Player player : getPlayers()) {
            if (player == null) {
                continue;
            }

            player.getTrading().resetTrade(false);
        }

        World.savePlayers();
        ServerGUI.save();
        GameLogic.getSingleton().shutdown();
        System.exit(0);
    }

    /**
     * Saves all registered players.
     */
    public static void savePlayers() {
        for (Player player : getPlayers()) {
            if (player == null) {
                continue;
            }

            WritePlayerFileEvent write = new WritePlayerFileEvent(player);
            write.run();
        }
    }

    /**
     * Gets an instance of a player by their name.
     * 
     * @param player
     *            the name of the player you are trying to get the instance of.
     * @return the instance of the player, null if no player with that name was
     *         found.
     */
    public static Player getPlayer(String player) {
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getUsername().equals(player)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Gets the amount of players that are online.
     * 
     * @return the amount of online players.
     */
    public static int playerAmount() {
        int amount = 0;
        for (int i = 1; i < players.length; i++) {
            if (players[i] != null) {
                amount++;
            }
        }
        return amount;
    }

    /**
     * Gets the amount of NPCs that are online.
     * 
     * @return the amount of online NPCs.
     */
    public static int npcAmount() {
        int amount = 0;
        for (int i = 1; i < npcs.length; i++) {
            if (npcs[i] != null) {
                amount++;
            }
        }
        return amount;
    }

    /**
     * Gets all registered players.
     * 
     * @return the players.
     */
    public static Player[] getPlayers() {
        return players;
    }

    /**
     * Gets all registered NPCs.
     * 
     * @return the npcs.
     */
    public static Mob[] getNpcs() {
        return npcs;
    }

    /**
     * Gets the name of every player online.
     * 
     * @return the names.
     */
    public static String[] getPlayerNames() {
        for (int i = 0; i < names.length; i++) {
            names[i] = null;
        }

        for (Player player : players) {
            if (player == null) {
                continue;
            }

            names[player.getSlot()] = player.getUsername();
        }

        return names;
    }

    /**
     * @return the canDrop.
     */
    public static boolean isCanDrop() {
        return canDrop;
    }

    /**
     * @param canDrop
     *            the canDrop to set.
     */
    public static void setCanDrop(boolean canDrop) {
        World.canDrop = canDrop;
    }

    /**
     * @return the canTrade.
     */
    public static boolean isCanTrade() {
        return canTrade;
    }

    /**
     * @param canTrade
     *            the canTrade to set.
     */
    public static void setCanTrade(boolean canTrade) {
        World.canTrade = canTrade;
    }

    /**
     * @return the canShop.
     */
    public static boolean isCanShop() {
        return canShop;
    }

    /**
     * @param canShop
     *            the canShop to set.
     */
    public static void setCanShop(boolean canShop) {
        World.canShop = canShop;
    }

    /**
     * @return the canPickup.
     */
    public static boolean isCanPickup() {
        return canPickup;
    }

    /**
     * @param canPickup
     *            the canPickup to set.
     */
    public static void setCanPickup(boolean canPickup) {
        World.canPickup = canPickup;
    }

    /**
     * @return the shutdownInProgress.
     */
    public static boolean isShutdownInProgress() {
        return shutdownInProgress;
    }

    /**
     * @param shutdownInProgress
     *            the shutdownInProgress to set.
     */
    public static void setShutdownInProgress(boolean shutdownInProgress) {
        World.shutdownInProgress = shutdownInProgress;
    }
}
