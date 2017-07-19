package server.world.entity.mob.dialogue;

import server.world.entity.mob.MobDialogue;
import server.world.entity.player.Player;

/**
 * A conversation between the player and wizard frumscone.
 * 
 * @author lare96
 */
public class RunecraftingDialogue extends MobDialogue {

    @Override
    public void dialogue(Player player) {
        switch (player.getConversationStage()) {
            case 0:
                MobDialogue.oneLineMobDialogue(player, Expression.HAPPY, "Hi " + player.getUsername() + "! What can I help you with?", 460);
                this.next(player);
                break;
            case 1:
                MobDialogue.twoOptions(player, "I would like to mine essence.", "I would like to craft runes.");
                player.setOption(1);
                this.stop(player);
                break;
        }
    }

    @Override
    protected int dialogueId() {
        return 2;
    }
}
