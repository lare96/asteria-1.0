package server.world.entity;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.world.DynamicActionContainer;

/**
 * The current teleportation methods.
 * 
 * @author lare96
 */
public enum Teleport {

    NORMAL_SPELLBOOK_TELEPORT(new DynamicActionContainer<Entity>() {
        @Override
        public void run(final Entity entity) {
            entity.animation(new Animation(714));

            GameLogic.getSingleton().submit(new Task(1, false, Time.TICK) {
                @Override
                public void logic() {
                    if (entity.isUnregistered()) {
                        this.cancel();
                        return;
                    }

                    if (entity.getTeleportStage() == 1) {
                        entity.gfx(new Gfx(308));
                        entity.setTeleportStage(2);
                    } else if (entity.getTeleportStage() == 2) {
                        entity.setTeleportStage(3);
                    } else if (entity.getTeleportStage() == 3) {
                        entity.move(entity.getTeleport());
                        entity.animation(new Animation(715));
                        entity.setTeleportStage(0);
                        this.cancel();
                    }
                }
            });
        }
    }),

    ANCIENTS_SPELLBOOK_TELEPORT(new DynamicActionContainer<Entity>() {
        @Override
        public void run(final Entity entity) {
            entity.animation(new Animation(1979));

            GameLogic.getSingleton().submit(new Task(1, false, Time.TICK) {
                @Override
                public void logic() {
                    if (entity.isUnregistered()) {
                        this.cancel();
                        return;
                    }

                    if (entity.getTeleportStage() == 1) {
                        entity.gfx(new Gfx(392));
                        entity.setTeleportStage(2);
                    } else if (entity.getTeleportStage() == 2) {
                        entity.setTeleportStage(3);
                    } else if (entity.getTeleportStage() == 3) {
                        entity.setTeleportStage(4);
                    } else if (entity.getTeleportStage() == 4) {
                        entity.move(entity.getTeleport());
                        entity.setTeleportStage(0);
                        this.cancel();
                    }
                }
            });
        }
    });

    /**
     * The teleportation action.
     */
    private DynamicActionContainer<Entity> action;

    /**
     * Create a new teleport action.
     * 
     * @param action
     *            the action for this teleport.
     */
    Teleport(DynamicActionContainer<Entity> action) {
        this.setAction(action);
    }

    /**
     * @return the action.
     */
    public DynamicActionContainer<Entity> getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set.
     */
    public void setAction(DynamicActionContainer<Entity> action) {
        this.action = action;
    }
}
