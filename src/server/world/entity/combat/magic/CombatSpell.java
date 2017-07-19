package server.world.entity.combat.magic;

import java.lang.reflect.Method;

import server.world.entity.Animation;
import server.world.entity.Entity;

/**
 * Represents any combat spell that can be cast in game.
 * 
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

    /**
     * The animation that will be played for the entity casting the spell.
     */
    public abstract Animation castAnimation();

    /**
     * The maximum damage for this spell.
     */
    public abstract int maxDamage();

    /**
     * The spell projectiles.
     */
    public abstract SpellProjectile spellProjectile();

    /**
     * Method loaded from a different class that will determine what happens
     * when the entity is hit with the spell.
     */
    public abstract Method onHit();

    @Override
    protected void castSpell(Entity cast, Entity castOn) {
        // TODO Auto-generated method stub
    }
}
