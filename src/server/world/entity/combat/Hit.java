package server.world.entity.combat;

/**
 * A hit that can be inflicted on an entity.
 * 
 * @author lare96
 */
public class Hit {

    /**
     * The amount of damage inflicted.
     */
    private int damage;

    /**
     * The damage type.
     */
    private DamageType damageType;

    /**
     * Create a new hit.
     * 
     * @param damage
     *            the amount of damage.
     * @param damageType
     *            the type of damage.
     */
    public Hit(int damage, DamageType damageType) {
        this.setDamage(damage);
        this.setDamageType(damageType);

        if (this.getDamage() == 0) {
            this.setDamageType(DamageType.BLOCKED);
        } else if (this.getDamageType() == DamageType.BLOCKED) {
            this.setDamage(0);
        }
    }

    /**
     * Create a new hit.
     * 
     * @param damage
     *            the amount of damage.
     */
    public Hit(int damage) {
        this.setDamage(damage);
        this.setDamageType(DamageType.NORMAL);

        if (this.getDamage() == 0) {
            this.setDamageType(DamageType.BLOCKED);
        }
    }

    /**
     * The different types of damages.
     */
    public enum DamageType {
        BLOCKED, // 0 - BLUE

        NORMAL, // 1 - RED

        POISON, // 2 - GREEN

        DISEASE, // 3 - ORANGE
    }

    /**
     * @return the damage.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @param damage
     *            the damage to set.
     */
    private void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * @return the damageType.
     */
    public DamageType getDamageType() {
        return damageType;
    }

    /**
     * @param damageType
     *            the damageType to set.
     */
    private void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }
}
