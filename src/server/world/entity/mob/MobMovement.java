package server.world.entity.mob;

import server.util.Misc;
import server.world.map.Position;

/**
 * Handles random movement for a single mob. In order to keep the mob in the
 * general area of where it was spawned, walking states are used. Each mob has
 * it's own walking personal walking state, and if that state is equal to
 * <code>AT_ORIGINAL_POSITION</code> the next movement for the mob will be in
 * a two tile radius away from its original position. If that walking state is
 * equal to <code>AWAY_FROM_ORGINAL_POSITION</code> then the next movement for
 * the mob will be wherever the mob was originally spawned. This prevents mobs
 * from wandering off when walking randomly.
 * 
 * @author lare96
 */
public class MobMovement {

    // FIXME: Does not work on any height level other than 0?

    /**
     * The mob that will be walking.
     */
    private Mob mob;

    /**
     * The current state the mob is in.
     */
    private State walkingState = State.AT_ORIGINAL_POSITION;

    /**
     * If this mob is currently walking randomly.
     */
    private boolean randomWalking;

    /**
     * Construct a new class to handle movement for a mob.
     * 
     * @param mob
     *            the mob to handle random movement for.
     */
    protected MobMovement(Mob mob) {
        this.setMob(mob);
    }

    /**
     * All the possible walking states for the mob to be in.
     */
    private enum State {
        AT_ORIGINAL_POSITION,

        AWAY_FROM_ORGINAL_POSITION
    }

    /**
     * Gets a random position for this mob to walk in and moves it to that
     * position.
     */
    protected void walk() {

        /** Block if this mob is not set to walk randomly. */
        if (!this.isRandomWalking()) {
            return;
        }

        /** 1/11 Chance to walk randomly every tick. */
        if (Misc.getRandom().nextInt(10) == 0) {

            /**
             * Grab a position relative to the mobs current position depending
             * on its current walking state.
             */
            Position position = randomRelativePosition();

            if (position != null) {

                /** Walk to the selected position. */
                mob.getMovementQueue().walk(position.getX(), position.getY());
            }
        }
    }

    /**
     * Spits out a random position relative to the mobs current position. The
     * position is affected by the mobs current walking state.
     * 
     * @return the random position relative to the mobs current position.
     */
    private Position randomRelativePosition() {

        /**
         * If the mob is at it's original position generate another position
         * within a two-tile radius for the mob to walk in.
         */
        if (this.getWalkingState() == State.AT_ORIGINAL_POSITION) {
            int x = Misc.getRandom().nextInt(2);
            int y = Misc.getRandom().nextInt(2);

            this.setWalkingState(State.AWAY_FROM_ORGINAL_POSITION);
            return new Position(x, y);

            /**
             * If the mob is away from it's original position, walk back to its
             * original position to prevent this mob from wandering off.
             */
        } else {
            this.setWalkingState(State.AT_ORIGINAL_POSITION);

            int x = mob.getOriginalPosition().getX() - mob.getPosition().getX();
            int y = mob.getOriginalPosition().getY() - mob.getPosition().getY();

            return new Position(x, y);
        }
    }

    /**
     * @return the walkingState.
     */
    private State getWalkingState() {
        return walkingState;
    }

    /**
     * @param walkingState
     *            the walkingState to set.
     */
    private void setWalkingState(State walkingState) {
        this.walkingState = walkingState;
    }

    /**
     * @return the randomWalking.
     */
    public boolean isRandomWalking() {
        return randomWalking;
    }

    /**
     * @param randomWalking
     *            the randomWalking to set.
     */
    public void setRandomWalking(boolean randomWalking) {
        this.randomWalking = randomWalking;
    }

    /**
     * @param mob
     *            the mob to set.
     */
    private void setMob(Mob mob) {
        this.mob = mob;
    }
}
