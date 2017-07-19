package server.world.item;

/**
 * Listener for the container.
 * 
 * @author Graham
 * @author Vix
 */
public interface ContainerListener {

    /**
     * Runs an action if an item has changed.
     * 
     * @param container
     *            the container.
     * @param slot
     *            the slot.
     */
    public void itemChanged(Container container, int slot);

    /**
     * Runs an action if an items have changed.
     * 
     * @param container
     *            the container.
     * @param slots
     *            the slots.
     */
    public void itemsChanged(Container container, int[] slots);

    /**
     * Runs an action if an items have changed.
     * 
     * @param container
     *            the container.
     */
    public void itemsChanged(Container container);
}