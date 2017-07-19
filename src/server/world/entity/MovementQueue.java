package server.world.entity;

import java.util.Deque;
import java.util.LinkedList;

import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.util.Misc;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.mob.Mob;
import server.world.entity.player.Player;
import server.world.map.Position;

/**
 * Handles the movement of an Entity.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class MovementQueue {

    /**
     * The entity trying to move.
     */
    private final Entity entity;

    /**
     * Queue of waypoints for the entity.
     */
    private Deque<Point> waypoints = new LinkedList<Point>();

    /**
     * If your run is toggled.
     */
    private boolean runToggled = false;

    /**
     * If the current path is a run path.
     */
    private boolean runPath = false;

    /**
     * If this entity's movement is locked.
     */
    private boolean lockMovement;

    /**
     * Creates a new movement queue.
     * 
     * @param entity
     *            the entity to create this queue for.
     */
    public MovementQueue(Entity entity) {
        this.entity = entity;
    }

    /**
     * Movement processing.
     */
    public void execute() {
        Point walkPoint = null;
        Point runPoint = null;

        /** Handle the movement. */
        walkPoint = waypoints.poll();
        if (isRunToggled()) {
            runPoint = waypoints.poll();
        }

        /** Decide if this is a run path or not. */
        if (runPoint != null) {
            this.setRunPath(true);
        } else {
            this.setRunPath(false);
        }

        /** Walk if this is a walk point. */
        if (walkPoint != null && walkPoint.getDirection() != -1) {
            entity.getPosition().move(Misc.DIRECTION_DELTA_X[walkPoint.getDirection()], Misc.DIRECTION_DELTA_Y[walkPoint.getDirection()]);
            entity.setPrimaryDirection(walkPoint.getDirection());
        }

        /** Run if this is a run point. */
        if (runPoint != null && runPoint.getDirection() != -1) {
            if (entity instanceof Player) {
                if (((Player) entity).getRunEnergy() > 0) {
                    ((Player) entity).decrementRunEnergy();
                } else {
                    setRunToggled(false);
                }
            }

            entity.getPosition().move(Misc.DIRECTION_DELTA_X[runPoint.getDirection()], Misc.DIRECTION_DELTA_Y[runPoint.getDirection()]);
            entity.setSecondaryDirection(runPoint.getDirection());
        }

        /** Check for region changes. */
        int deltaX = entity.getPosition().getX() - entity.getCurrentRegion().getRegionX() * 8;
        int deltaY = entity.getPosition().getY() - entity.getCurrentRegion().getRegionY() * 8;
        if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
            if (!(entity instanceof Mob)) {
                ((Player) entity).getServerPacketBuilder().sendMapRegion();
            }
        }
    }

    /**
     * Allow the entity to walk to a certain position point relevant to its
     * current position.
     * 
     * @param addX
     *            the amount of spaces to walk to the x.
     * @param addY
     *            the amount of spaces to walk to the y.
     */
    public void walk(int addX, int addY) {
        this.reset();
        this.addToPath(new Position(entity.getPosition().getX() + addX, entity.getPosition().getY() + addY));
        this.finish();

        if (entity instanceof Mob) {
            ((Mob) entity).getFlags().flag(Flag.APPEARANCE);
        }
    }

    /**
     * Allow the entity to walk to a certain position point not relevant to its
     * current position.
     * 
     * @param position
     *            the position the entity is moving too.
     */
    public void walk(Position position) {
        this.reset();
        this.addToPath(position);
        this.finish();

        if (entity instanceof Mob) {
            ((Mob) entity).getFlags().flag(Flag.APPEARANCE);
        }
    }

    /**
     * Resets the walking queue.
     */
    public void reset() {
        setRunPath(false);
        waypoints.clear();

        /** Set the base point as this position. */
        Position p = entity.getPosition();
        waypoints.add(new Point(p.getX(), p.getY(), -1));
    }

    /**
     * Finishes the current path.
     */
    public void finish() {
        waypoints.removeFirst();
    }

    /**
     * Returns if the walking queue is finished or not.
     */
    public boolean isMovementDone() {
        return waypoints.size() == 0;
    }

    /**
     * Adds a position to the path.
     * 
     * @param position
     *            the position.
     */
    public void addToPath(Position position) {
        if (waypoints.size() == 0) {
            reset();
        }
        Point last = waypoints.peekLast();
        int deltaX = position.getX() - last.getX();
        int deltaY = position.getY() - last.getY();
        int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
        for (int i = 0; i < max; i++) {
            if (deltaX < 0) {
                deltaX++;
            } else if (deltaX > 0) {
                deltaX--;
            }
            if (deltaY < 0) {
                deltaY++;
            } else if (deltaY > 0) {
                deltaY--;
            }
            addStep(position.getX() - deltaX, position.getY() - deltaY);
        }
    }

    /**
     * Adds a step.
     * 
     * @param x
     *            the X coordinate
     * @param y
     *            the Y coordinate
     */
    private void addStep(int x, int y) {
        if (waypoints.size() >= 100) {
            return;
        }
        Point last = waypoints.peekLast();
        int deltaX = x - last.getX();
        int deltaY = y - last.getY();
        int direction = Misc.direction(deltaX, deltaY);
        if (direction > -1) {
            waypoints.add(new Point(x, y, direction));
        }
    }

    /**
     * Locks this entity's movement for the desired time.
     * 
     * @param delay
     *            the desired time.
     * @param time
     *            the desired time unit.
     */
    public void lockMovementFor(int delay, Time time) {
        this.setLockMovement(true);

        GameLogic.getSingleton().submit(new Task(delay, false, time) {
            @Override
            public void logic() {
                setLockMovement(false);
                this.cancel();
            }
        });
    }

    /**
     * Toggles the running flag.
     * 
     * @param runToggled
     *            the flag.
     */
    public void setRunToggled(boolean runToggled) {
        this.runToggled = runToggled;
    }

    /**
     * Gets whether or not run is toggled.
     * 
     * @return run toggled.
     */
    public boolean isRunToggled() {
        return runToggled;
    }

    /**
     * Toggles running for the current path only.
     * 
     * @param runPath
     *            the flag.
     */
    public void setRunPath(boolean runPath) {
        this.runPath = runPath;
    }

    /**
     * Gets whether or not we're running for the current path.
     * 
     * @return running.
     */
    public boolean isRunPath() {
        return runPath;
    }

    /**
     * An internal Position type class with support for direction.
     * 
     * @author blakeman8192
     */
    private class Point extends Position {

        /** The direction. */
        private int direction;

        /**
         * Creates a new Point.
         * 
         * @param x
         *            the X coordinate.
         * @param y
         *            the Y coordinate.
         * @param direction
         *            the direction to this point.
         */
        public Point(int x, int y, int direction) {
            super(x, y);
            setDirection(direction);
        }

        /**
         * Sets the direction.
         * 
         * @param direction
         *            the direction.
         */
        public void setDirection(int direction) {
            this.direction = direction;
        }

        /**
         * Gets the direction.
         * 
         * @return the direction.
         */
        public int getDirection() {
            return direction;
        }
    }

    /**
     * @return the lockMovement
     */
    public boolean isLockMovement() {
        return lockMovement;
    }

    /**
     * @param lockMovement
     *            the lockMovement to set
     */
    public void setLockMovement(boolean lockMovement) {
        this.lockMovement = lockMovement;
    }
}
