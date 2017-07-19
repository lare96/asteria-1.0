package server.world.item;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.util.Misc;
import server.world.World;
import server.world.entity.player.Player;
import server.world.map.Position;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * An item placed somewhere in the region. World items are handled very
 * differently because each item essentially 'schedules' itself. This basically
 * means that within this container class it does not only hold values
 * representing a world item, but also a {@link Task} that will exclusively
 * perform processing on this item, whereas in a typical project insanity
 * server, you'd be looping though a list of items every server tick when it
 * might not even be needed.
 * 
 * @author lare96
 */
public class WorldItem {

    /**
     * A list to keep track of all the world items.
     */
    private static List<WorldItem> items = new ArrayList<WorldItem>();

    /**
     * The item.
     */
    private Item item;

    /**
     * The location of the item on the coordinate plane.
     */
    private Position position;

    /**
     * The subscriber.
     */
    private Player player;

    /**
     * If the item has been taken.
     */
    private boolean pickedUp;

    /**
     * If this item respawns (only applicable if this item is static).
     */
    private boolean respawns;

    /**
     * The worker that will be used to process this item.
     */
    private Task task;

    /**
     * The state of this item.
     */
    private State state = State.SEEN_BY_OWNER;

    /**
     * The class constructor for a default world item.
     * 
     * @param item
     *            the item to create.
     * @param position
     *            the position to create it on.
     * @param player
     *            the player to create it for.
     */
    public WorldItem(final Item item, final Position position, final Player player) {
        this.setItem(item);
        this.setPosition(position);
        this.setPlayer(player);
        this.setTask(new Task(1, false, Time.MINUTE) {
            @Override
            public void logic() {
                switch (getState()) {
                    case SEEN_BY_OWNER:
                        setState(State.SEEN_BY_EVERYONE);
                    case SEEN_BY_EVERYONE:
                        if (!isPickedUp()) {
                            for (Player p : World.getPlayers()) {
                                if (p == null || p.getUsername().equals(getPlayer().getUsername())) {
                                    continue;
                                }

                                p.getServerPacketBuilder().sendGroundItem(new WorldItem(item, position, player));
                            }

                            setPlayer(null);
                            setState(State.SEEN_BY_NO_ONE);
                        } else {
                            unregister();
                        }
                        break;
                    case SEEN_BY_NO_ONE:
                        if (!isPickedUp()) {
                            for (Player p : World.getPlayers()) {
                                if (p == null) {
                                    continue;
                                }

                                p.getServerPacketBuilder().removeGroundItem(new WorldItem(item, position, player));
                            }

                            unregister();
                        } else {
                            unregister();
                        }
                        break;
                    case STATIC:
                        if (isPickedUp()) {
                            for (Player p : World.getPlayers()) {
                                if (p == null) {
                                    continue;
                                }

                                p.getServerPacketBuilder().sendGroundItem(new WorldItem(item, position, player));
                            }

                            setPlayer(null);
                            this.cancel();
                        }
                        break;
                    default:
                        System.err.println("World item task was not stopped!");
                        break;

                }
            }
        });
    }

    /**
     * Default class constructor.
     */
    public WorldItem() {

    }

    /**
     * All possible states an item can be in.
     * 
     * @author lare96
     */
    private enum State {
        SEEN_BY_OWNER,

        SEEN_BY_EVERYONE,

        SEEN_BY_NO_ONE,

        STATIC
    }

    /**
     * Register this item as an active world item.
     */
    public void register() {
        getPlayer().getServerPacketBuilder().sendGroundItem(this);
        GameLogic.getSingleton().submit(this.getTask());
        items.add(this);
    }

    /**
     * Unregister this world item.
     */
    public void unregister() {
        this.getTask().cancel();
        items.remove(this);
    }

    /**
     * Registers a static world item. A static world item is an item that is
     * visible to everyone from the moment of conception, and does not have a
     * set owner. Static world items can also be set to respawn once picked up.
     * 
     * @param item
     *            the static item to register.
     */
    public static void registerStaticItem(final WorldItem item) {
        item.setPlayer(null);
        item.setState(State.STATIC);
        item.setTask(new Task(1, false, Time.MINUTE) {
            @Override
            public void logic() {
                switch (item.getState()) {
                    case SEEN_BY_OWNER:
                        item.setState(State.SEEN_BY_EVERYONE);
                    case SEEN_BY_EVERYONE:
                        if (!item.isPickedUp()) {
                            for (Player p : World.getPlayers()) {
                                if (p == null || p.getUsername().equals(item.getPlayer().getUsername())) {
                                    continue;
                                }

                                p.getServerPacketBuilder().sendGroundItem(new WorldItem(item.getItem(), item.getPosition(), item.getPlayer()));
                            }

                            item.setPlayer(null);
                            item.setState(State.SEEN_BY_NO_ONE);
                        } else {
                            item.unregister();
                        }
                        break;
                    case SEEN_BY_NO_ONE:
                        if (!item.isPickedUp()) {
                            for (Player p : World.getPlayers()) {
                                if (p == null) {
                                    continue;
                                }

                                p.getServerPacketBuilder().removeGroundItem(new WorldItem(item.getItem(), item.getPosition(), item.getPlayer()));
                            }

                            item.unregister();
                        } else {
                            item.unregister();
                        }
                        break;
                    case STATIC:
                        if (item.isPickedUp()) {
                            for (Player p : World.getPlayers()) {
                                if (p == null) {
                                    continue;
                                }

                                p.getServerPacketBuilder().sendGroundItem(new WorldItem(item.getItem(), item.getPosition(), item.getPlayer()));
                            }

                            item.setPlayer(null);
                            item.setPickedUp(false);
                            items.add(item);
                            this.cancel();
                        }
                        break;
                    default:
                        System.err.println("World item task was not stopped!");
                        break;

                }
            }
        });

        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            p.getServerPacketBuilder().sendGroundItem(item);
        }

        items.add(item);
    }

    /**
     * Makes world items reappear on login (if there are any).
     * 
     * @param player
     *            the player to make the items reappear for.
     */
    public static void load(Player player) {
        for (final WorldItem w : items) {
            if (w == null) {
                continue;
            }

            if (w.getState() == State.SEEN_BY_NO_ONE || w.getState() == State.STATIC && player.getPosition().getZ() == w.getPosition().getZ()) {
                player.getServerPacketBuilder().sendGroundItem(new WorldItem(new Item(w.getItem().getId(), w.getItem().getAmount()), new Position(w.getPosition().getX(), w.getPosition().getY(), w.getPosition().getZ()), player));
                continue;
            }

            if (w.getPlayer() != null) {
                if (w.getPlayer().getUsername().equals(player.getUsername()) && player.getPosition().getZ() == w.getPosition().getZ()) {
                    player.getServerPacketBuilder().sendGroundItem(new WorldItem(new Item(w.getItem().getId(), w.getItem().getAmount()), new Position(w.getPosition().getX(), w.getPosition().getY(), w.getPosition().getZ()), player));
                    continue;
                }
            }
        }
    }

    /**
     * Removes any world items for a player that isn't on the same height level.
     * 
     * @param player
     *            the player to remove the items for.
     */
    public static void removeAllHeight(Player player) {
        for (final WorldItem w : items) {
            if (w == null) {
                continue;
            }

            if (player.getPosition().getZ() != w.getPosition().getZ()) {
                player.getServerPacketBuilder().removeGroundItem(w);
            }
        }
    }

    /**
     * Allows a player to pick this item up.
     * 
     * @param pickup
     *            the player picking this item up.
     */
    public void pickup(Player pickup) {
        switch (getState()) {
            case SEEN_BY_NO_ONE:
                if (!this.isPickedUp()) {
                    this.setPickedUp(true);

                    for (Player p : World.getPlayers()) {
                        if (p == null) {
                            continue;
                        }

                        p.getServerPacketBuilder().removeGroundItem(this);
                    }

                    unregister();
                    pickup.getInventory().addItem(this.getItem());
                } else {
                    pickup.getServerPacketBuilder().sendMessage("Too late!");
                }
                break;
            case SEEN_BY_OWNER:
                this.setPickedUp(true);
                pickup.getServerPacketBuilder().removeGroundItem(this);
                unregister();
                pickup.getInventory().addItem(this.getItem());
                break;
            case STATIC:
                if (!this.isPickedUp()) {
                    this.setPickedUp(true);

                    for (Player p : World.getPlayers()) {
                        if (p == null) {
                            continue;
                        }

                        p.getServerPacketBuilder().removeGroundItem(this);
                    }

                    items.remove(this);
                    pickup.getInventory().addItem(this.getItem());

                    if (this.isRespawns()) {
                        GameLogic.getSingleton().submit(this.getTask());
                    }
                } else {
                    pickup.getServerPacketBuilder().sendMessage("Too late!");
                }
                break;
            default:
                System.err.println("ERROR! Item supossed to be gone but still there? " + this.getState().name());
                break;
        }
    }

    /**
     * Determines if an item exists or not, and if it does it returns the
     * instance of the item.
     * 
     * @param item
     *            the item to check exists.
     * @return the item (if it exists).
     */
    public static WorldItem itemExists(WorldItem item) {
        for (WorldItem w : items) {
            if (w == null) {
                continue;
            }

            if (w.getItem().getId() == item.getItem().getId() && w.getPosition().getX() == item.getPosition().getX() && w.getPosition().getY() == item.getPosition().getY() && w.getPosition().getZ() == item.getPosition().getZ()) {
                return w;
            }
        }
        return null;
    }

    /**
     * Determines if an item exists on this position or not, and if it does it
     * returns the instance of the item.
     * 
     * @param position
     *            the position to check for items.
     * @return if there are any items on this position.
     */
    public static boolean itemExistsOnPosition(Position position) {
        for (WorldItem w : items) {
            if (w == null) {
                continue;
            }

            if (w.getPosition().getX() == position.getX() && w.getPosition().getY() == position.getY() && w.getPosition().getZ() == position.getZ()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parse the all of the data for the static world items.
     * 
     * @throws JsonIOException
     *             if any i/o exceptions are thrown.
     * @throws JsonSyntaxException
     *             if the syntax is wrong.
     * @throws FileNotFoundException
     *             if the file isn't found.
     */
    @SuppressWarnings("unused")
    public static void load() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(Misc.WORLD_ITEMS));
        final Gson builder = new GsonBuilder().create();
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            WorldItem item = new WorldItem();
            item.setItem(new Item(reader.get("id").getAsInt(), reader.get("amount").getAsInt()));
            item.setPosition(new Position(reader.get("x").getAsInt(), reader.get("y").getAsInt(), reader.get("z").getAsInt()));
            item.setRespawns(reader.get("respawns").getAsBoolean());
            registerStaticItem(item);
        }
    }

    /**
     * Returns the world item.
     * 
     * @return the item.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Modifies the world item.
     * 
     * @param item
     *            the new modification.
     */
    private void setItem(Item item) {
        this.item = item;
    }

    /**
     * Returns the location of the item on the coordinate plane.
     * 
     * @return the returned location.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Modifies the location of the item on the coordinate plane.
     * 
     * @param position
     *            the new modification.
     */
    private void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Returns the item subscriber.
     * 
     * @return the returned subscriber.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Modifies the item subscriber.
     * 
     * @param player
     *            the new modification.
     */
    private void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Returns if the item has been claimed.
     * 
     * @return the returned result.
     */
    public boolean isPickedUp() {
        return pickedUp;
    }

    /**
     * Modifies if the item has been claimed.
     * 
     * @param pickedUp
     *            the modified result.
     */
    private void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    /**
     * @return the task for this item.
     */
    private Task getTask() {
        return task;
    }

    /**
     * @param task
     *            the task to set.
     */
    private void setTask(Task task) {
        this.task = task;
    }

    /**
     * @return the state.
     */
    public State getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set.
     */
    private void setState(State state) {
        this.state = state;
    }

    /**
     * @return the respawns.
     */
    public boolean isRespawns() {
        return respawns;
    }

    /**
     * @param respawns
     *            the respawns to set.
     */
    public void setRespawns(boolean respawns) {
        this.respawns = respawns;
    }
}