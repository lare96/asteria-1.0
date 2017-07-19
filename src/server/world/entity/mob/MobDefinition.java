package server.world.entity.mob;

import java.io.FileNotFoundException;
import java.io.FileReader;

import server.util.Misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * A single npc definition.
 * 
 * @author lare96
 */
public class MobDefinition {

    /**
     * An array containing all of the mob definitions.
     */
    private static MobDefinition[] mobDefinition;

    /**
     * The id of the mob.
     */
    private int id;

    /**
     * The name of the mob.
     */
    private String name;

    /**
     * The examine of the mob.
     */
    private String examine;

    /**
     * The combat level of the mob.
     */
    private int combat;

    /**
     * The mob size
     */
    private int size;

    /**
     * If the mob is attackable.
     */
    private boolean attackable;

    /**
     * If the mob is aggressive.
     */
    private boolean aggressive;

    /**
     * If the mob retreats.
     */
    private boolean retreats;

    /**
     * If the mob poisons.
     */
    private boolean poisonous;

    /**
     * Time it takes for this mob to respawn.
     */
    private int respawn;

    /**
     * The max hit of this mob.
     */
    private int maxHit;

    /**
     * The amount of hp this mob has.
     */
    private int hitpoints;

    /**
     * The attack speed of this mob.
     */
    private int attackSpeed;

    /**
     * The attack animation of this mob.
     */
    private int attackAnim;

    /**
     * The defence animation of this mob.
     */
    private int defenceAnim;

    /**
     * The death animation of this mob.
     */
    private int deathAnim;

    /**
     * This mobs attack bonus.
     */
    private int attackBonus;

    /**
     * This mobs melee resistance.
     */
    private int defenceMelee;

    /**
     * This mobs range resistance.
     */
    private int defenceRange;

    /**
     * This mobs defence resistance.
     */
    private int defenceMage;

    /**
     * Parse the mob definitions.
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
        setMobDefinition(new MobDefinition[6102]);

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(Misc.MOB_DEFINITIONS));
        final Gson builder = new GsonBuilder().create();
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            int index = reader.get("id").getAsInt();

            mobDefinition[index] = new MobDefinition();
            mobDefinition[index].setId(index);
            mobDefinition[index].setName(reader.get("name").getAsString());
            mobDefinition[index].setExamine(reader.get("examine").getAsString());
            mobDefinition[index].setCombatLevel(reader.get("combat").getAsInt());
            mobDefinition[index].setNpcSize(reader.get("size").getAsInt());
            mobDefinition[index].setAttackable(reader.get("attackable").getAsBoolean());
            mobDefinition[index].setAggressive(reader.get("aggressive").getAsBoolean());
            mobDefinition[index].setRetreats(reader.get("retreats").getAsBoolean());
            mobDefinition[index].setPoisonous(reader.get("poisonous").getAsBoolean());
            mobDefinition[index].setRespawnTime(reader.get("respawn").getAsInt());
            mobDefinition[index].setMaxHit(reader.get("maxHit").getAsInt());
            mobDefinition[index].setHitpoints(reader.get("hitpoints").getAsInt());
            mobDefinition[index].setAttackSpeed(reader.get("attackSpeed").getAsInt());
            mobDefinition[index].setAttackAnimation(reader.get("attackAnim").getAsInt());
            mobDefinition[index].setDefenceAnimation(reader.get("defenceAnim").getAsInt());
            mobDefinition[index].setDeathAnimation(reader.get("deathAnim").getAsInt());
            mobDefinition[index].setAttackBonus(reader.get("attackBonus").getAsInt());
            mobDefinition[index].setDefenceMelee(reader.get("defenceMelee").getAsInt());
            mobDefinition[index].setDefenceRange(reader.get("defenceRange").getAsInt());
            mobDefinition[index].setDefenceMage(reader.get("defenceMage").getAsInt());
            parsed++;
        }
    }

    /**
     * @return the mobDefinition.
     */
    public static MobDefinition[] getMobDefinition() {
        return mobDefinition;
    }

    /**
     * @param mobDefinition
     *            the mobDefinition to set.
     */
    public static void setMobDefinition(MobDefinition[] mobDefinition) {
        MobDefinition.mobDefinition = mobDefinition;
    }

    /**
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the examine.
     */
    public String getExamine() {
        return examine;
    }

    /**
     * @param examine
     *            the examine to set.
     */
    public void setExamine(String examine) {
        this.examine = examine;
    }

    /**
     * @return the combatLevel.
     */
    public int getCombatLevel() {
        return combat;
    }

    /**
     * @param combatLevel
     *            the combatLevel to set.
     */
    public void setCombatLevel(int combat) {
        this.combat = combat;
    }

    /**
     * @return the npcSize.
     */
    public int getNpcSize() {
        return size;
    }

    /**
     * @param npcSize
     *            the npcSize to set.
     */
    public void setNpcSize(int size) {
        this.size = size;
    }

    /**
     * @return the attackable.
     */
    public boolean isAttackable() {
        return attackable;
    }

    /**
     * @param attackable
     *            the attackable to set.
     */
    public void setAttackable(boolean attackable) {
        this.attackable = attackable;
    }

    /**
     * @return the aggressive.
     */
    public boolean isAggressive() {
        return aggressive;
    }

    /**
     * @param aggressive
     *            the aggressive to set.
     */
    public void setAggressive(boolean aggressive) {
        this.aggressive = aggressive;
    }

    /**
     * @return the retreats.
     */
    public boolean isRetreats() {
        return retreats;
    }

    /**
     * @param retreats
     *            the retreats to set.
     */
    public void setRetreats(boolean retreats) {
        this.retreats = retreats;
    }

    /**
     * @return the poisonous.
     */
    public boolean isPoisonous() {
        return poisonous;
    }

    /**
     * @param poisonous
     *            the poisonous to set.
     */
    public void setPoisonous(boolean poisonous) {
        this.poisonous = poisonous;
    }

    /**
     * @return the respawnTime.
     */
    public int getRespawnTime() {
        return respawn;
    }

    /**
     * @param respawnTime
     *            the respawnTime to set.
     */
    public void setRespawnTime(int respawn) {
        this.respawn = respawn;
    }

    /**
     * @return the maxHit.
     */
    public int getMaxHit() {
        return maxHit;
    }

    /**
     * @param maxHit
     *            the maxHit to set.
     */
    public void setMaxHit(int maxHit) {
        this.maxHit = maxHit;
    }

    /**
     * @return the hitpoints.
     */
    public int getHitpoints() {
        return hitpoints;
    }

    /**
     * @param hitpoints
     *            the hitpoints to set.
     */
    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }

    /**
     * @return the attackSpeed.
     */
    public int getAttackSpeed() {
        return attackSpeed;
    }

    /**
     * @param attackSpeed
     *            the attackSpeed to set.
     */
    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    /**
     * @return the attackAnimation.
     */
    public int getAttackAnimation() {
        return attackAnim;
    }

    /**
     * @param attackAnimation
     *            the attackAnimation to set.
     */
    public void setAttackAnimation(int attackAnim) {
        this.attackAnim = attackAnim;
    }

    /**
     * @return the defenceAnimation.
     */
    public int getDefenceAnimation() {
        return defenceAnim;
    }

    /**
     * @param defenceAnimation
     *            the defenceAnimation to set.
     */
    public void setDefenceAnimation(int defenceAnim) {
        this.defenceAnim = defenceAnim;
    }

    /**
     * @return the deathAnimation.
     */
    public int getDeathAnimation() {
        return deathAnim;
    }

    /**
     * @param deathAnimation
     *            the deathAnimation to set.
     */
    public void setDeathAnimation(int deathAnim) {
        this.deathAnim = deathAnim;
    }

    /**
     * @return the attackBonus.
     */
    public int getAttackBonus() {
        return attackBonus;
    }

    /**
     * @param attackBonus
     *            the attackBonus to set.
     */
    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    /**
     * @return the defenceMelee.
     */
    public int getDefenceMelee() {
        return defenceMelee;
    }

    /**
     * @param defenceMelee
     *            the defenceMelee to set.
     */
    public void setDefenceMelee(int defenceMelee) {
        this.defenceMelee = defenceMelee;
    }

    /**
     * @return the defenceRange.
     */
    public int getDefenceRange() {
        return defenceRange;
    }

    /**
     * @param defenceRange
     *            the defenceRange to set.
     */
    public void setDefenceRange(int defenceRange) {
        this.defenceRange = defenceRange;
    }

    /**
     * @return the defenceMage.
     */
    public int getDefenceMage() {
        return defenceMage;
    }

    /**
     * @param defenceMage
     *            the defenceMage to set.
     */
    public void setDefenceMage(int defenceMage) {
        this.defenceMage = defenceMage;
    }
}
