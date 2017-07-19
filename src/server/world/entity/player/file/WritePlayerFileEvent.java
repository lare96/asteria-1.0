package server.world.entity.player.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import server.Server;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerFileEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * A writing implementation of a player file operation.
 * 
 * @author lare96
 */
public class WritePlayerFileEvent extends PlayerFileEvent {

    /**
     * Create a new class used for writing to character files.
     * 
     * @param player
     *            the player taking part in this operation.
     */
    public WritePlayerFileEvent(Player player) {
        super(player);

        if (!file().exists()) {
            try {
                file().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (file().exists() && !this.getPlayer().isIncorrectPassword()) {
            try {
                final Gson builder = new GsonBuilder().setPrettyPrinting().create();
                final JsonObject object = new JsonObject();

                object.addProperty("username", this.getPlayer().getUsername().trim());
                object.addProperty("password", this.getPlayer().getPassword().trim());
                object.addProperty("x", new Integer(this.getPlayer().getPosition().getX()));
                object.addProperty("y", new Integer(this.getPlayer().getPosition().getY()));
                object.addProperty("z", new Integer(this.getPlayer().getPosition().getZ()));
                object.addProperty("staff-rights", new Integer(this.getPlayer().getStaffRights()));
                object.addProperty("gender", new Integer(this.getPlayer().getGender()));
                object.add("appearance", builder.toJsonTree(getPlayer().getAppearance()));
                object.add("colors", builder.toJsonTree(getPlayer().getColors()));
                object.addProperty("run-toggled", new Boolean(this.getPlayer().getMovementQueue().isRunToggled()));
                object.addProperty("new-player", new Boolean(this.getPlayer().isNewPlayer()));
                object.add("inventory", builder.toJsonTree(getPlayer().getInventory().getItemContainer().toArray()));
                object.add("bank", builder.toJsonTree(getPlayer().getBank().getContainer().toArray()));
                object.add("equipment", builder.toJsonTree(getPlayer().getEquipment().getItemContainer().toArray()));
                object.add("skills", builder.toJsonTree(getPlayer().getSkills().getTrainable()));
                object.add("friends", builder.toJsonTree(getPlayer().getFriends().toArray()));
                object.add("ignores", builder.toJsonTree(getPlayer().getIgnores().toArray()));
                object.addProperty("run-energy", new Integer(this.getPlayer().getRunEnergy()));
                object.addProperty("spell-book", this.getPlayer().getSpellbook().name());
                object.addProperty("is-banned", new Boolean(this.getPlayer().isBanned()));
                object.addProperty("auto-retaliate", new Boolean(this.getPlayer().isAutoRetaliate()));

                FileWriter fileWriter = new FileWriter(file());
                fileWriter.write(builder.toJson(object));
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
                Server.print("Error while writing data for " + this.getPlayer());
            }
        }
    }

    @Override
    public File file() {
        return new File("./data/players/" + getPlayer().getUsername() + ".json");
    }
}
