package server.world;

/**
 * A container that contains a single runnable method with any Object as its
 * parameter.
 * 
 * @author lare96
 * @param <T>
 *            The type of object taking on this action.
 */
public interface DynamicActionContainer<T> {

    /**
     * The runnable action.
     * 
     * @param param
     *            any Object that will take on this action.
     */
    void run(final T param);
}
