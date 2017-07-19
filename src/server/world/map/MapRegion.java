package server.world.map;

/**
 * A map region constructed from a collection of map region tiles.
 * 
 * @author lare96
 * @author Graham
 */
public class MapRegion {

    // XXX: I've tried and tried and tried again to get this to work but I am
    // completely unsure whether it is the client or this code that's wrong.

    // XXX: The custom map region loads but it's like a completely black region
    // (if you teleport to the coordinates [1, 1] it looks like
    // that). A better way to put it would be to say that the new map region
    // loads but no tiles load on it.

    // XXX: Maybe I'm loading it incorrectly, but you can see for yourself by
    // typing in ::region or looking at the code I'm using to load it in the
    // commands packet.

    /**
     * A chunk of tiles that make up this map region.
     */
    private MapRegionTile[][][] region;

    /**
     * The constant x length of this map region.
     */
    public static final int SIZE_LENGTH_X = 13;

    /**
     * The constant y length of this map region.
     */
    public static final int SIZE_LENGTH_Y = 13;

    /**
     * The constant z length of this map region.
     */
    public static final int SIZE_LENGTH_Z = 4;

    /**
     * Creates a new custom map region.
     */
    public MapRegion() {
        this.setRegion(new MapRegionTile[SIZE_LENGTH_X][SIZE_LENGTH_Y][SIZE_LENGTH_Z]);
    }

    /**
     * Gets a tile in the map region.
     * 
     * @param x
     *            the x coordinate in the region.
     * @param y
     *            the y coordinate in the region.
     * @param z
     *            the z coordinate in the region.
     * @return the map tile on this coordinate.
     */
    public MapRegionTile getTile(int x, int y, int z) {
        return region[x][y][z];
    }

    /**
     * Sets a tile on the map region.
     * 
     * @param x
     *            the x coordinate in the region.
     * @param y
     *            the y coordinate in the region.
     * @param z
     *            the z coordinate in the region.
     * @param tile
     *            the map tile to set.
     * @return this map region.
     */
    public MapRegion setTile(int x, int y, int z, MapRegionTile tile) {
        region[x][y][z] = tile;
        return this;
    }

    /**
     * Gets this map region.
     * 
     * @return this map region.
     */
    public MapRegionTile[][][] getRegion() {
        return region;
    }

    /**
     * Overrides this map region tileset with another map region tileset.
     * 
     * @param region
     *            the new region set.
     * @return the new map region.
     */
    public MapRegion setRegion(MapRegionTile[][][] region) {
        this.region = region;
        return this;
    }
}
