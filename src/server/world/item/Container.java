package server.world.item;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A container for storing and managing items.
 * 
 * @author Graham
 * @author Vix
 * @author lare96
 */
public class Container {

    /**
     * The possible types of containers.
     */
    public enum Type {
        STANDARD, ALWAYS_STACK, NEVER_STACK;
    }

    /**
     * The capacity of this container.
     */
    private int containerCapacity;

    /**
     * The items within this container.
     */
    private Item[] items;

    /**
     * The listeners for this container.
     */
    private List<ContainerListener> listeners = new LinkedList<ContainerListener>();

    /**
     * The type of container.
     */
    private Type containerType;

    /**
     * If this container is firing events.
     */
    private boolean firingEvents = true;

    /**
     * Creates a new item container.
     * 
     * @param type
     *            the type of container.
     * @param capacity
     *            the capacity.
     */
    public Container(Type type, int capacity) {
        this.containerType = type;
        this.containerCapacity = capacity;
        this.items = new Item[capacity];
    }

    /**
     * Adds an item to this container.
     * 
     * @param item
     *            the item to add.
     * @return true if the item was added.
     */
    public boolean add(Item item) {
        return add(item, -1);
    }

    /**
     * Removes all the listeners for this container.
     */
    public void removeAllListeners() {
        listeners.clear();
    }

    /**
     * Removes a single listener for this container.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeListener(ContainerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Removes an item to zero.
     * 
     * @param item
     *            the item removed.
     * @return the amount removed.
     */
    public int removeOrZero(Item item) {
        return remove(item, -1, true);
    }

    /**
     * Adds a listener for this container.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addListener(ContainerListener listener) {
        listeners.add(listener);
        listener.itemsChanged(this);
    }

    /**
     * Returns the capacity of this container.
     * 
     * @return the capacity.
     */
    public int capacity() {
        return containerCapacity;
    }

    /**
     * Clears this container.
     */
    public void clear() {
        items = new Item[items.length];
        if (firingEvents) {
            fireItemsChanged();
        }
    }

    /**
     * Gets the id of an item by its slot.
     * 
     * @param slot
     *            the slot to get.
     * @return the item id in that slot.
     */
    public int getIdBySlot(int slot) {
        return items[slot].getId();
    }

    /**
     * Returns if this container is firing events.
     * 
     * @return true if this container is firing events.
     */
    public boolean isFiringEvents() {
        return firingEvents;
    }

    /**
     * Checks if a slot is free.
     * 
     * @param slot
     *            the slot to check.
     * @return true if the slot is free.
     */
    public boolean isSlotFree(int slot) {
        return items[slot] == null;
    }

    /**
     * Checks if a slot is used.
     * 
     * @param slot
     *            the slot to check.
     * @return true if the slot is used.
     */
    public boolean isSlotUsed(int slot) {
        return items[slot] != null;
    }

    /**
     * Removes an item from this container.
     * 
     * @param item
     *            the item to remove.
     * @return the amount removed.
     */
    public int remove(Item item) {
        return remove(item, -1, false);
    }

    /**
     * Removes an item from this container.
     * 
     * @param item
     *            the item to remove.
     * @param preferredSlot
     *            the preferred slot to remove the item from.
     * @return the amount removed.
     */
    public int remove(Item item, int preferredSlot) {
        return remove(item, preferredSlot, false);
    }

    /**
     * Checks if this container has a certain item.
     * 
     * @param id
     *            the item to check in this container for.
     * @return true if this container has the item.
     */
    public boolean contains(int id) {
        return getSlotById(id) != -1;
    }

    /**
     * Checks if this container has a certain item.
     * 
     * @param item
     *            the item to check in this container for.
     * @return true if this container has the item.
     */
    public boolean contains(Item item) {
        for (Item i : items) {
            if (i == null) {
                continue;
            }

            if (item.getId() == i.getId() && i.getAmount() >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the listeners for this container.
     */
    public Collection<ContainerListener> getListeners() {
        return Collections.unmodifiableCollection(listeners);
    }

    /**
     * Runs the listeners.
     * 
     * @param slot
     *            the slot of the item.
     */
    public void fireItemChanged(int slot) {
        for (ContainerListener listener : listeners) {
            listener.itemChanged(this, slot);
        }
    }

    /**
     * Sets an item to another item.
     * 
     * @param index
     *            the index of the item being set.
     * @param item
     *            the item.
     */
    public void set(int index, Item item) {
        items[index] = item;
        if (firingEvents) {
            fireItemChanged(index);
        }
    }

    /**
     * Sets the firing events.
     * 
     * @param firingEvents
     *            if you are firing events.
     */
    public void setFiringEvents(boolean firingEvents) {
        this.firingEvents = firingEvents;
    }

    /**
     * Sets this containers items to another set of items.
     * 
     * @param items
     *            the new set of items.
     */
    public void setItems(Item[] items) {
        clear();
        for (int i = 0; i < items.length; i++) {
            this.items[i] = items[i];
        }
    }

    /**
     * Fires the listeners for this container.
     */
    public void fireItemsChanged() {
        for (ContainerListener listener : listeners) {
            listener.itemsChanged(this);
        }
    }

    /**
     * Fires the listeners for this container.
     * 
     * @param slots
     *            the slots of the changed items.
     */
    public void fireItemsChanged(int[] slots) {
        for (ContainerListener listener : listeners) {
            listener.itemsChanged(this, slots);
        }
    }

    /**
     * Gets a free slot.
     * 
     * @return the free slot.
     */
    public int freeSlot() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the amount of free slots left.
     * 
     * @return the amount of slots left.
     */
    public int freeSlots() {
        return containerCapacity - size();
    }

    /**
     * Gets an item by its index.
     * 
     * @param index
     *            the index.
     * @return the item on this index.
     */
    public Item getItem(int index) {
        if (index == -1)
            return null;
        return items[index];
    }

    /**
     * Gets an item id by its index.
     * 
     * @param index
     *            the index.
     * @return the item id on this index.
     */
    public int getItemId(int index) {
        if (index == -1 || items[index] == null)
            return -1;
        return items[index].getId();
    }

    /**
     * Gets an item id by its index.
     * 
     * @param index
     *            the index.
     * @return the item id on this index.
     */
    public Item getById(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            if (items[i].getId() == id) {
                return items[i];
            }
        }
        return null;
    }

    /**
     * Gets the amount of times an item is in your inventory by its id.
     * 
     * @param id
     *            the id.
     * @return the amount of times this item is in your inventory.
     */
    public int getCount(int id) {
        int total = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (items[i].getId() == id) {
                    total += items[i].getAmount();
                }
            }
        }
        return total;
    }

    /**
     * Gets the slot of an item by its id.
     * 
     * @param id
     *            the id.
     * @return the slot of the item.
     */
    public int getSlotById(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            if (items[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds an item to this container.
     * 
     * @param item
     *            the item to add.
     * @param slot
     *            the slot to add it to.
     * @return true if the item was added.
     */
    public boolean add(Item item, int slot) {
        if (item == null) {
            return false;
        }
        int newSlot = (slot > -1) ? slot : freeSlot();
        if ((item.getDefinition().isStackable() || containerType.equals(Type.ALWAYS_STACK)) && !containerType.equals(Type.NEVER_STACK)) {
            if (getCount(item.getId()) > 0) {
                newSlot = getSlotById(item.getId());
            }
        }
        if (newSlot == -1) {
            return false;
        }
        if (getItem(newSlot) != null) {
            newSlot = freeSlot();
        }
        if ((item.getDefinition().isStackable() || containerType.equals(Type.ALWAYS_STACK)) && !containerType.equals(Type.NEVER_STACK)) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].getId() == item.getId()) {
                    set(i, new Item(items[i].getId(), items[i].getAmount() + item.getAmount()));
                    return true;
                }
            }
            if (newSlot == -1) {
                return false;
            } else {
                set(slot > -1 ? newSlot : freeSlot(), item);
                return true;
            }
        } else {
            int slots = freeSlots();
            if (slots >= item.getAmount()) {
                boolean isFiringEvents = firingEvents;
                firingEvents = false;
                try {
                    for (int i = 0; i < item.getAmount(); i++) {
                        set(slot > -1 ? newSlot : freeSlot(), new Item(item.getId(), 1));
                    }
                    if (isFiringEvents) {
                        fireItemsChanged();
                    }
                    return true;
                } finally {
                    firingEvents = isFiringEvents;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Checks if this container has room for this item.
     * 
     * @param item
     *            the item to check if this has room for.
     * @return true if it has room for the item.
     */
    public boolean hasRoomFor(Item item) {
        if ((item.getDefinition().isStackable() || containerType.equals(Type.ALWAYS_STACK)) && !containerType.equals(Type.NEVER_STACK)) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].getId() == item.getId()) {
                    int totalCount = item.getAmount() + items[i].getAmount();
                    if (totalCount >= Integer.MAX_VALUE || totalCount < 1) {
                        return false;
                    }
                    return true;
                }
            }
            int slot = freeSlot();
            return slot != -1;
        } else {
            int slots = freeSlots();
            return slots >= item.getAmount();
        }
    }

    /**
     * Inserts an item.
     * 
     * @param fromSlot
     *            the old slot.
     * @param toSlot
     *            the new slot.
     */
    public void insert(int fromSlot, int toSlot) {
        Item from = items[fromSlot];
        if (from == null) {
            return;
        }
        items[fromSlot] = null;
        if (fromSlot > toSlot) {
            int shiftFrom = toSlot;
            int shiftTo = fromSlot;
            for (int i = (toSlot + 1); i < fromSlot; i++) {
                if (items[i] == null) {
                    shiftTo = i;
                    break;
                }
            }
            Item[] slice = new Item[shiftTo - shiftFrom];
            System.arraycopy(items, shiftFrom, slice, 0, slice.length);
            System.arraycopy(slice, 0, items, shiftFrom + 1, slice.length);
        } else {
            int sliceStart = fromSlot + 1;
            int sliceEnd = toSlot;
            for (int i = (sliceEnd - 1); i >= sliceStart; i--) {
                if (items[i] == null) {
                    sliceStart = i;
                    break;
                }
            }
            Item[] slice = new Item[sliceEnd - sliceStart + 1];
            System.arraycopy(items, sliceStart, slice, 0, slice.length);
            System.arraycopy(slice, 0, items, sliceStart - 1, slice.length);
        }
        items[toSlot] = from;
        if (firingEvents) {
            fireItemsChanged();
        }
    }

    /**
     * Removes an item from this container.
     * 
     * @param item
     *            the item to remove.
     * @param preferredSlot
     *            the slot to remove it from.
     * @param allowZero
     *            if the item amount can stay at 0 without being removed.
     * @return the amount removed.
     */
    public int remove(Item item, int preferredSlot, boolean allowZero) {
        if (item == null)
            return -1;
        int removed = 0;
        if ((item.getDefinition().isStackable() || containerType.equals(Type.ALWAYS_STACK)) && !containerType.equals(Type.NEVER_STACK)) {
            int slot = getSlotById(item.getId());
            Item stack = getItem(slot);
            if (stack == null)
                return -1;
            if (stack.getAmount() > item.getAmount()) {
                removed = item.getAmount();
                set(slot, new Item(stack.getId(), stack.getAmount() - item.getAmount()));
            } else {
                removed = stack.getAmount();
                set(slot, allowZero ? new Item(stack.getId(), 0) : null);
            }
        } else {
            for (int i = 0; i < item.getAmount(); i++) {
                int slot = getSlotById(item.getId());
                if (i == 0 && preferredSlot != -1) {
                    Item inSlot = getItem(preferredSlot);
                    if (inSlot.getId() == item.getId()) {
                        slot = preferredSlot;
                    }
                }
                if (slot != -1) {
                    removed++;
                    set(slot, null);
                } else {
                    break;
                }
            }
        }
        return removed;
    }

    /**
     * Shifts the items (to clear empty spaces).
     */
    public void shift() {
        Item[] previousItems = items;
        items = new Item[containerCapacity];
        int newIndex = 0;
        for (int i = 0; i < items.length; i++) {
            if (previousItems[i] != null) {
                items[newIndex] = previousItems[i];
                newIndex++;
            }
        }
        if (firingEvents) {
            fireItemsChanged();
        }
    }

    /**
     * Gets the size of this container.
     * 
     * @return the size.
     */
    public int size() {
        int size = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                size++;
            }
        }
        return size;
    }

    /**
     * Swaps items.
     * 
     * @param fromSlot
     *            from this slot.
     * @param toSlot
     *            to this slot.
     */
    public void swap(int fromSlot, int toSlot) {
        Item temp = getItem(fromSlot);
        boolean isFiringEvents = firingEvents;
        firingEvents = false;
        try {
            set(fromSlot, getItem(toSlot));
            set(toSlot, temp);
            if (isFiringEvents) {
                fireItemsChanged(new int[] { fromSlot, toSlot });
            }
        } finally {
            firingEvents = isFiringEvents;
        }
    }

    /**
     * Transfers items to another container.
     * 
     * @param from
     *            from this container.
     * @param to
     *            to this container.
     * @param fromSlot
     *            from this slot.
     * @param id
     *            with this id.
     * @return true if it was transferred.
     */
    public static boolean transfer(Container from, Container to, int fromSlot, int id) {
        Item fromItem = from.getItem(fromSlot);
        if (fromItem == null || fromItem.getId() != id) {
            return false;
        }
        if (to.add(fromItem)) {
            from.set(fromSlot, null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * The container itself.
     * 
     * @return the array of items.
     */
    public Item[] toArray() {
        return items;
    }
}