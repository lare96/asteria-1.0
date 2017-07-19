package server.world.entity.player.content;

import server.util.Misc;
import server.world.World;
import server.world.entity.player.Player;

/**
 * Handles the sending of a private message from one player to another.
 * 
 * @author lare96
 */
public class PrivateMessage {

    /**
     * The player sending the message.
     */
    private Player player;

    /**
     * The last private messaging id.
     */
    private int lastPrivateMessageId = 1;

    /**
     * Instantiate this class.
     * 
     * @param player
     *            the player.
     */
    public PrivateMessage(Player player) {
        this.player = player;
    }

    /**
     * Refreshes the friends list on login for yourself and players you have
     * added.
     */
    public void sendPrivateMessageOnLogin() {

        /** Sends the private messaging list for this player. */
        player.getServerPacketBuilder().sendPrivateMessagingList(2);

        /** Updates the list with all your friends. */
        for (long l : player.getFriends()) {
            if (l == 0) {
                continue;
            }

            Player load = World.getPlayer(Misc.longToName(l));

            if (load != null) {
                player.getServerPacketBuilder().loadPrivateMessage(l, 1);
            } else {
                player.getServerPacketBuilder().loadPrivateMessage(l, 0);
            }
        }

        /** Updates the list for all your friends. */
        long name = Misc.nameToLong(player.getUsername());

        for (Player players : World.getPlayers()) {
            if (players == null)
                continue;

            if (players.getFriends().contains(name)) {
                players.getServerPacketBuilder().loadPrivateMessage(name, 1);
            }
        }
    }

    /**
     * Refreshes the friends list on logout for players you have added.
     */
    public void sendPrivateMessageOnLogout() {

        /** Updates the list for all your friends. */
        long name = Misc.nameToLong(player.getUsername());

        for (Player players : World.getPlayers()) {
            if (players == null)
                continue;

            if (players.getFriends().contains(name)) {
                players.getServerPacketBuilder().loadPrivateMessage(name, 0);
            }
        }
    }

    /**
     * Add someone to your friends list.
     * 
     * @param name
     *            the name in a long.
     */
    public void addFriend(long name) {

        /** Block if the friends list is full. */
        if (player.getFriends().size() >= 200) {
            player.getServerPacketBuilder().sendMessage("Your friends list is full.");
            return;
        }

        /**
         * Block if the person you are trying to add is already on your friends
         * list.
         */
        if (player.getFriends().contains(name)) {
            player.getServerPacketBuilder().sendMessage("" + Misc.longToName(name) + " is already on your friends list.");
            return;
        }

        /** Add the name to your friends list. */
        player.getFriends().add(name);
        player.getServerPacketBuilder().loadPrivateMessage(name, 1);

        long playerName = Misc.nameToLong(player.getUsername());

        for (Player players : World.getPlayers()) {
            if (players == null)
                continue;

            if (players.getFriends().contains(playerName)) {
                players.getServerPacketBuilder().loadPrivateMessage(playerName, 1);
            }
        }
    }

    /**
     * Add someone to your ignores list.
     * 
     * @param name
     *            the name in a long.
     */
    public void addIgnore(long name) {

        /** Block if the ignores list is full. */
        if (player.getIgnores().size() >= 100) {
            player.getServerPacketBuilder().sendMessage("Your ignores list is full.");
            return;
        }

        /**
         * Block if the person you are trying to add is already on your ignores
         * list.
         */
        if (player.getIgnores().contains(name)) {
            player.getServerPacketBuilder().sendMessage("" + Misc.longToName(name) + " is already on your ignores list.");
            return;
        }

        /** Add the name to your ignores list. */
        player.getIgnores().add(name);
    }

    /**
     * Remove a friend from your friends list.
     * 
     * @param name
     *            the name in a long.
     */
    public void removeFriend(long name) {
        if (player.getFriends().contains(name)) {
            player.getFriends().remove(name);
        } else {
            player.getServerPacketBuilder().sendMessage("" + Misc.longToName(name) + " is not even on your friends list...");
        }
    }

    /**
     * Remove an ignore from your ignores list.
     * 
     * @param name
     *            the name in a long.
     */
    public void removeIgnore(long name) {
        if (player.getIgnores().contains(name)) {
            player.getIgnores().remove(name);
        } else {
            player.getServerPacketBuilder().sendMessage("" + Misc.longToName(name) + " is not even on your ignores list...");
        }
    }

    /**
     * Sends a private message to another player.
     * 
     * @param from
     *            the player sending the message.
     * @param to
     *            the player being sent the message.
     * @param message
     *            the message in bytes.
     * @param messageSize
     *            the size of the message.
     */
    public void sendPrivateMessage(Player from, long to, byte[] message, int messageSize) {
        for (Player p : player.getPlayers()) {
            if (p != null) {
                if (Misc.nameToLong(p.getUsername()) == to) {
                    p.getServerPacketBuilder().sendPrivateMessage(Misc.nameToLong(from.getUsername()), from.getStaffRights(), message, messageSize);
                }
            }
        }
    }

    /**
     * @return your last private message id + 1.
     */
    public int getLastPrivateMessageId() {
        return lastPrivateMessageId++;
    }
}
