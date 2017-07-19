package server.world.shop;

/**
 * Holds data for all the different types of currency used in shops.
 * 
 * @author lare96
 */
public enum Currency {

    COINS(995),

    TOKKUL(6529);

    /**
     * The item id of the currency.
     */
    private int itemId;

    /**
     * Construct a new currency.
     * 
     * @param itemId
     *            the item id of the currency.
     */
    Currency(int itemId) {
        this.setItemId(itemId);
    }

    /**
     * @return the itemId.
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * @param itemId
     *            the itemId to set.
     */
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
