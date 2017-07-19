package server.world.entity.player.skill.impl;

import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;

@SuppressWarnings("unused")
public class Fletching extends TrainableSkill {

    // FIXME: Finish this, I ripped data from another tutorial because I was way
    // too lazy to get it myself.

    /**
     * The id of the knife used to fletch logs.
     */

    private static final int KNIFE_ID = 946;

    /**
     * The animation played when fletching bows and arrows.
     */
    private static final Animation FLETCHING_ANIMATION = new Animation(6702);

    /**
     * 
     * @author lare96
     */
    private enum Bow {

    }

    /**
     * 
     * @author lare96
     */
    private enum Arrow {

    }

    // public enum LogData {
    //
    // NORMAL_LOG(1511, 6702, new int[] { 5, 10, 9, 1 }, new double[] { 5, 10,
    // 6, 5 }, new int[] { 50, 48, 9440, 52 }),
    // OAK_LOG(1521, 6702, new int[] { 20, 25, 24, -1 }, new double[] { 16.5,
    // 25, 16, -1 }, new int[] { 54, 56, 9442, -1 }),
    // WILLOW_LOG(1519, 6702, new int[] { 35, 40, 39, -1 }, new double[] {
    // 33.25, 41.5, 22, -1 }, new int[] { 60, 58, 9444, -1 }),
    // MAPLE_LOG(1517, 6702, new int[] { 50, 55, 54, -1 }, new double[] { 50,
    // 58.2, 32, -1 }, new int[] { 64, 62, 9448, -1 }),
    // YEW_LOG(1515, 6702, new int[] { 65, 70, 69, -1 }, new double[] { 67.5,
    // 75, 50, -1 }, new int[] { 68, 66, 9452, -1 }),
    // MAGIC_LOG(1513, 6702, new int[] { 80, 85, -1, -1 }, new double[] {
    // 83.2, 91.5, -1, -1 }, new int[] { 72, 70, -1, -1 });
    //
    // private int logId, animId;
    // private double[] exp;
    // private int[] levelReq, item;
    //
    // static Map<Integer, LogData> logData = new HashMap<Integer, LogData>();
    //
    // static {
    // for (LogData l : values())
    // logData.put(l.getLogId(), l);
    // }
    //
    // public static LogData forLog(int itemId) {
    // return logData.get(itemId);
    // }
    //
    // private LogData(int logId, int animId, int[] levelReq, double[] exp,
    // int[] item) {
    // this.logId = logId;
    // this.animId = animId;
    // this.levelReq = levelReq;
    // this.exp = exp;
    // this.item = item;
    // }
    //
    // public int getLogId() {
    // return logId;
    // }
    //
    // public int getAnimId() {
    // return animId;
    // }
    //
    // public int[] getLevelReq() {
    // return levelReq;
    // }
    //
    // public double[] getExp() {
    // return exp;
    // }
    //
    // public int[] getItem() {
    // return item;
    // }
    // }
    //
    //
    // public enum BowData {
    // LOGSHORTBOW(5, 6678, 48, 1777, 841, 5), LOGLONGBOW(10, 6684, 50, 1777,
    // 839, 5),
    // OAKSHORTBOW(20, 6679, 54, 1777, 843, 16.5), OAKLONGBOW(25, 6685, 56,
    // 1777, 845, 25),
    // COMPOGREBOW(30, 6685, 4825, 7830, 4827, 45),
    // WILLOWSHORTBOW(35, 6680, 60, 1777, 849, 32.25), WILLOWLONGBOW(40, 6686,
    // 58,
    // 1777,
    // 847,
    // 41.5),
    // MAPLESHORTBOW(50, 6681, 64, 1777, 853, 50), MAPLELONGBOW(55, 6687, 62,
    // 1777, 851,
    // 58.2),
    // YEWSHORTBOW(65, 6682, 68, 1777, 857, 67.5), YEWLONGBOW(70, 6688, 66,
    // 1777, 855, 75),
    // MAGICSHORTBOW(80, 6683, 72, 1777, 861, 83.2), MAGICLONGBOW(85, 6689,
    // 70, 1777,
    // 859, 91.5);
    //
    // public int levelReq, animId, uBowId, bowId, stringId;
    // public double Exp;
    //
    // static Map<Integer, BowData> bowData = new HashMap<Integer, BowData>();
    //
    // static {
    // for (BowData a : values())
    // bowData.put(a.getUBowId(), a);
    // }
    //
    // public static BowData forBow(int itemId) {
    // return bowData.get(itemId);
    // }
    //
    // private BowData(int levelReq, int animId, int uBowId, int stringId,
    // int bowId, double Exp) {
    // this.levelReq = levelReq;
    // this.animId = animId;
    // this.uBowId = uBowId;
    // this.stringId = stringId;
    // this.bowId = bowId;
    // this.Exp = Exp;
    // }
    //
    // public int getLevelReq() {
    // return levelReq;
    // }
    //
    // public int getAnimId() {
    // return animId;
    // }
    //
    // public int getUBowId() {
    // return uBowId;
    // }
    //
    // public int getStringId() {
    // return stringId;
    // }
    //
    // public int getBowId() {
    // return bowId;
    // }
    //
    // public int getItemReq() {
    // return bowId;
    // }
    //
    // public double getExp() {
    // return Exp;
    // }
    // }
    //
    // public enum ArrowData {
    // HEADLESS_ARROW(1, 52, 1, 53), BRONZE_ARROW(1, 39, 2.6, 882),
    // IRON_ARROW(15, 40, 3.8, 884), STEEL_ARROW(30, 41, 6.3, 886),
    // MITHIRIL_ARROW(45, 42, 8.8, 888), ADAMANT_ARROW(60, 43, 10, 890),
    // RUNE_ARROW(75, 44, 13.8, 892), DRAGON_ARROW(90, 11237, 16.3, 11212);
    //
    // private int levelReq, arrowHeadId, arrowId;
    // private double expReceived;
    //
    // static Map<Integer, ArrowData> arrowData = new HashMap<Integer,
    // ArrowData>();
    //
    // static {
    // for (ArrowData a : values())
    // arrowData.put(a.getHeadId(), a);
    // }
    //
    // private ArrowData(int levelReq, int arrowHeadId, double expReceived,
    // int arrowId) {
    // this.levelReq = levelReq;
    // this.arrowHeadId = arrowHeadId;
    // this.expReceived = expReceived;
    // this.arrowId = arrowId;
    // }
    //
    // public static ArrowData forArrow(int itemId) {
    // return arrowData.get(itemId);
    // }
    //
    // public int getLevelReq() {
    // return levelReq;
    // }
    //
    // public int getHeadId() {
    // return arrowHeadId;
    // }
    //
    // public double getExpReceived() {
    // return expReceived;
    // }
    //
    // public int getArrowId() {
    // return arrowId;
    // }
    //
    // }
    //
    // }

    @Override
    public void reset(Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public int index() {
        return SkillManager.FLETCHING;
    }

    @Override
    public Skill skill() {
        return Skill.FLETCHING;
    }
}
