package server.world.entity.player.content;

import server.net.buffer.PacketBuffer;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.entity.player.container.InventoryContainer;
import server.world.item.Container;
import server.world.item.Item;
import server.world.item.Container.Type;

/**
 * This class contains methods to manage a full trade session with another
 * player.
 * 
 * @author lare96
 */
public class Trade {

    /**
     * The player in this trade session.
     */
    private Player player;

    /**
     * Container of the items this player is offering.
     */
    private Container offering = new Container(Type.STANDARD, InventoryContainer.SIZE);

    /**
     * The other player in this trade session.
     */
    private Player partner;

    /**
     * The trade stage the player is currently in.
     */
    private Stage stage;

    /**
     * If the player has accepted the offer.
     */
    private boolean acceptInitialOffer, acceptConfirmOffer;

    /**
     * Creates a new class to handle a trade session.
     * 
     * @param player
     *            the main player in this trade session.
     */
    public Trade(Player player) {
        this.player = player;
    }

    /**
     * The different stages of this trade session.
     */
    public enum Stage {
        REQUEST,

        OFFER,

        CONFIRM_OFFER
    }

    /**
     * The first stage of a trade session is the request. In this stage, a
     * request is sent to the player to trade.
     * 
     * @param sending
     *            the player being sent this request.
     */
    public void request(Player sending) {
        if (inTrade()) {
            return;
        }

        if (sending.getTrading().getPartner() != null) {
            if (sending.getTrading().getPartner().getUsername().equals(player.getUsername())) {
                setPartner(sending);
                sending.getTrading().setPartner(player);

                setStage(Stage.OFFER);
                sending.getTrading().setStage(Stage.OFFER);

                firstOffer();
                sending.getTrading().firstOffer();
                return;
            }
        }

        setPartner(sending);
        setStage(Stage.REQUEST);
        player.getServerPacketBuilder().sendMessage("Sending trade request...");
        sending.getServerPacketBuilder().sendMessage(player.getUsername() + ":tradereq:");
    }

    /**
     * The second stage of a trade session is the first offer confirmation. In
     * this stage the items you wish to trade can be presented to the other
     * player through the trade screen.
     */
    public void firstOffer() {
        player.getServerPacketBuilder().sendUpdateItems(3322, player.getInventory().getItemContainer().toArray());

        String out = partner.getUsername();

        if (partner.getStaffRights() == 1) {
            out = "@cr1@" + out;
        } else if (partner.getStaffRights() == 2 || partner.getStaffRights() == 3) {
            out = "@cr2@" + out;
        }

        player.getServerPacketBuilder().sendString("Trading with: " + partner.getUsername() + " who has @gre@" + partner.getInventory().getItemContainer().freeSlots() + " free slots", 3417);
        player.getServerPacketBuilder().sendString("", 3431);
        player.getServerPacketBuilder().sendString("Are you sure you want to make this trade?", 3535);
        player.getServerPacketBuilder().sendInventoryInterface(3323, 3321);
    }

    /**
     * The third stage of a trade session is the final offer confirmation. In
     * this stage the items in the trade from both users are presented in a
     * textual form. This stage isn't even really a necessity, but exists merely
     * for security purposes.
     */
    public void confirmTrade() {
        player.getServerPacketBuilder().sendUpdateItems(3214, player.getInventory().getItemContainer().toArray());

        String tradeItems = "Absolutely nothing!";
        String tradeAmount = "";

        int count = 0;

        for (Item item : offering.toArray()) {
            if (item == null || item.getId() < 1 || item.getAmount() < 1) {
                continue;
            }

            if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
                tradeAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + item.getAmount() + ")";
            } else if (item.getAmount() >= 1000000) {
                tradeAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + item.getAmount() + ")";
            } else {
                tradeAmount = "" + item.getAmount();
            }

            if (count == 0) {
                tradeItems = item.getDefinition().getItemName();
            } else {
                tradeItems = tradeItems + "\\n" + item.getDefinition().getItemName();
            }

            if (item.getDefinition().isStackable()) {
                tradeItems = tradeItems + " x " + tradeAmount;
            }

            count++;
        }

        player.getServerPacketBuilder().sendString(tradeItems, 3557);

        tradeItems = "Absolutely nothing!";
        tradeAmount = "";

        count = 0;

        for (Item item : partner.getTrading().getOffering().toArray()) {
            if (item == null || item.getId() < 1 || item.getAmount() < 1) {
                continue;
            }

            if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
                tradeAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + item.getAmount() + ")";
            } else if (item.getAmount() >= 1000000) {
                tradeAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + item.getAmount() + ")";
            } else {
                tradeAmount = "" + item.getAmount();
            }

            if (count == 0) {
                tradeItems = item.getDefinition().getItemName();
            } else {
                tradeItems = tradeItems + "\\n" + item.getDefinition().getItemName();
            }

            if (item.getDefinition().isStackable()) {
                tradeItems = tradeItems + " x " + tradeAmount;
            }

            count++;
        }

        player.getServerPacketBuilder().sendString(tradeItems, 3558);
        player.getServerPacketBuilder().sendInventoryInterface(3443, 3213);
    }

    /**
     * The fourth and last stage of a trade is the item distribution and reset.
     * In this stage, if both players have come to an agreement on what items
     * they want traded and both have clicked accept on the third stage of the
     * trade session, the appropriate items are handed out and everything is
     * reset.
     */
    public void finishTrade() {
        for (Item item : offering.toArray()) {
            if (item == null) {
                continue;
            }

            partner.getInventory().addItem(item);
        }

        for (Item item : partner.getTrading().getOffering().toArray()) {
            if (item == null) {
                continue;
            }

            player.getInventory().addItem(item);
        }

        player.getServerPacketBuilder().closeWindows();
        partner.getServerPacketBuilder().closeWindows();

        partner.getTrading().getOffering().clear();
        offering.clear();

        this.updateThisTrade();
        partner.getTrading().updateThisTrade();
        this.updateOtherTrade();
        partner.getTrading().updateOtherTrade();

        this.setAcceptInitialOffer(false);
        this.setAcceptConfirmOffer(false);
        partner.getTrading().setAcceptInitialOffer(false);
        partner.getTrading().setAcceptConfirmOffer(false);

        partner.getTrading().setStage(null);
        partner.getTrading().setPartner(null);
        setStage(null);
        setPartner(null);
    }

    /**
     * Flag that returns if you are in a trade or not.
     * 
     * @return true if you're in a trade.
     */
    public boolean inTrade() {
        return player.getTrading().getStage() == Stage.OFFER || player.getTrading().getStage() == Stage.CONFIRM_OFFER ? true : false;
    }

    /**
     * Puts an item on the trade screen.
     * 
     * @param item
     *            the item to put on the trade screen.
     * @param slot
     *            the inventory slot it's coming from.
     */
    public void offer(Item item, int slot) {
        if (item.getId() < 1 || item.getAmount() < 1 || item == null) {
            return;
        }

        if (!player.getInventory().getItemContainer().contains(item.getId())) {
            return;
        }

        for (int i : Misc.ITEM_UNTRADEABLE) {
            if (i == item.getId()) {
                player.getServerPacketBuilder().sendMessage("You cannot trade this item.");
                return;
            }
        }

        if (item.getAmount() > player.getInventory().getItemContainer().getCount(item.getId()) && !item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getItemContainer().getCount(item.getId()));
        } else if (item.getAmount() > player.getInventory().getItemContainer().getItem(slot).getAmount() && item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getItemContainer().getItem(slot).getAmount());
        }

        player.getInventory().removeItemSlot(item, slot);
        offering.add(item);

        partner.getServerPacketBuilder().sendString("Trading with: " + player.getUsername() + " who has @gre@" + player.getInventory().getItemContainer().freeSlots() + " free slots", 3417);
        player.getServerPacketBuilder().sendUpdateItems(3322, player.getInventory().getItemContainer().toArray());
        this.updateThisTrade();
        partner.getTrading().updateOtherTrade();
        player.getServerPacketBuilder().sendString("", 3431);
        partner.getServerPacketBuilder().sendString("", 3431);
    }

    /**
     * Takes an item off of the trade screen.
     * 
     * @param item
     *            the item to take off of the trade screen.
     */
    public void unoffer(Item item) {
        if (!offering.contains(item.getId())) {
            return;
        }

        if (item.getAmount() > offering.getCount(item.getId())) {
            item.setAmount(offering.getCount(item.getId()));
        }

        offering.remove(item);
        player.getInventory().addItem(item);

        partner.getServerPacketBuilder().sendString("Trading with: " + player.getUsername() + " who has @gre@" + player.getInventory().getItemContainer().freeSlots() + " free slots", 3417);
        player.getServerPacketBuilder().sendUpdateItems(3322, player.getInventory().getItemContainer().toArray());
        this.updateThisTrade();
        partner.getTrading().updateOtherTrade();
        player.getServerPacketBuilder().sendString("", 3431);
        partner.getServerPacketBuilder().sendString("", 3431);
    }

    /**
     * Resets the trade for the player and their partner.
     * 
     * @param declined
     *            if the trade was manually reset (by declining).
     */
    public void resetTrade(boolean declined) {
        if (!inTrade()) {
            return;
        }

        for (Item item : offering.toArray()) {
            if (item == null) {
                continue;
            }

            player.getInventory().addItem(item);
        }

        for (Item item : partner.getTrading().getOffering().toArray()) {
            if (item == null) {
                continue;
            }

            partner.getInventory().addItem(item);
        }

        player.getServerPacketBuilder().closeWindows();
        partner.getServerPacketBuilder().closeWindows();

        partner.getTrading().getOffering().clear();
        offering.clear();

        this.updateThisTrade();
        partner.getTrading().updateThisTrade();
        this.updateOtherTrade();
        partner.getTrading().updateOtherTrade();

        if (declined) {
            partner.getServerPacketBuilder().sendMessage("The other player has declined the trade!");
        }

        this.setAcceptInitialOffer(false);
        this.setAcceptConfirmOffer(false);
        partner.getTrading().setAcceptInitialOffer(false);
        partner.getTrading().setAcceptConfirmOffer(false);

        partner.getTrading().setStage(null);
        partner.getTrading().setPartner(null);
        setStage(null);
        setPartner(null);
    }

    /**
     * Updates the trading interface with the items from this player.
     */
    public void updateThisTrade() {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2048);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 53);
        out.writeShort(3415);
        out.writeShort(this.getThisTradeAmount());

        for (Item item : this.getOffering().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getId() > 0) {
                if (item.getAmount() > 254) {
                    out.writeByte(255);
                    out.writeInt(item.getAmount(), PacketBuffer.ByteOrder.INVERSE_MIDDLE);
                } else {
                    out.writeByte(item.getAmount());
                }

                out.writeShort(item.getId() + 1, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
            }
        }

        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
    }

    /**
     * Updates the trading interface with the items from the trading partner.
     */
    public void updateOtherTrade() {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2048);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 53);
        out.writeShort(3416);
        out.writeShort(this.getOtherTradeAmount());

        for (Item item : partner.getTrading().getOffering().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getId() > 0) {
                if (item.getAmount() > 254) {
                    out.writeByte(255);
                    out.writeInt(item.getAmount(), PacketBuffer.ByteOrder.INVERSE_MIDDLE);
                } else {
                    out.writeByte(item.getAmount());
                }

                out.writeShort(item.getId() + 1, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
            }
        }

        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
    }

    /**
     * @return the amount of different items in this player's container
     *         excluding null values.
     */
    private int getThisTradeAmount() {
        int total = 0;

        for (Item i : this.getOffering().toArray()) {
            if (i == null) {
                continue;
            }

            if (i.getId() > 0) {
                total++;
            }
        }

        return total;
    }

    /**
     * @return the amount of different items in this player's partner's
     *         container excluding null values.
     */
    private int getOtherTradeAmount() {
        int total = 0;

        for (Item i : partner.getTrading().getOffering().toArray()) {
            if (i == null) {
                continue;
            }

            if (i.getId() > 0) {
                total++;
            }
        }

        return total;
    }

    /**
     * @return the offering.
     */
    public Container getOffering() {
        return offering;
    }

    /**
     * @return the trading.
     */
    public Player getPartner() {
        return partner;
    }

    /**
     * @param trading
     *            the trading to set.
     */
    public void setPartner(Player partner) {
        this.partner = partner;
    }

    /**
     * @return the stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage
     *            the stage to set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * @return the acceptInitialOffer.
     */
    public boolean isAcceptInitialOffer() {
        return acceptInitialOffer;
    }

    /**
     * @param acceptInitialOffer
     *            the acceptInitialOffer to set.
     */
    public void setAcceptInitialOffer(boolean acceptInitialOffer) {
        this.acceptInitialOffer = acceptInitialOffer;
    }

    /**
     * @return the acceptConfirmOffer.
     */
    public boolean isAcceptConfirmOffer() {
        return acceptConfirmOffer;
    }

    /**
     * @param acceptConfirmOffer
     *            the acceptConfirmOffer to set.
     */
    public void setAcceptConfirmOffer(boolean acceptConfirmOffer) {
        this.acceptConfirmOffer = acceptConfirmOffer;
    }
}
