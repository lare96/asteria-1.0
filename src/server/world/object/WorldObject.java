package server.world.object;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
 * An object placed somewhere in the region.
 * 
 * @author lare96
 */
public class WorldObject {

    /**
     * A set to keep track of the objects in the rs2 world.
     */
    private static Set<WorldObject> objects = new HashSet<WorldObject>();

    /**
     * The id of the object.
     */
    private int id;

    /**
     * The position of the object
     */
    private Position position;

    /**
     * The face of the object.
     */
    private Rotation face;

    /**
     * The type of object.
     */
    private int type;

    /**
     * All possible directions the object can be facing.
     * 
     * @author lare96
     */
    public enum Rotation {
        WEST(0), NORTH(1), EAST(2), SOUTH(3);

        /**
         * The id of the direction.
         */
        private int faceId;

        /**
         * Create a new object face.
         * 
         * @param faceId
         *            the id of the direction.
         */
        Rotation(int faceId) {
            this.setFaceId(faceId);
        }

        /**
         * @return the faceId.
         */
        public int getFaceId() {
            return faceId;
        }

        /**
         * @param faceId
         *            the faceId to set.
         */
        public void setFaceId(int faceId) {
            this.faceId = faceId;
        }
    }

    /**
     * Construct a new world object.
     * 
     * @param id
     *            the id of this object.
     * @param position
     *            the position of this object.
     * @param face
     *            the face of this object.
     * @param type
     *            the type of object.
     */
    public WorldObject(int id, Position position, Rotation face, int type) {
        this.setId(id);
        this.setPosition(position);
        this.setFace(face);
        this.setType(type);
    }

    /**
     * Parse the all of the data for the world objects.
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
        JsonArray array = (JsonArray) parser.parse(new FileReader(Misc.WORLD_OBJECTS));
        final Gson builder = new GsonBuilder().create();
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            int id = reader.get("id").getAsInt();
            int x = reader.get("x").getAsInt();
            int y = reader.get("y").getAsInt();
            int z = reader.get("z").getAsInt();
            Rotation face = Rotation.valueOf(reader.get("rotation").getAsString());

            if (face == null) {
                throw new IllegalStateException("Invalid object rotation! for [" + id + ":" + x + ":" + y + ":" + z + "]");
            }

            int type = reader.get("type").getAsInt();

            objects.add(new WorldObject(id, new Position(x, y, z), face, type));
            parsed++;
        }

        // System.out.println(parsed);
    }

    /**
     * Register a new global object that will be visible to everyone.
     * 
     * @param object
     *            the object to register.
     */
    public static void register(WorldObject object) {

        /**
         * Check if an object is already on this position, and if so it removes
         * the object from the database before spawning the new one over it.
         */
        for (Iterator<WorldObject> iter = objects.iterator(); iter.hasNext();) {
            WorldObject o = iter.next();

            if (o == null) {
                continue;
            }

            if (o.getPosition().equals(object.getPosition())) {
                iter.remove();
            }
        }

        /** Register object for future players. */
        objects.add(object);

        /** Add object for existing players (in the region) */
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            if (player.getPosition().withinDistance(object.getPosition(), 60)) {
                player.getServerPacketBuilder().sendObject(object);
            }
        }
    }

    /**
     * Unregister an existing global object that will no longer be visible to
     * anyone.
     * 
     * @param object
     *            the object to unregister.
     */
    public static void unregister(WorldObject object) {
        /** Can't remove an object that isn't there. */
        if (!objects.contains(object)) {
            return;
        }

        /** Unregister object for future players. */
        for (Iterator<WorldObject> iter = objects.iterator(); iter.hasNext();) {
            WorldObject o = iter.next();

            if (o == null) {
                continue;
            }

            if (o.equals(object)) {
                iter.remove();
            }
        }

        /** Remove object for all existing players. */
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            player.getServerPacketBuilder().removeObject(object);
        }
    }

    /**
     * Unregisters an existing global object not in the database (spawned from
     * the client).
     * 
     * @param object
     *            the object to unregister.
     */
    public static void unregisterNoDatabase(WorldObject object) {

        /** Remove object for all existing players. */
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            player.getServerPacketBuilder().removeObject(object);
        }
    }

    /**
     * Load registered objects for a player.
     * 
     * @param player
     *            the player to load the objects for.
     */
    public static void load(Player player) {

        /** Update existing objects for player in region. */
        for (WorldObject object : objects) {
            if (object == null) {
                continue;
            }

            if (object.getPosition().withinDistance(player.getPosition(), 60)) {
                player.getServerPacketBuilder().sendObject(object);
            }
        }
    }

    /**
     * Removes any ground items for a player that aren't on the same height
     * level.
     * 
     * @param player
     *            the player to remove the items for.
     */
    public static void removeAllHeight(Player player) {
        for (final WorldObject w : objects) {
            if (w == null) {
                continue;
            }

            if (player.getPosition().getZ() != w.getPosition().getZ()) {
                player.getServerPacketBuilder().removeObject(w);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WorldObject)) {
            return false;
        }

        WorldObject o = (WorldObject) obj;

        return o.getId() == this.getId() && o.getPosition().equals(this.getPosition()) && o.getFace() == this.getFace() && o.getType() == this.getType() ? true : false;
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
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * @return the face.
     */
    public Rotation getFace() {
        return face;
    }

    /**
     * @param face
     *            the face to set.
     */
    public void setFace(Rotation face) {
        this.face = face;
    }

    /**
     * @return the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the objects.
     */
    public static Set<WorldObject> getObjects() {
        return objects;
    }
}
