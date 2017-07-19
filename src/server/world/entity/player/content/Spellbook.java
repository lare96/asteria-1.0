package server.world.entity.player.content;

import server.world.entity.Teleport;
import server.world.entity.player.Player;

/**
 * Enum that holds data for all the possible spellbooks that can be used.
 * 
 * @author lare96
 */
public enum Spellbook {

    NORMAL(1151, Teleport.NORMAL_SPELLBOOK_TELEPORT),

    ANCIENT(12855, Teleport.ANCIENTS_SPELLBOOK_TELEPORT);

    /**
     * The interface of the spellbook.
     */
    private int sidebarInterface;

    /**
     * The teleport type this spellbook uses.
     */
    private Teleport teleport;

    /**
     * Construct a new spellbook.
     * 
     * @param sidebarInterface
     *            the sidebar interface for this spellbook.
     * @param teleport
     *            the teleport type this spellbook uses.
     */
    Spellbook(int sidebarInterface, Teleport teleport) {
        this.setSidebarInterface(sidebarInterface);
        this.setTeleport(teleport);
    }

    /**
     * Converts the player to a different magic type.
     * 
     * @param player
     *            the player to convert.
     * @param book
     *            the book to convert to.
     */
    public static void convert(Player player, Spellbook book) {
        player.getServerPacketBuilder().sendSidebarInterface(6, book.getSidebarInterface());
        player.setSpellbook(book);
        player.getServerPacketBuilder().sendMessage("You convert to " + book.name().toLowerCase().replaceAll("_", " ") + " magicks!");
    }

    /**
     * @return the sidebarInterface.
     */
    public int getSidebarInterface() {
        return sidebarInterface;
    }

    /**
     * @param sidebarInterface
     *            the sidebarInterface to set.
     */
    public void setSidebarInterface(int sidebarInterface) {
        this.sidebarInterface = sidebarInterface;
    }

    /**
     * @return the teleport.
     */
    public Teleport getTeleport() {
        return teleport;
    }

    /**
     * @param teleport
     *            the teleport to set.
     */
    public void setTeleport(Teleport teleport) {
        this.teleport = teleport;
    }
}
