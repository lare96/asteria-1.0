package server.world.entity.combat.magic;

import server.world.entity.combat.Projectile;

/**
 * Represents a spell projectile.
 * 
 * @author lare96
 */
public class SpellProjectile {

    /**
     * The gfx played when a spell is cast.
     */
    private Projectile castGraphic;

    /**
     * The gfx played when a spell is in mid air.
     */
    private Projectile projectileGraphic;

    /**
     * The gfx played when the spell hits.
     */
    private Projectile collideGraphic;

    /**
     * Create a new spell projectile.
     * 
     * @param castGraphic
     *            the cast graphic.
     * @param projectileGraphic
     *            the projectile graphic.
     * @param collideGraphic
     *            the graphic when the spell hits.
     */
    public SpellProjectile(Projectile castGraphic, Projectile projectileGraphic, Projectile collideGraphic) {
        this.setCastGraphic(castGraphic);
        this.setProjectileGraphic(projectileGraphic);
        this.setCollideGraphic(collideGraphic);
    }

    /**
     * @return the castGraphic.
     */
    public Projectile getCastGraphic() {
        return castGraphic;
    }

    /**
     * @param castGraphic
     *            the castGraphic to set.
     */
    public void setCastGraphic(Projectile castGraphic) {
        this.castGraphic = castGraphic;
    }

    /**
     * @return the projectileGraphic.
     */
    public Projectile getProjectileGraphic() {
        return projectileGraphic;
    }

    /**
     * @param projectileGraphic
     *            the projectileGraphic to set.
     */
    public void setProjectileGraphic(Projectile projectileGraphic) {
        this.projectileGraphic = projectileGraphic;
    }

    /**
     * @return the collideGraphic.
     */
    public Projectile getCollideGraphic() {
        return collideGraphic;
    }

    /**
     * @param collideGraphic
     *            the collideGraphic to set.
     */
    public void setCollideGraphic(Projectile collideGraphic) {
        this.collideGraphic = collideGraphic;
    }
}
