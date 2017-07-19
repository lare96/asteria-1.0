package server.world.map;

/**
 * A tile on the constructed map region palette.
 * 
 * @author lare96
 * @author Graham
 */
public class MapRegionTile {

    /**
     * The id's of all of the possible directions of a tile on the palette.
     */
    public static final int DIRECTION_NORMAL = 0, DIRECTION_CW_0 = 0,
            DIRECTION_CW_90 = 1, DIRECTION_CW_180 = 2, DIRECTION_CW_270 = 3;

    /**
     * The x coordinate of the tile.
     */
    private int x;

    /**
     * The y coordinate of the tile.
     */
    private int y;

    /**
     * The z coordinate of the tile.
     */
    private int z;

    /**
     * The direction of the tile.
     */
    private int rot;

    /**
     * Constructs a new map region tile.
     * 
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     */
    public MapRegionTile(int x, int y) {
        this(x, y, 0);
    }

    /**
     * Constructs a new map region tile.
     * 
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     * @param z
     *            the z coordinate.
     */
    public MapRegionTile(int x, int y, int z) {
        this(x, y, z, DIRECTION_NORMAL);
    }

    /**
     * Constructs a new map region tile.
     * 
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     * @param z
     *            the z coordinate.
     * @param rot
     *            the direction this tile is facing.
     */
    public MapRegionTile(int x, int y, int z, int rot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rot = rot;
    }

    /**
     * Gets the x coordinate.
     * 
     * @return the x coordinate.
     */
    public int getX() {
        return x / 8;
    }

    /**
     * Gets the y coordinate.
     * 
     * @return the y coordinate.
     */
    public int getY() {
        return y / 8;
    }

    /**
     * Gets the z coordinate.
     * 
     * @return the z coordinate.
     */
    public int getZ() {
        return z % 4;
    }

    /**
     * Gets the direction of the tile.
     * 
     * @return the direction.
     */
    public int getRotation() {
        return rot % 4;
    }
}
