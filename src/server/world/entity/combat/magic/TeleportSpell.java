package server.world.entity.combat.magic;

import server.world.entity.Entity;
import server.world.entity.Teleport;
import server.world.map.Position;

/**
 * Represents any teleportation spell that can be cast in game.
 * 
 * @author lare96
 */
public abstract class TeleportSpell extends Spell {

    /**
     * The position to teleport whomever.
     */
    public abstract Position teleportTo();

    /**
     * The teleport type of this spell.
     */
    public abstract Teleport type();

    @Override
    public int spellId() {
        /** Teleportation spells do not have an 'id'. */
        return -1;
    }

    @Override
    protected void castSpell(Entity cast, Entity castOn) {
        cast.teleport(this);
    }
}