package server.world.entity.mob;

import java.io.FileNotFoundException;
import java.io.FileReader;

import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.magic.TeleportSpell;
import server.world.map.Position;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * A non-player-character that extends Entity so that we can share the many
 * similar attributes.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Mob extends Entity {

    /**
     * The mob ID.
     */
    private int mobId;

    /**
     * Whether or not the mob is visible.
     */
    private boolean isVisible = true;

    /**
     * The mobs max health.
     */
    private int maxHealth;

    /**
     * The mobs current health.
     */
    private int currentHealth;

    /**
     * If this mob respawns or not.
     */
    private boolean respawn = true;

    /**
     * The mobs position from the moment of conception. This position never
     * changes.
     */
    private Position originalPosition;

    /**
     * If this mob was originally random walking.
     */
    private boolean originalRandomWalk;

    /**
     * The respawn ticks.
     */
    private int respawnTicks;

    /**
     * Handles random walking for this mob.
     */
    private MobMovement randomWalking = new MobMovement(this);

    /**
     * Creates a new Mob.
     * 
     * @param mobId
     *            the mob ID.
     * @param position
     *            the mobs position.
     */
    public Mob(int mobId, Position position) {
        this.setMobId(mobId);
        this.getPosition().setAs(position);
        this.setOriginalPosition(new Position().setAs(position));
        this.setMaxHealth(Mob.getDefinition(this.getMobId()).getHitpoints());
        this.setCurrentHealth(Mob.getDefinition(this.getMobId()).getHitpoints());
        this.setAutoRetaliate(true);
        this.getFlags().flag(Flag.APPEARANCE);
    }

    @Override
    public void engineWork() throws Exception {
        this.getRandomWalking().walk();
        this.getMovementQueue().execute();
    }

    @Override
    public Task onDeath() throws Exception {
        return new Task(1, false, Time.TICK) {
            @Override
            public void logic() {
                if (getDeathTicks() == 1) {
                    animation(new Animation(Mob.getDefinition(getMobId()).getDeathAnimation()));
                } else if (getDeathTicks() == 6) {

                    if (getRespawnTicks() == 0) {
                        // XXX: The mob would drop items here! Example...
                        // new WorldItem(new Item(526), new
                        // Position(getPosition().getX(), getPosition().getY()),
                        // World.getPlayer("lare96")).register();

                        move(new Position(1, 1));

                        if (!isRespawn()) {
                            this.cancel();
                        }
                    }

                    // XXX: Pretty messy way to do respawning. Will most likely
                    // be re-done in the future.
                    if (getRespawnTicks() == (getDefinition(getMobId()).getRespawnTime() == 0 ? 1 : getDefinition(getMobId()).getRespawnTime()) * 2) {
                        getPosition().setAs(getOriginalPosition());
                        register();
                        this.cancel();
                    } else {
                        incrementRespawnTicks();
                    }
                    return;
                }

                incrementDeathTicks();
            }
        };
    }

    @Override
    public void teleport(TeleportSpell spell) {
        if (getTeleportStage() > 0) {
            return;
        }

        getMovementQueue().reset();
        setTeleportStage(1);
        getTeleport().setAs(new Position(0, 0, 0));
        spell.type().getAction().run(this);
    }

    @Override
    public void move(Position position) {
        getMovementQueue().reset();
        getPosition().setAs(position);
        getFlags().flag(Flag.APPEARANCE);
        unregister();
    }

    @Override
    public void register() {
        for (int i = 1; i < World.getNpcs().length; i++) {
            if (World.getNpcs()[i] == null) {
                World.getNpcs()[i] = this;
                this.setSlot(i);
                return;
            }
        }
        throw new IllegalStateException("Server is full!");
    }

    @Override
    public void unregister() {
        if (this.getSlot() == -1) {
            return;
        }

        World.getNpcs()[this.getSlot()] = null;
        this.setUnregistered(true);
    }

    /**
     * Loads and spawns mobs on startup.
     * 
     * @throws JsonIOException
     *             if any i/o exceptions are thrown.
     * @throws JsonSyntaxException
     *             if the syntax is wrong.
     * @throws FileNotFoundException
     *             if the file isn't found.
     */
    @SuppressWarnings("unused")
    public static void load() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(Misc.WORLD_MOBS));
        final Gson builder = new GsonBuilder().create();
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            int id = reader.get("id").getAsInt();
            int x = reader.get("x").getAsInt();
            int y = reader.get("y").getAsInt();
            int z = reader.get("z").getAsInt();
            boolean randomWalk = reader.get("random-walk").getAsBoolean();

            Mob mob = new Mob(id, new Position(x, y, z));
            World.register(mob);
            mob.getRandomWalking().setRandomWalking(randomWalk);
            mob.setOriginalRandomWalk(randomWalk);
            parsed++;
        }
    }

    /**
     * Increases this mobs health.
     * 
     * @param amount
     *            the amount to increase by.
     */
    public void increaseHealth(int amount) {
        if ((currentHealth + amount) > maxHealth) {
            currentHealth = maxHealth;
            return;
        }

        currentHealth += amount;
    }

    /**
     * Decreases this mobs health.
     * 
     * @param amount
     *            the amount to decrease by.
     */
    public void decreaseHealth(int amount) {
        if ((currentHealth - amount) < 0) {
            currentHealth = 0;
            return;
        }

        currentHealth -= amount;
    }

    /**
     * Sets the Mob ID.
     * 
     * @param mobId
     *            the mobId.
     */
    public void setMobId(int mobId) {
        this.mobId = mobId;
    }

    /**
     * Gets the Mob ID.
     * 
     * @return the mobId.
     */
    public int getMobId() {
        return mobId;
    }

    /**
     * @return the isVisible.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * @param isVisible
     *            the isVisible to set.
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * @return the maxHealth.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * @param maxHealth
     *            the maxHealth to set.
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * @return the currentHealth.
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * @param currentHealth
     *            the currentHealth to set.
     */
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    /**
     * @return the originalPosition.
     */
    public Position getOriginalPosition() {
        return originalPosition;
    }

    /**
     * @param originalPosition
     *            the originalPosition to set.
     */
    public void setOriginalPosition(Position originalPosition) {
        this.originalPosition = originalPosition;
    }

    /**
     * @return the randomWalking.
     */
    public MobMovement getRandomWalking() {
        return randomWalking;
    }

    /**
     * Gets a mob definition.
     * 
     * @param id
     *            the mob definition to get.
     * @return the definition.
     */
    public static MobDefinition getDefinition(int id) {
        return MobDefinition.getMobDefinition()[id];
    }

    /**
     * @return the originalRandomWalk.
     */
    public boolean isOriginalRandomWalk() {
        return originalRandomWalk;
    }

    /**
     * @param originalRandomWalk
     *            the originalRandomWalk to set.
     */
    public void setOriginalRandomWalk(boolean originalRandomWalk) {
        this.originalRandomWalk = originalRandomWalk;
    }

    /**
     * @param respawn
     *            the respawn to set.
     */
    public void setRespawn(boolean respawn) {
        this.respawn = respawn;
    }

    /**
     * @return the respawn.
     */
    public boolean isRespawn() {
        return respawn;
    }

    /**
     * @return the respawnTicks.
     */
    public int getRespawnTicks() {
        return respawnTicks;
    }

    /**
     * @param respawnTicks
     *            the respawnTicks to set.
     */
    public void setRespawnTicks(int respawnTicks) {
        this.respawnTicks = respawnTicks;
    }

    /**
     * Increments the respawn ticks.
     */
    public void incrementRespawnTicks() {
        this.respawnTicks++;
    }
}
