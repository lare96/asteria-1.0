package server.world.entity;

/**
 * An animation that can be performed by an entity.
 * 
 * @author lare96
 */
public class Animation {

    /**
     * The id of the animation.
     */
    private int id;

    /**
     * The delay before the animation executes in ticks.
     */
    private int delay;

    /**
     * Create a new animation.
     * 
     * @param id
     *            the id of the animation.
     * @param delay
     *            the delay of the animation.
     */
    public Animation(int id, int delay) {
        this.setId(id);
        this.setDelay(delay);
    }

    /**
     * Create a new animation with a delay of 0.
     * 
     * @param id
     *            the id of the animation.
     */
    public Animation(int id) {
        this.setId(id);
        this.setDelay(0);
    }

    /**
     * Create a new animation with the default values.
     */
    public Animation() {

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
     * Sets this animation as the other animation.
     * 
     * @param other
     *            the other animation.
     * @return this animation.
     */
    public Animation setAs(Animation other) {
        this.id = other.id;
        this.delay = other.delay;
        return this;
    }
}
