package server.world.entity.player.container;

import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;
import server.world.item.Container;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.item.Container.Type;

/**
 * A container for equipment items.
 * 
 * @author Vix
 * @author lare96
 */
public class EquipmentContainer {

    /**
     * The equipment interface.
     */
    private static final int EQUIPMENT_INTERFACE = 1688;

    /**
     * The player using this container.
     */
    private Player player;

    /**
     * The item container.
     */
    private Container itemContainer = new Container(Type.STANDARD, 14);

    /**
     * Creates a new container for equipment.
     * 
     * @param player
     *            the player to create the container for.
     */
    public EquipmentContainer(Player player) {
        this.player = player;
    }

    /**
     * Refreshes the players equipment tab.
     */
    public void refresh() {
        Item[] items = itemContainer.toArray();
        player.getServerPacketBuilder().sendUpdateItems(EQUIPMENT_INTERFACE, items);
    }

    /**
     * Equips an item for this player.
     * 
     * @param slot
     *            the inventory slot the item being equipped is in.
     */
    public void equipItem(int slot) {
        Item item = player.getInventory().getItemContainer().getItem(slot);
        if (item == null) {
            return;
        }
        if (ItemDefinition.getDefinitions()[item.getId()].isStackable()) {
            int slotType = item.getDefinition().getEquipmentSlot();
            Item equipItem = itemContainer.getItem(slotType);
            if (itemContainer.getItem(slotType) != null) {
                if (item.getId() == equipItem.getId()) {
                    itemContainer.set(slotType, new Item(item.getId(), item.getAmount() + equipItem.getAmount()));
                } else {
                    player.getInventory().addItemToSlot(equipItem, slot);
                    itemContainer.set(slotType, item);
                }
            } else {
                itemContainer.set(slotType, item);
            }
            player.getInventory().removeItemSlot(item, slot);
        } else {
            int slotType = item.getDefinition().getEquipmentSlot();
            if (slotType == 3) {
                if (ItemDefinition.getDefinitions()[item.getId()].isTwoHanded()) {
                    removeItem(5);
                    if (itemContainer.getItem(5) != null) {
                        return;
                    }
                }
            }
            if (slotType == 5 && itemContainer.getItem(3) != null) {
                if (ItemDefinition.getDefinitions()[item.getId()].isTwoHanded()) {
                    removeItem(3);
                    if (itemContainer.getItem(3) != null) {
                        return;
                    }
                }
            }
            if (itemContainer.getItem(slotType) != null) {
                Item equipItem = itemContainer.getItem(slotType);
                player.getInventory().addItemToSlot(equipItem, slot);
            } else {
                player.getInventory().removeItemSlot(item, slot);
            }
            itemContainer.set(slotType, new Item(item.getId(), item.getAmount()));
        }
        player.writeBonus();
        refresh();
        player.getFlags().flag(Flag.APPEARANCE);
    }

    /**
     * Removes an item from this container.
     * 
     * @param slot
     *            the equipment slot to unequip the item from.
     */
    public void removeItem(int slot) {
        Item item = itemContainer.getItem(slot);
        if (!player.getInventory().getItemContainer().hasRoomFor(item)) {
            player.getServerPacketBuilder().sendMessage("Not enough space in your inventory to un-equip this item.");
            return;
        }
        if (item == null) {
            return;
        }

        itemContainer.remove(item, slot);
        player.getInventory().addItem(new Item(item.getId(), item.getAmount()));
        player.writeBonus();
        refresh();
        player.getInventory().refresh(InventoryContainer.DEFAULT_INVENTORY_INTERFACE);
        player.getFlags().flag(Flag.APPEARANCE);
    }

    /**
     * @return the item container.
     */
    public Container getItemContainer() {
        return itemContainer;
    }
}