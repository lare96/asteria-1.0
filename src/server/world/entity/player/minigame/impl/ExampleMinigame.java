package server.world.entity.player.minigame.impl;

import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.map.Location;

public class ExampleMinigame extends Minigame {

    // XXX: There are compulsory implementations you must make...
    @Override
    public void login(Player player) {
        // on login while in the minigame
        player.getServerPacketBuilder().sendMessage("You have logged out while in this minigame. Thanks for returning!");
        // list.addToList(player);
        // ...
    }

    @Override
    public void logout(Player player) {
        // on logout while in the minigame
        // list.removeFromList(player);
        // ...
    }

    @Override
    public Location[] minigameLocation() {
        // minigame locations that the above will take affect
        return null;
    }

    // XXX: And then, you can also make your own implementations of certain
    // rules in the parent class for this minigame. The example below will make
    // it so you are unable to fight while in the area of this minigame.
    @Override
    public boolean canFight() {
        return false;
    }

    // XXX: Lets say we don't want them teleporting or eating/drinking either...
    @Override
    public boolean canTeleport() {
        return false;
    }

    @Override
    public boolean canEat() {
        return false;
    }

    // XXX: We can also make certain things happen on death. :)
    @Override
    public void onDeath(Player player) {
        player.getServerPacketBuilder().sendMessage("Holy shit, you just died dude...");
        // list.removeFromList(player);
        // teleportBackIntoMinigame(player);
        // ...
    }

    // XXX: And much more! This will be improved on greatly in the future.
}
