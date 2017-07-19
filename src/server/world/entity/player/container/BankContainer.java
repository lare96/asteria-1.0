package server.world.entity.player.container;

import server.world.entity.player.Player;
import server.world.item.Container;
import server.world.item.Item;
import server.world.item.ItemDefinition;

/**
 * A container for bank items.
 * 
 * @author Vix
 * @author lare96
 */
public class BankContainer {

    /**
     * The player who owns this container.
     */
    private Player player;

    /**
     * The container for this bank.
     */
    private Container container = new Container(Container.Type.ALWAYS_STACK, 250);

    /**
     * Instantiates a new bank container.
     * 
     * @param player
     *            the player.
     */
    public BankContainer(Player player) {
        setPlayer(player);
    }

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

    /**
     * @return the container.
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Opens the banking interface.
     */
    public void createBankingInterface() {
        getPlayer().getServerPacketBuilder().sendInventoryInterface(5292, 5063);
        Item[] bankItems = getPlayer().getBank().getContainer().toArray();
        Item[] inventoryItems = getPlayer().getInventory().getItemContainer().toArray();
        getPlayer().getServerPacketBuilder().sendUpdateItems(5382, bankItems);
        getPlayer().getServerPacketBuilder().sendUpdateItems(5064, inventoryItems);
    }

    /**
     * Deposits a bank item.
     * 
     * @param bankSlot
     *            the slot from your inventory.
     * @param bankItem
     *            the item to deposit.
     * @param bankAmount
     *            the item amount to deposit.
     */
    public void depositItem(int bankSlot, int bankItem, int bankAmount) {
        Item inventoryItem = getPlayer().getInventory().getItemContainer().getItem(bankSlot);
        int inventoryItemAmount = getPlayer().getInventory().getItemContainer().getCount(bankItem);
        boolean isInventoryItemNoted = inventoryItem.getDefinition().isNoted();
        int freeBankingSlots = getPlayer().getBank().getContainer().freeSlot();
        if (freeBankingSlots == -1) {
            getPlayer().getServerPacketBuilder().sendMessage("You don't have the remaining bank space to deposit this item.");
            return;
        }
        if (inventoryItemAmount > bankAmount) {
            inventoryItemAmount = bankAmount;
        }
        getPlayer().getInventory().removeItemSlot(new Item(bankItem, inventoryItemAmount), bankSlot);
        int bankCount = getPlayer().getBank().getContainer().getCount(bankItem);
        int transferIdentity = isInventoryItemNoted ? ItemDefinition.getDefinitions()[inventoryItem.getId()].getUnNotedId() : ItemDefinition.getDefinitions()[inventoryItem.getId()].getItemId();
        if (bankCount == 0) {
            getPlayer().getBank().getContainer().add(new Item(transferIdentity, inventoryItemAmount));
        } else {
            getPlayer().getBank().getContainer().set(getPlayer().getBank().getContainer().getSlotById(transferIdentity), new Item(transferIdentity, bankCount + inventoryItemAmount));
        }
        getPlayer().getInventory().refresh(5064);
        Item[] bankItems = getPlayer().getBank().getContainer().toArray();
        getPlayer().getServerPacketBuilder().sendUpdateItems(5382, bankItems);
    }

    /**
     * Withdraws a bank item.
     * 
     * @param bankSlot
     *            the slot from your bank.
     * @param bankItem
     *            the item to withdraw.
     * @param bankAmount
     *            the item amount to withdraw.
     */
    public void withdrawItem(int bankSlot, int bankItem, int bankAmount) {
        Item requestedItem = new Item(bankItem + 1, 1);
        boolean isRequestedItemNoted = ItemDefinition.getDefinitions()[requestedItem.getId()].isNoted();
        int containedBankAmount = getPlayer().getBank().getContainer().getCount(bankItem);

        if (!requestedItem.getDefinition().isStackable()) {
            if (player.getInventory().getItemContainer().freeSlots() < bankAmount) {
                player.getServerPacketBuilder().sendMessage("You do not have enough space in your inventory!");
                return;
            }
        } else {
            if (player.getInventory().getItemContainer().freeSlots() < 1) {
                player.getServerPacketBuilder().sendMessage("You do not have enough space in your inventory!");
                return;
            }
        }

        if (bankAmount < 1 || bankItem < 0) {
            return;
        }
        if (containedBankAmount < bankAmount) {
            bankAmount = containedBankAmount;
        }
        if (getPlayer().isWithdrawAsNote() && !isRequestedItemNoted) {
            getPlayer().getServerPacketBuilder().sendMessage("This item can't be withdrawn as a note.");
            return;
        }
        if (!getPlayer().isWithdrawAsNote()) {
            getPlayer().getInventory().addItem(new Item(bankItem, bankAmount));
        } else if (getPlayer().isWithdrawAsNote()) {
            getPlayer().getInventory().addItem(new Item(bankItem + 1, bankAmount));
        }
        getPlayer().getBank().getContainer().remove(new Item(bankItem, bankAmount), bankSlot);
        getPlayer().getBank().getContainer().shift();
        Item[] bankItems = getPlayer().getBank().getContainer().toArray();
        getPlayer().getInventory().refresh(5064);
        getPlayer().getServerPacketBuilder().sendUpdateItems(5382, bankItems);
    }
}