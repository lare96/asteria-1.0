package server.world.entity.player.container;

import server.world.entity.player.Player;
import server.world.item.Container;
import server.world.item.Item;
import server.world.item.Container.Type;

/**
 * A container for inventory items.
 * 
 * @author Vix
 * @author lare96
 */
public class InventoryContainer {

    /**
     * The inventory interface.
     */
    public static final int DEFAULT_INVENTORY_INTERFACE = 3214;

    /**
     * The size of this container.
     */
    public static final int SIZE = 28;

    /**
     * The player.
     */
    private Player player;

    /**
     * The container.
     */
    private Container itemContainer = new Container(Type.STANDARD, SIZE);

    /**
     * Creates a new item container.
     * 
     * @param player
     *            the player.
     */
    public InventoryContainer(Player player) {
        this.player = player;
    }

    /**
     * Refreshes the inventory on login.
     */
    public void sendInventoryOnLogin() {
        refresh(DEFAULT_INVENTORY_INTERFACE);
    }

    /**
     * Writes this inventory to an interface.
     * 
     * @param widget
     *            the interface to write this inventory to.
     */
    public void refresh(int widget) {
        Item[] inv = itemContainer.toArray();
        player.getServerPacketBuilder().sendUpdateItems(widget, inv);
    }

    /**
     * Adds an item to this inventory.
     * 
     * @param item
     *            the item to add.
     */
    public void addItem(Item item) {
        if (item == null) {
            return;
        }
        if (!itemContainer.contains(item.getId()) && !item.getDefinition().isStackable()) {
            if (itemContainer.freeSlot() == -1) {
                player.getServerPacketBuilder().sendMessage("You don't have enough free space in your inventory.");
                return;
            }
        }
        int amount = item.getAmount();
        if (amount > itemContainer.freeSlots() && !item.getDefinition().isStackable()) {
            amount = itemContainer.freeSlots();
        }
        itemContainer.add(new Item(item.getId(), amount));
        refresh(DEFAULT_INVENTORY_INTERFACE);
    }

    /**
     * Adds an item to this inventory.
     * 
     * @param item
     *            the item to add.
     * @param slot
     *            the slot to add this item to.
     */
    public void addItemToSlot(Item item, int slot) {
        if (item == null) {
            return;
        }
        itemContainer.set(slot, item);
        refresh(DEFAULT_INVENTORY_INTERFACE);
    }

    /**
     * Removes an item from this inventory.
     * 
     * @param item
     *            the item to remove.
     */
    public void removeItem(Item item) {
        if (item == null || item.getId() == -1) {
            return;
        }
        if (!itemContainer.contains(item.getId())) {
            return;
        }

        if (item.getDefinition().isStackable()) {
            itemContainer.remove(item, item.getAmount());
        } else {
            itemContainer.remove(item, 1);
        }

        refresh(DEFAULT_INVENTORY_INTERFACE);
    }

    /**
     * Replaces an existing item in your inventory with a new one.
     * 
     * @param oldItem
     *            the old item.
     * @param newItem
     *            the new item.
     */
    public void replaceItemWithItem(Item oldItem, Item newItem) {
        if (getItemContainer().getCount(oldItem.getId()) >= oldItem.getAmount()) {
            removeItem(oldItem);
            addItem(newItem);
        }
    }

    /**
     * Removes an item from this inventory.
     * 
     * @param item
     *            the item to remove.
     * @param slot
     *            the slot to remove this item from.
     */
    public void removeItemSlot(Item item, int slot) {
        if (item == null || item.getId() == -1) {
            return;
        }
        if (slot == -1) {
            return;
        }
        if (itemContainer.getItem(slot) == null) {
            return;
        }
        if (!itemContainer.contains(item.getId())) {
            return;
        }
        itemContainer.remove(item, slot);
        refresh(DEFAULT_INVENTORY_INTERFACE);
    }

    /**
     * Swaps two items in inventory.
     * 
     * @param fromSlot
     *            the old slot of this item.
     * @param toSlot
     *            the destination slot for this item.
     */
    public void swap(int fromSlot, int toSlot) {
        itemContainer.swap(fromSlot, toSlot);
        refresh(DEFAULT_INVENTORY_INTERFACE);
    }

    /**
     * @return the container.
     */
    public Container getItemContainer() {
        return itemContainer;
    }
}