package server.world.shop;

import java.io.FileNotFoundException;
import java.io.FileReader;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.net.buffer.PacketBuffer;
import server.util.Misc;
import server.world.World;
import server.world.entity.player.Player;
import server.world.item.Container;
import server.world.item.Item;
import server.world.item.Container.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * A collection of functions that represent a shop. Every shop contains its own
 * restocking worker that allows every shop to individually restock itself,
 * which takes away the need for a massive loop every 600ms (sound familiar?
 * check out the <code>WorldItem</code> class). This also means that if no
 * items are being restocked, no workers are even running! Which in my opinion,
 * is a ton better than looping through a massive array every 600ms, when half
 * the time restocking isn't even needed.
 * 
 * @author lare96
 */
public class Shop {

    /**
     * An array of active shops.
     */
    private static Shop[] shops = new Shop[10];

    /**
     * The shop id.
     */
    private int id;

    /**
     * The shop name.
     */
    private String name;

    /**
     * The modifiable shop container.
     */
    private Container container = new Container(Type.ALWAYS_STACK, 48);

    /**
     * The original shop items.
     */
    private Item[] originalShopItems;

    /**
     * Flag that determines if the shop will replenish its stock or not.
     */
    private boolean stock;

    /**
     * The currency this shop is running on.
     */
    private Currency currency;

    /**
     * The worker that will restock this shop.
     */
    private Task task;

    /**
     * Open and configure this shop.
     * 
     * @param player
     *            the player to open the shop for.
     */
    public void openShop(Player player) {

        /** Block if shopping is disabled. */
        if (!World.isCanShop()) {
            player.getServerPacketBuilder().sendMessage("Shopping has been disabled!");
            return;
        }

        /**
         * Open the shop, display the shop items and send an interface to the
         * inventory that allows the player to right click buy and sell.
         */
        player.getServerPacketBuilder().sendUpdateItems(3823, player.getInventory().getItemContainer().toArray());
        updateShopItems(player);
        player.setOpenShopId(this.getId());
        player.getServerPacketBuilder().sendInventoryInterface(3824, 3822);
        player.getServerPacketBuilder().sendString(this.getName(), 3901);
    }

    /**
     * Updates the items in this shop.
     * 
     * @param player
     *            the player to update the items for.
     */
    private void updateShopItems(Player player) {

        /**
         * A custom implementation of the <code>sendItemUpdates</code> method
         * used for shops.
         */
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2048);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 53);
        out.writeShort(3900);
        out.writeShort(this.getShopItemAmount());

        for (Item item : this.getShop().toArray()) {
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
     * Purchase an item from this shop.
     * 
     * @param player
     *            the player purchasing the item.
     * @param item
     *            the item being purchased.
     */
    public void buyItem(Player player, Item item) {

        /** Check if the item you are buying as 0 stock. */
        if (item.getAmount() == 0) {
            player.getServerPacketBuilder().sendMessage("There is none of this item left in stock!");
            return;
        }

        /**
         * Check if the shop even contains the item you're trying to buy
         * (protection from packet injection).
         */
        if (!this.getShop().contains(item.getId())) {
            return;
        }

        /**
         * Check if the player has the required amount of the currency needed to
         * buy this item.
         */
        if (this.getCurrency() == Currency.COINS) {
            if (!(player.getInventory().getItemContainer().getCount(this.getCurrency().getItemId()) >= (item.getDefinition().getGeneralStorePrice() * item.getAmount()))) {
                player.getServerPacketBuilder().sendMessage("You do not have enough coins to buy this item.");
                return;
            }
        } else {
            if (!(player.getInventory().getItemContainer().getCount(this.getCurrency().getItemId()) >= (item.getDefinition().getSpecialStorePrice() * item.getAmount()))) {
                player.getServerPacketBuilder().sendMessage("You do not have enough " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + " to buy this item.");
                return;
            }
        }

        /**
         * If you are buying more than the shop has in stock, set the amount you
         * are buying to how much is in stock.
         */
        if (item.getAmount() > this.getShop().getCount(item.getId())) {
            item.setAmount(this.getShop().getCount(item.getId()));
        }

        /**
         * Set the amount you are buying to your current amount of free slots if
         * you do not have enough room for the item you are trying to buy.
         */
        if (!player.getInventory().getItemContainer().hasRoomFor(item)) {
            item.setAmount(player.getInventory().getItemContainer().freeSlots());

            if (item.getAmount() == 0) {
                player.getServerPacketBuilder().sendMessage("You do not have enough space in your inventory to buy this item!");
                return;
            }
        }

        /** Buy the item. */
        if (player.getInventory().getItemContainer().freeSlots() >= item.getAmount() && !item.getDefinition().isStackable() || player.getInventory().getItemContainer().freeSlots() >= 1 && item.getDefinition().isStackable()) {
            this.getShop().getById(item.getId()).decrementAmountBy(item.getAmount());

            if (this.getCurrency() == Currency.COINS) {
                player.getInventory().removeItem(new Item(this.getCurrency().getItemId(), item.getAmount() * item.getDefinition().getGeneralStorePrice()));
            } else {
                player.getInventory().removeItem(new Item(this.getCurrency().getItemId(), item.getAmount() * item.getDefinition().getSpecialStorePrice()));
            }

            player.getInventory().addItem(item);
        } else {
            player.getServerPacketBuilder().sendMessage("You don't have enough space in your inventory.");
            return;
        }

        /** Update the players inventory. */
        player.getServerPacketBuilder().sendUpdateItems(3823, player.getInventory().getItemContainer().toArray());

        /** Update the shop for anyone who has it open. */
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == this.getId()) {
                this.updateShopItems(p);
            }
        }

        /** Check if this shop needs to be restocked, if so restock it. */
        restockShop();
    }

    /**
     * Sell an item to this shop.
     * 
     * @param player
     *            the player selling the item.
     * @param item
     *            the item being sold.
     * @param fromSlot
     *            the slot being sold from.
     */
    public void sellItem(Player player, Item item, int fromSlot) {

        /** Check if this is a valid item. */
        if (item.getId() < 1 || item.getAmount() < 1 || item == null) {
            return;
        }

        /** Checks if this item is allowed to be sold. */
        for (int i : Misc.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getServerPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
                return;
            }
        }

        /**
         * Checks if you have the item you want to sell in your inventory, to
         * protect against packet injection.
         */
        if (!player.getInventory().getItemContainer().contains(item.getId())) {
            return;
        }

        /**
         * Block if this shop isn't a general store and you are trying to sell
         * an item that the shop doesn't even have in stock.
         */
        if (!this.getShop().contains(item.getId()) && !this.getName().equalsIgnoreCase("General Store")) {
            player.getServerPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
            return;
        }

        /** Checks if this shop has room for the item you are trying to sell. */
        if (!this.getShop().hasRoomFor(item)) {
            player.getServerPacketBuilder().sendMessage("There is no room for the item you are trying to sell in this store!");
            return;
        }

        /**
         * Checks if you have enough space in your inventory to receive the
         * currency.
         */
        if (player.getInventory().getItemContainer().freeSlots() == 0 && !player.getInventory().getItemContainer().contains(this.getCurrency().getItemId())) {
            player.getServerPacketBuilder().sendMessage("You do not have enough space in your inventory to sell this item!");
            return;
        }

        /**
         * If you try and sell more then you have, it sets the amount to what
         * you have.
         */
        if (item.getAmount() > player.getInventory().getItemContainer().getCount(item.getId()) && !item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getItemContainer().getCount(item.getId()));
        } else if (item.getAmount() > player.getInventory().getItemContainer().getItem(fromSlot).getAmount() && item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getItemContainer().getItem(fromSlot).getAmount());
        }

        /** Sell the item. */
        player.getInventory().removeItemSlot(item, fromSlot);
        player.getInventory().addItem(new Item(this.getCurrency().getItemId(), item.getAmount() * getSellingPrice(item)));
        this.getShop().getById(item.getId()).incrementAmountBy(item.getAmount());

        /** Update your inventory. */
        player.getServerPacketBuilder().sendUpdateItems(3823, player.getInventory().getItemContainer().toArray());

        /** Update the shop for anyone who has it open. */
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == this.getId()) {
                this.updateShopItems(p);
            }
        }
    }

    /**
     * Sends the value of the item to the player when selling.
     * 
     * @param player
     *            the player to send the value for.
     * @param item
     *            the item to send the value of.
     */
    public void sendItemSellingPrice(Player player, Item item) {

        /** Checks if this item is able to be sold. */
        for (int i : Misc.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getServerPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
                return;
            }
        }

        /**
         * Block if this shop isn't a general store and you are trying to sell
         * an item that the shop doesn't even have in stock.
         */
        if (!this.getShop().contains(item.getId()) && !this.getName().equalsIgnoreCase("General Store")) {
            player.getServerPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
            return;
        }

        /** Send the value. */
        if (this.getCurrency() == Currency.COINS) {
            player.getServerPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will buy for " + this.getSellingPrice(item) + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(this.getSellingPrice(item)) + ".");
        } else {
            player.getServerPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will buy for " + this.getSellingPrice(item) + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(this.getSellingPrice(item)) + ".");
        }
    }

    /**
     * Sends the value of the item to the player when buying.
     * 
     * @param player
     *            the player to send the value for.
     * @param item
     *            the item to send the value of.
     */
    public void sendItemBuyingPrice(Player player, Item item) {

        /** Send the value of the item based on the currency. */
        if (this.getCurrency() == Currency.COINS) {
            player.getServerPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will sell for " + item.getDefinition().getGeneralStorePrice() + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(item.getDefinition().getGeneralStorePrice()) + ".");
        } else {
            player.getServerPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will sell for " + item.getDefinition().getSpecialStorePrice() + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(item.getDefinition().getSpecialStorePrice()) + ".");
        }
    }

    /**
     * Gets the selling price of a certain item (the item has to be worth less
     * when selling or people would just buy then sell for profit).
     * 
     * @param item
     *            the item to get the selling price for.
     * @return the price.
     */
    private int getSellingPrice(Item item) {
        return (int) (this.getCurrency() == Currency.COINS ? Math.floor((item.getDefinition().getGeneralStorePrice() / 2)) : Math.floor((item.getDefinition().getSpecialStorePrice() / 2)));
    }

    /**
     * Instantiate and schedule a new worker that will restock this shop.
     */
    private void restockShop() {
        if (!this.needsRestock() || !this.isReplenishStock()) {
            return;
        }

        if (this.getTask() == null || !this.getTask().isRunning()) {
            task = new Task(5, false, Time.SECOND) {
                @Override
                public void logic() {
                    if (atOriginalAmounts() || !isReplenishStock()) {
                        this.cancel();
                        return;
                    }

                    for (Item item : getShop().toArray()) {
                        if (item == null) {
                            continue;
                        }
                        if (item.getAmount() < getOriginalAmount(item.getId())) {
                            item.incrementAmount();

                            for (Player player : World.getPlayers()) {
                                if (player == null) {
                                    continue;
                                }

                                if (player.getOpenShopId() == getId()) {
                                    updateShopItems(player);
                                }
                            }
                        }
                    }
                }
            };

            GameLogic.getSingleton().submit(this.getTask());
        }
    }

    /**
     * Checks if this shops needs to restock its items.
     * 
     * @return if the shop needs to be restocked.
     */
    private boolean needsRestock() {

        /**
         * Loop through the current shop items and check if the stock of any
         * original shop items are 0. If so, return true.
         */
        for (Item item : this.getShop().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getAmount() < 1 && isOriginalItem(item.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an item id is apart of the original shop items.
     * 
     * @param id
     *            the id to check for.
     * @return if the item is apart of the original shop items.
     */
    private boolean isOriginalItem(int id) {

        /**
         * Loop through the original shop items. If the id specified matches
         * with any of the original shop items, return true.
         */
        for (Item item : this.getOriginalShopItems()) {
            if (item == null) {
                continue;
            }

            if (item.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the current shop items are at the original amount.
     * 
     * @return if the current shop items are at the original amount.
     */
    private boolean atOriginalAmounts() {
        int amountNeeded = this.getOriginalShopItemAmount();
        int amountGotten = 0;

        for (Item item : this.getShop().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getAmount() == getOriginalAmount(item.getId())) {
                amountGotten++;
            }
        }
        return amountNeeded == amountGotten ? true : false;
    }

    /**
     * Gets the original amount of a shop item.
     * 
     * @param id
     *            the item to get the original amount of.
     * @return the original amount.
     */
    private int getOriginalAmount(int id) {
        for (Item item : this.getOriginalShopItems()) {
            if (item == null) {
                continue;
            }

            if (item.getId() == id) {
                return item.getAmount();
            }
        }
        return -1;
    }

    /**
     * @return the amount of different items in this shop excluding null values.
     */
    private int getShopItemAmount() {
        int total = 0;

        for (Item i : this.getShop().toArray()) {
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
     * @return the amount of different items in this shop excluding null values
     *         and non-primitive shop items.
     */
    private int getOriginalShopItemAmount() {
        int total = 0;

        for (Item item : this.getShop().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getId() > 0 && isOriginalItem(item.getId())) {
                total++;
            }
        }

        return total;
    }

    /**
     * Gets an instance of a shop by it's id.
     * 
     * @param id
     *            the id of the shop to get.
     * @return the static instance of the shop.
     */
    public static Shop getShop(int id) {
        return shops[id];
    }

    /**
     * Gets an instance of a shop by it's name.
     * 
     * @param id
     *            the name of the shop to get.
     * @return the static instance of the shop.
     */
    public static Shop getShop(String name) {
        for (Shop s : shops) {
            if (s == null) {
                continue;
            }

            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Loads all of the shops from the <code>world_shops.json</code> file.
     * 
     * @throws JsonIOException
     *             any JsonIOException's that may occur.
     * @throws JsonSyntaxException
     *             any JsonSyntaxException's that may occur.
     * @throws FileNotFoundException
     *             any FileNotFoundException's that may occur.
     */
    public static void load() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(Misc.WORLD_SHOPS));
        final Gson builder = new GsonBuilder().create();
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            Shop shop = new Shop();
            shop.setId(reader.get("id").getAsInt());
            shop.setName(reader.get("name").getAsString());
            shop.getShop().setItems(builder.fromJson(reader.get("items").getAsJsonArray(), Item[].class));
            shop.setOriginalShopItems(builder.fromJson(reader.get("items").getAsJsonArray(), Item[].class));
            shop.setReplenishStock(reader.get("restock").getAsBoolean());
            shop.setCurrency(Currency.valueOf(reader.get("currency").getAsString()));

            for (int e : Misc.NO_SHOP_ITEMS) {
                if (shop.getShop().contains(e)) {
                    System.err.println("Invalid shop item id: " + e);
                    throw new IllegalStateException();
                }
            }

            shops[shop.getId()] = shop;
            parsed++;
        }
    }

    /**
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set.
     */
    private void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set.
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * @return the container.
     */
    public Container getShop() {
        return container;
    }

    /**
     * @return the replenishStock.
     */
    public boolean isReplenishStock() {
        return stock;
    }

    /**
     * @param replenishStock
     *            the replenishStock to set.
     */
    public void setReplenishStock(boolean stock) {
        this.stock = stock;
    }

    /**
     * @return the currency.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency
     *            the currency to set.
     */
    private void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the restockWorker.
     */
    private Task getTask() {
        return task;
    }

    /**
     * @return the originalShopItems.
     */
    private Item[] getOriginalShopItems() {
        return originalShopItems;
    }

    /**
     * @param originalShopItems
     *            the originalShopItems to set.
     */
    private void setOriginalShopItems(Item[] originalShopItems) {
        this.originalShopItems = originalShopItems;
    }
}
