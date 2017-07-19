package server.world.entity;

/**
 * Represents a graphic that can be performed by an entity.
 * 
 * @author lare96
 */
public class Gfx {

    /**
     * The id of the gfx.
     */
    private int id;

    /**
     * The delay before the gfx executes in ticks.
     */
    private int delay;

    /**
     * Creates a new graphic.
     * 
     * @param id
     *            the id of the graphic.
     * @param delay
     *            the delay for the graphic.
     */
    public Gfx(int id, int delay) {
        this.setId(id);
        this.setDelay(delay);
    }

    /**
     * Creates a new graphic.
     * 
     * @param id
     *            the id of the graphic.
     */
    public Gfx(int id) {
        this.setId(id);
        this.setDelay(0);
    }

    /**
     * Creates a new graphic with the default values.
     */
    public Gfx() {

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
     * @return the delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @param delay
     *            the delay to set.
     */
    public void setDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Cannot have a delay below 0!");
        }

        this.delay = delay;
    }

    /**
     * Sets this gfx as the other gfx.
     * 
     * @param other
     *            the other gfx.
     * @return this gfx.
     */
    public Gfx setAs(Gfx other) {
        this.id = other.id;
        this.delay = other.delay;
        return this;
    }
}
