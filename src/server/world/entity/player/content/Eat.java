package server.world.entity.player.content;

import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameManager;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.item.Item;

/**
 * Holds data for all eatable foods and a static method that allows for a
 * designated player to eat a certain type of food.
 */
public class Eat {

    /**
     * The delay between eating food in milliseconds.
     */
    private static final int EATING_DELAY = 1800;

    /**
     * The animation played when consuming food.
     */
    private static final Animation CONSUME = new Animation(829);

    /**
     * Hold data for each of the eatable foods.
     */
    public enum Data {
        ANCHOVIES(319, 3, true),

        SWORDFISH(373, 14, true),

        COD(339, 7, true),

        BREAD(2309, 2, true),

        PIKE(351, 8, true),

        SHRIMPS(315, 3, true),

        LOBSTER(379, 12, true),

        CHOCOLATE_CAKE(1901, 5, true),

        MACKEREL(355, 6, true),

        BASS(365, 13, true),

        SHARK(385, 20, true),

        TROUT(333, 7, true),

        CAKE(1891, 4, true),

        MANTA_RAY(391, 22, true),

        TWO_THIRDS_OF_CAKE(1893, 4, true),

        SLICE_OF_CAKE(1895, 4, false),

        TUNA(361, 10, true),

        SALMON(329, 9, true);

        /**
         * Item id of the food.
         */
        private int itemId;

        /**
         * How much hp the food heals.
         */
        private int heal;

        /**
         * If 'the' should come before the name of the food (for grammar).
         */
        private boolean the;

        /**
         * Construct new data.
         * 
         * @param itemId
         *            the item id of the food.
         * @param heal
         *            the amount it heals.
         * @param the
         *            if 'the' should come before the name of the food.
         */
        Data(int itemId, int heal, boolean the) {
            this.setItemId(itemId);
            this.setHeal(heal);
            this.setThe(the);
        }

        /**
         * @return the itemId.
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * @param itemId
         *            the itemId to set.
         */
        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        /**
         * @return the heal amount.
         */
        public int getHeal() {
            return heal;
        }

        /**
         * @param heal
         *            the heal amount to set.
         */
        public void setHeal(int heal) {
            this.heal = heal;
        }

        /**
         * @return the the.
         */
        public boolean isThe() {
            return the;
        }

        /**
         * @param the
         *            the the to set.
         */
        public void setThe(boolean the) {
            this.the = the;
        }
    }

    /**
     * Static method that allows for a player to eat food.
     * 
     * @param player
     *            the player eating the food.
     * @param data
     *            the food.
     * @param slot
     *            the slot the food is in.
     */
    public static void eat(final Player player, final Data data, final int slot) {
        Minigame minigame = MinigameManager.inAnyMinigame(player);

        if (minigame != null) {
            if (!minigame.canEat()) {
                return;
            }
        }

        if (player.getEatingTimer().elapsed() > EATING_DELAY) {
            if (player.getInventory().getItemContainer().contains(data.getItemId())) {
                if (player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevelForExperience() > player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevel()) {
                    player.animation(CONSUME);
                    player.getInventory().removeItemSlot(new Item(data.getItemId()), slot);
                    player.getServerPacketBuilder().sendMessage(data.isThe() ? "You eat the " + data.name().toLowerCase().replaceAll("_", " ") + " and it restores some health." : "You eat a " + data.name().toLowerCase().replaceAll("_", " ") + " and it restores some health.");
                } else {
                    player.getServerPacketBuilder().sendMessage(data.isThe() ? "You eat the " + data.name().toLowerCase().replaceAll("_", " ") + "." : "You eat a " + data.name().toLowerCase().replaceAll("_", " ") + ".");
                    player.animation(CONSUME);
                    player.getInventory().removeItemSlot(new Item(data.getItemId()), slot);
                }

                switch (data.getItemId()) {
                    case 1891:
                        player.getInventory().addItemToSlot(new Item(1893), slot);
                        break;
                    case 1893:
                        player.getInventory().addItemToSlot(new Item(1895), slot);
                        break;
                }

                player.heal(data.getHeal());
                player.getEatingTimer().reset();
            }
        }
    }
}
