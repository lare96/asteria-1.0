package server.world.entity;

import server.logic.GameLogic;
import server.logic.task.listener.EventListener;

/**
 * A listener that executes actions when the walking queue is finished.
 * 
 * @author lare96
 */
public class MovementQueueListener {

    /**
     * The entity that the action will be added for.
     */
    private Entity entity;

    /**
     * Listener that will listen for the end of the walking queue.
     */
    private EventListener listener;

    /**
     * Create a new container for our listener.
     * 
     * @param entity
     *            the entity that the container will be made for.
     */
    public MovementQueueListener(Entity entity) {
        this.entity = entity;
    }

    /**
     * Adds an action that will execute once the walking queue is finished. If
     * we are already listening for a walking queue to finish, the old action is
     * stopped and replaced with the new one.
     * 
     * @param action
     *            the action to run once the walking queue is finished.
     */
    public void submit(final Runnable action) {

        /** Stop any existing actions. */
        if (this.getListener() != null) {
            if (this.getListener().isRunning()) {
                this.getListener().cancel();
            }
        }

        /** And begin listening for a new action. */
        this.setListener(new EventListener() {

            @Override
            public boolean listenForEvent() {
                return entity.getMovementQueue().isMovementDone() || entity.isUnregistered() ? false : true;

            }

            @Override
            public void run() {

                /** Attempt to run the action. */
                try {
                    if (entity.isUnregistered()) {
                        return;
                    }

                    action.run();

                    /** Handle any errors we may come across. */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /** Schedule the listener. */
        GameLogic.getSingleton().submit(this.getListener());
    }

    /**
     * @return the listener.
     */
    private EventListener getListener() {
        return listener;
    }

    /**
     * @param listener
     *            the listener to set.
     */
    private void setListener(EventListener listener) {
        this.listener = listener;
    }
}
