package server.world.entity.mob.dialogue;

import server.util.Misc;
import server.world.entity.mob.MobDialogue;
import server.world.entity.player.Player;

/**
 * Represents the dialogue that a typical man will hold with a player on the
 * server.
 * 
 * @author lare96
 */
public class ManDialogue extends MobDialogue {

    @Override
    public void dialogue(Player player) {
        switch (player.getConversationStage()) {
            case 0:
                switch (Misc.getRandom().nextInt(1)) {
                    case 0:
                        MobDialogue.oneLineMobDialogue(player, Expression.DEFAULT, "Hello " + player.getUsername() + ", wonderful day we're having!", 1);
                        break;
                    case 1:
                        MobDialogue.oneLineMobDialogue(player, Expression.HAPPY, "All of the trees and the birds make me so happy!", 1);
                        break;
                }

                this.next(player);
                break;
            case 1:
                MobDialogue.oneLinePlayerDialogue(player, Expression.HAPPY, "I completely agree with you! G'day!");
                this.stop(player);
                break;
        }
    }

    @Override
    protected int dialogueId() {
        return 1;
    }
}
