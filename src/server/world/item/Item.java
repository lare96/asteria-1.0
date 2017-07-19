package server.world.item;

/**
 * Represents an item.
 * 
 * @author lare96
 */
public class Item {

    /**
     * The id of the item.
     */
    private int id;

    /**
     * The amount of the item.
     */
    private int amount;

    /**
     * Construct a new item with the specified amount.
     */
    public Item(int id, int amount) {
        this.setId(id);
        this.setAmount(amount);
    }

    /**
     * Construct a new item with the specified amount as 1.
     */
    public Item(int id) {
        this.setId(id);
        this.setAmount(1);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * @param amount
     *            the amount to set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Increment the amount by 1.
     */
    public void incrementAmount() {
        if ((amount + 1) > Integer.MAX_VALUE) {
            return;
        }
        amount++;
    }

    /**
     * Decrement the amount by 1.
     */
    public void decrementAmount() {
        if ((amount - 1) < 0) {
            return;
        }
        amount--;
    }

    /**
     * Increment the amount by the specified amount.
     */
    public void incrementAmountBy(int amount) {
        if ((this.amount + amount) > Integer.MAX_VALUE) {
            this.amount = Integer.MAX_VALUE;
        } else {
            this.amount += amount;
        }
    }

    /**
     * Decrement the amount by the specified amount.
     */
    public void decrementAmountBy(int amount) {
        if ((this.amount - amount) < 1) {
            this.amount = 0;
        } else {
            this.amount -= amount;
        }
    }

    /**
     * Gets this item definition.
     * 
     * @param id
     *            the item definition to get.
     * @return the definition.
     */
    public ItemDefinition getDefinition() {
        return ItemDefinition.getDefinitions()[id];
    }
}