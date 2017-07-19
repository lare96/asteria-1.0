package server.world.entity.combat;

/**
 * A projectile used when doing things like firing arrows or casting spells.
 * 
 * @author lare96
 */
public class Projectile {

    /**
     * The id of the projectile.
     */
    private int id;

    /**
     * The height of the projectile.
     */
    private int height;

    /**
     * The delay of the projectile.
     */
    private int delay;

    /**
     * Create a new projectile.
     * 
     * @param projectileId
     *            the projectile id.
     * @param projectileHeight
     *            the projectile height.
     * @param projectileDelay
     *            the projectile delay.
     */
    public Projectile(int projectileId, int projectileHeight, int projectileDelay) {
        this.setProjectileId(projectileId);
        this.setProjectileHeight(projectileHeight);
        this.setProjectileDelay(projectileDelay);
    }

    /**
     * Create a new projectile.
     * 
     * @param projectileId
     *            the projectile id.
     * @param projectileHeight
     *            the projectile height.
     */
    public Projectile(int projectileId, int projectileHeight) {
        this.setProjectileId(projectileId);
        this.setProjectileHeight(projectileHeight);
        this.setProjectileDelay(0);
    }

    /**
     * Create a new projectile.
     * 
     * @param projectileId
     *            the projectile id.
     */
    public Projectile(int projectileId) {
        this.setProjectileId(projectileId);
        this.setProjectileHeight(0);
        this.setProjectileDelay(0);
    }

    /**
     * @return the projectileId.
     */
    public int getProjectileId() {
        return id;
    }

    /**
     * @param projectileId
     *            the projectileId to set.
     */
    public void setProjectileId(int projectileId) {
        this.id = projectileId;
    }

    /**
     * @return the projectileHeight.
     */
    public int getProjectileHeight() {
        return height;
    }

    /**
     * @param projectileHeight
     *            the projectileHeight to set.
     */
    public void setProjectileHeight(int projectileHeight) {
        this.height = projectileHeight;
    }

    /**
     * @return the projectileDelay.
     */
    public int getProjectileDelay() {
        return delay;
    }

    /**
     * @param projectileDelay
     *            the projectileDelay to set.
     */
    public void setProjectileDelay(int projectileDelay) {
        this.delay = projectileDelay;
    }
}
