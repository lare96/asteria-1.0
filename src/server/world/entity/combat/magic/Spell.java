package server.world.entity.combat.magic;

import server.world.entity.Entity;
import server.world.item.Item;

/**
 * Any generic spell that can be cast in game.
 * 
 * @author lare96
 */
public abstract class Spell {

    /**
     * The id of the spell.
     */
    public abstract int spellId();

    /**
     * The level required to cast this spell.
     */
    public abstract int levelRequired();

    /**
     * The base experience gained from this spell.
     */
    public abstract int baseExperience();

    /**
     * The items required to use this spell.
     */
    public abstract Item[] itemsRequired();

    /**
     * Method called when this spell is cast.
     */
    protected abstract void castSpell(Entity cast, Entity castOn);
}
