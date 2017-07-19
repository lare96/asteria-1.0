package server.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import server.world.entity.player.skill.impl.Fishing.Fish;
import server.world.item.Item;
import server.world.map.Position;

/**
 * A collection of miscellaneous utility methods and constants.
 * 
 * @author blakeman8192
 * @author lare96
 */
public final class Misc {

    /**
     * An instance of the random class for arithmetic operations.
     */
    private static Random random = new Random();

    /**
     * Difference in X coordinates for directions array.
     */
    public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

    /**
     * Difference in Y coordinates for directions array.
     */
    public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };

    /** The login response codes. */
    public static final int LOGIN_RESPONSE_OK = 2,
            LOGIN_RESPONSE_INVALID_CREDENTIALS = 3,
            LOGIN_RESPONSE_ACCOUNT_DISABLED = 4,
            LOGIN_RESPONSE_ACCOUNT_ONLINE = 5, LOGIN_RESPONSE_UPDATED = 6,
            LOGIN_RESPONSE_WORLD_FULL = 7,
            LOGIN_RESPONSE_LOGIN_SERVER_OFFLINE = 8,
            LOGIN_RESPONSE_LOGIN_LIMIT_EXCEEDED = 9,
            LOGIN_RESPONSE_BAD_SESSION_ID = 10,
            LOGIN_RESPONSE_PLEASE_TRY_AGAIN = 11,
            LOGIN_RESPONSE_NEED_MEMBERS = 12,
            LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN = 13,
            LOGIN_RESPONSE_SERVER_BEING_UPDATED = 14,
            LOGIN_RESPONSE_LOGIN_ATTEMPTS_EXCEEDED = 16,
            LOGIN_RESPONSE_MEMBERS_ONLY_AREA = 17;

    /** The equipment slots. */
    public static final int EQUIPMENT_SLOT_HEAD = 0, EQUIPMENT_SLOT_CAPE = 1,
            EQUIPMENT_SLOT_AMULET = 2, EQUIPMENT_SLOT_WEAPON = 3,
            EQUIPMENT_SLOT_CHEST = 4, EQUIPMENT_SLOT_SHIELD = 5,
            EQUIPMENT_SLOT_LEGS = 7, EQUIPMENT_SLOT_HANDS = 9,
            EQUIPMENT_SLOT_FEET = 10, EQUIPMENT_SLOT_RING = 12,
            EQUIPMENT_SLOT_ARROWS = 13;

    /** The appearance slots. */
    public static final int APPEARANCE_SLOT_CHEST = 0,
            APPEARANCE_SLOT_ARMS = 1, APPEARANCE_SLOT_LEGS = 2,
            APPEARANCE_SLOT_HEAD = 3, APPEARANCE_SLOT_HANDS = 4,
            APPEARANCE_SLOT_FEET = 5, APPEARANCE_SLOT_BEARD = 6;

    /** The gender id's. */
    public static final int GENDER_MALE = 0, GENDER_FEMALE = 1;

    /**
     * Items that are not allowed to be traded.
     */
    public static final int[] ITEM_UNTRADEABLE = {};

    /**
     * Items that are not allowed to be in a shop.
     */
    public static final int[] NO_SHOP_ITEMS = { 995 };

    /**
     * Items that are platebodies.
     */
    private static boolean[] isPlatebody = new boolean[7956];

    /**
     * Items that are full helms.
     */
    private static boolean[] isFullHelm = new boolean[7956];

    /**
     * Items that are twohanded.
     */
    private static boolean[] is2H = new boolean[7956];

    /** A collection of files that will be loaded on startup. */
    public static final File WORLD_OBJECTS = new File("./data/json/objects/world_objects.json"),
            WORLD_MOBS = new File("./data/json/mobs/world_mobs.json"),
            WORLD_ITEMS = new File("./data/json/items/world_items.json"),
            WORLD_SHOPS = new File("./data/json/shops/world_shops.json"),
            ITEM_DEFINITIONS = new File("./data/json/items/item_definitions.json"),
            MOB_DEFINITIONS = new File("./data/json/mobs/mob_definitions.json"),
            COMBAT_SPELLS = new File("./data/json/magic/world_combat_spells.json"),
            NORMAL_SPELLS = new File("./data/json/magic/world_standard_spells.json");

    /** The bonus names. */
    public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range", "Strength", "Prayer" };

    /** The character table. */
    private static char xlateTable[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']' };

    /** The decode buffer. */
    private static char decodeBuf[] = new char[4096];

    /** A table of valid characters. */
    public static final char VALID_CHARS[] = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"', '[', ']', '|', '?', '/', '`' };

    /**
     * Picks a random element from out of an Item[] array.
     * 
     * @param array
     *            the Item[] array to pick the element from.
     * @return the element chosen.
     */
    public static Item randomElement(Item[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    /**
     * Picks a random element from out of an String[] array.
     * 
     * @param array
     *            the String[] array to pick the element from.
     * @return the element chosen.
     */
    public static String randomElement(String[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    /**
     * Picks a random element from out of a list with Fish as its generic type.
     * 
     * @param list
     *            the list to pick the element from.
     * @return the element chosen.
     */
    public static Fish randomElement(List<Fish> list) {
        return list.get((int) (Math.random() * list.size()));
    }

    /**
     * Executes a method from the specified class by its name, assuming it has
     * no parameters.
     * 
     * @param methodName
     *            the method to execute.
     * @param classWithMethod
     *            the class with the method you want to execute.
     */
    public void classMethod(String methodName, Class<?> classWithMethod) {
        for (Method m : classWithMethod.getMethods()) {
            if (m == null || !Modifier.isStatic(m.getModifiers())) {
                continue;
            }

            if (m.getName().equals(methodName)) {
                try {
                    m.invoke(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Formats the price for easier viewing.
     * 
     * @param price
     *            the price to format.
     * @return the newly formatted price.
     */
    public static String formatPrice(int price) {
        if (price >= 1000 && price < 1000000) {
            return " (" + (price / 1000) + "K)";
        } else if (price >= 1000000) {
            return " (" + (price / 1000000) + " million)";
        }

        return "";
    }

    /**
     * Converts a long to a string. Used for private messaging.
     * 
     * @param l
     *            the long.
     * @return the string.
     */
    public static String longToName(long l) {
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    /** Loads platebodies on startup. */
    public static void loadPlatebody() {
        Scanner s;

        try {
            s = new Scanner(new File("./data/platebody.txt"));

            while (s.hasNextLine()) {
                isPlatebody[s.nextInt()] = true;
            }

            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Loads full helms on startup. */
    public static void loadFullHelm() {
        Scanner s;

        try {
            s = new Scanner(new File("./data/full_helm.txt"));

            while (s.hasNextLine()) {
                isFullHelm[s.nextInt()] = true;
            }

            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Loads two handed weapons on startup. */
    public static void loadTwoHanded() {
        Scanner s;

        try {
            s = new Scanner(new File("./data/2h_weapons.txt"));

            while (s.hasNextLine()) {
                is2H[s.nextInt()] = true;
            }

            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * A table of constants that hold a value representing their rarity.
     */
    public enum Rarity {
        ALWAYS(100),

        VERY_COMMON(90),

        COMMON(75),

        SOMETIMES(50),

        UNCOMMON(35),

        VERY_UNCOMMON(10),

        EXTREMELY_RARE(5),

        ALMOST_IMPOSSIBLE(1);

        /**
         * The rarity percentage out of 100.
         */
        private int percentage;

        /**
         * Creates a new constant.
         * 
         * @param percentage
         *            the rarity percentage out of 100.
         */
        Rarity(int percentage) {
            this.setPercentage(percentage);
        }

        /**
         * @return the percentage
         */
        public int getPercentage() {
            return percentage;
        }

        /**
         * @param percentage
         *            the percentage to set
         */
        public void setPercentage(int percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * Converts an array of bytes to an integer.
     * 
     * @param data
     *            the array of bytes.
     * @return the newly constructed integer.
     */
    public static int hexToInt(byte[] data) {
        int value = 0;
        int n = 1000;
        for (int i = 0; i < data.length; i++) {
            int num = (data[i] & 0xFF) * n;
            value += (int) num;
            if (n > 1) {
                n = n / 1000;
            }
        }
        return value;
    }

    /**
     * Unpacks text from an array of bytes.
     * 
     * @param packedData
     *            the array of bytes.
     * @param size
     *            the size of the array of bytes.
     * @return the unpacked string.
     */
    public static String textUnpack(byte packedData[], int size) {
        int idx = 0, highNibble = -1;
        for (int i = 0; i < size * 2; i++) {
            int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
            if (highNibble == -1) {
                if (val < 13)
                    decodeBuf[idx++] = xlateTable[val];
                else
                    highNibble = val;
            } else {
                decodeBuf[idx++] = xlateTable[((highNibble << 4) + val) - 195];
                highNibble = -1;
            }
        }

        return new String(decodeBuf, 0, idx);
    }

    /**
     * Reads a string from an i/o stream.
     * 
     * @param input
     *            the i/o stream.
     * @return the string read.
     */
    public static String readInputString(DataInputStream input) {
        byte data;
        StringBuilder builder = new StringBuilder();
        try {
            while ((data = input.readByte()) != 0) {
                builder.append((char) data);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * Formats a string.
     * 
     * @param string
     *            the string formatted.
     * @return the newly formatted string.
     */
    public static String formatInputString(String string) {
        String result = "";
        for (String part : string.toLowerCase().split(" ")) {
            result += part.substring(0, 1).toUpperCase() + part.substring(1) + " ";
        }
        return result.trim();
    }

    /**
     * Converts a string to a long value.
     * 
     * @param s
     *            the string.
     * @return the long value.
     */
    public static long nameToLong(String s) {
        long l = 0L;
        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z')
                l += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                l += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                l += (27 + c) - 48;
        }
        while (l % 37L == 0L && l != 0L)
            l /= 37L;
        return l;
    }

    /**
     * Returns the delta coordinates. Note that the returned Position is not an
     * actual position, instead it's values represent the delta values between
     * the two arguments.
     * 
     * @param a
     *            the first position.
     * @param b
     *            the second position.
     * @return the delta coordinates contained within a position.
     */
    public static Position delta(Position a, Position b) {
        return new Position(b.getX() - a.getX(), b.getY() - a.getY());
    }

    /**
     * Calculates the direction between the two coordinates.
     * 
     * @param dx
     *            the first coordinate.
     * @param dy
     *            the second coordinate.
     * @return the direction.
     */
    public static int direction(int dx, int dy) {
        if (dx < 0) {
            if (dy < 0) {
                return 5;
            } else if (dy > 0) {
                return 0;
            } else {
                return 3;
            }
        } else if (dx > 0) {
            if (dy < 0) {
                return 7;
            } else if (dy > 0) {
                return 2;
            } else {
                return 4;
            }
        } else {
            if (dy < 0) {
                return 6;
            } else if (dy > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /** Lengths for the various packets. */
    public static final int packetLengths[] = { //
    0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
    0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
    0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
    0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
    2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
    0, 0, 0, 12, 0, 0, 0, 0, 8, 0, // 50
    0, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
    6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
    0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
    0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
    0, 13, 0, -1, 0, 0, 0, 0, 0, 0,// 100
    0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
    1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
    0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
    0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
    0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
    0, 0, 0, 0, -1, -1, 0, 0, 0, 0,// 160
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
    0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
    0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
    2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
    4, 0, 0, 0, 7, 8, 0, 0, 10, 0, // 210
    0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
    1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
    0, 4, 0, 0, 0, 0, -1, 0, -1, 4,// 240
    0, 0, 6, 6, 0, 0, 0 // 250
    };

    /**
     * A simple timing utility.
     * 
     * @author blakeman8192
     * @author lare96
     */
    public static class Stopwatch {

        /** The cached time. */
        private long time = System.currentTimeMillis();

        /**
         * Resets this stopwatch.
         * 
         * @return this stopwatch.
         */
        public Stopwatch reset() {
            time = System.currentTimeMillis();
            return this;
        }

        /**
         * Returns the amount of time elapsed (in milliseconds) since this
         * object was initialized, or since the last call to the "reset()"
         * method.
         * 
         * @return the elapsed time (in milliseconds).
         */
        public long elapsed() {
            return System.currentTimeMillis() - time;
        }
    }

    /**
     * @return the isPlatebody.
     */
    public static boolean[] getIsPlatebody() {
        return isPlatebody;
    }

    /**
     * @return the isFullHelm.
     */
    public static boolean[] getIsFullHelm() {
        return isFullHelm;
    }

    /**
     * @return the is2H.
     */
    public static boolean[] getIs2H() {
        return is2H;
    }

    /**
     * @return the random.
     */
    public static Random getRandom() {
        return random;
    }

    // /** used to convert definintions, here for future reference
    // * to json.
    // */
    // public static void to() throws IOException {
    // if (!Server.MOB_DEFINITIONS2.exists()) {
    // Server.MOB_DEFINITIONS2.createNewFile();
    // }
    //
    // BufferedWriter writer = new BufferedWriter(new
    // FileWriter(Server.MOB_DEFINITIONS2));
    //
    // writer.write("[");
    // writer.newLine();
    //
    // for (MobDefinition m : MobDefinition.getMobDefinition()) {
    // if (m == null) {
    // continue;
    // }
    //
    // writer.write("{");
    // writer.newLine();
    // writer.write("`id`: " + m.getId() + ",");
    // writer.newLine();
    // writer.write("`name`: `" + m.getName() + "`,");
    // writer.newLine();
    // writer.write("`examine`: `" + m.getExamine() + "`,");
    // writer.newLine();
    // writer.write("`combat`: " + m.getCombatLevel() + ",");
    // writer.newLine();
    // writer.write("`size`: " + m.getNpcSize() + ",");
    // writer.newLine();
    // writer.write("`attackable`: " + m.isAttackable() + ",");
    // writer.newLine();
    // writer.write("`aggressive`: " + m.isAggressive() + ",");
    // writer.newLine();
    // writer.write("`retreats`: " + m.isRetreats() + ",");
    // writer.newLine();
    // writer.write("`poisonous`: " + m.isPoisonous() + ",");
    // writer.newLine();
    // writer.write("`respawn`: " + m.getRespawnTime() + ",");
    // writer.newLine();
    // writer.write("`maxHit`: " + m.getMaxHit() + ",");
    // writer.newLine();
    // writer.write("`hitpoints`: " + m.getHitpoints() + ",");
    // writer.newLine();
    // writer.write("`attackSpeed`: " + m.getAttackSpeed() + ",");
    // writer.newLine();
    // writer.write("`attackAnim`: " + m.getAttackAnimation() + ",");
    // writer.newLine();
    // writer.write("`defenceAnim`: " + m.getDefenceAnimation() + ",");
    // writer.newLine();
    // writer.write("`deathAnim`: " + m.getDeathAnimation() + ",");
    // writer.newLine();
    // writer.write("`attackBonus`: " + m.getAttackBonus() + ",");
    // writer.newLine();
    // writer.write("`defenceMelee`: " + m.getDefenceMelee() + ",");
    // writer.newLine();
    // writer.write("`defenceRange`: " + m.getDefenceRange() + ",");
    // writer.newLine();
    // writer.write("`defenceMage`: " + m.getDefenceMage() + "");
    // writer.newLine();
    // writer.write("},");
    // writer.newLine();
    // }
    //
    // writer.write("]");
    // writer.close();
    // }
}
