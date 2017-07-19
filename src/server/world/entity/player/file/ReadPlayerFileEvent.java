package server.world.entity.player.file;

import java.io.File;
import java.io.FileReader;

import server.Server;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerFileEvent;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.Trainable;
import server.world.item.Item;
import server.world.map.Position;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A reading implementation of a player file operation.
 * 
 * @author lare96
 */
public class ReadPlayerFileEvent extends PlayerFileEvent {

    /**
     * The return code to be sent to the client.
     */
    private int returnCode = Misc.LOGIN_RESPONSE_OK;

    /**
     * Create a new class used for reading character files.
     * 
     * @param player
     *            the players character file to read.
     */
    public ReadPlayerFileEvent(Player player) {
        super(player);

        if (!file().exists()) {
            SkillManager.getSingleton().login(player);
            Server.print(player + " is logging in for the first time!");
            this.setReturnCode(Misc.LOGIN_RESPONSE_OK);
        }
    }

    @Override
    public void run() {
        if (file().exists()) {
            try {
                final JsonParser fileParser = new JsonParser();
                final Gson builder = new GsonBuilder().create();
                final Object object = fileParser.parse(new FileReader(file()));
                final JsonObject reader = (JsonObject) object;

                final String username = reader.get("username").getAsString();
                final String password = reader.get("password").getAsString();
                final Position position = new Position(reader.get("x").getAsInt(), reader.get("y").getAsInt(), reader.get("z").getAsInt());
                final int staffRights = reader.get("staff-rights").getAsInt();
                final int gender = reader.get("gender").getAsInt();
                final int[] appearance = builder.fromJson(reader.get("appearance").getAsJsonArray(), int[].class);
                final int[] colors = builder.fromJson(reader.get("colors").getAsJsonArray(), int[].class);
                final boolean runToggled = reader.get("run-toggled").getAsBoolean();
                final boolean newPlayer = reader.get("new-player").getAsBoolean();
                final Item[] inventory = builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class);
                final Item[] bank = builder.fromJson(reader.get("bank").getAsJsonArray(), Item[].class);
                final Item[] equipment = builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class);
                final Trainable[] skills = builder.fromJson(reader.get("skills").getAsJsonArray(), Trainable[].class);
                final Long[] friends = builder.fromJson(reader.get("friends").getAsJsonArray(), Long[].class);
                final Long[] ignores = builder.fromJson(reader.get("ignores").getAsJsonArray(), Long[].class);
                final int runEnergy = reader.get("run-energy").getAsInt();
                final Spellbook book = Spellbook.valueOf(reader.get("spell-book").getAsString());
                final boolean banned = reader.get("is-banned").getAsBoolean();
                final boolean retaliate = reader.get("auto-retaliate").getAsBoolean();

                this.getPlayer().setUsername(username);

                if (!this.getPlayer().getPassword().equals(password)) {
                    this.getPlayer().setIncorrectPassword(true);
                    this.setReturnCode(Misc.LOGIN_RESPONSE_INVALID_CREDENTIALS);
                    return;
                }

                this.getPlayer().setPassword(password);
                this.getPlayer().getPosition().setAs(position);
                this.getPlayer().setStaffRights(staffRights);
                this.getPlayer().setGender(gender);
                this.getPlayer().setAppearance(appearance);
                this.getPlayer().setColors(colors);
                this.getPlayer().getMovementQueue().setRunToggled(runToggled);
                this.getPlayer().setNewPlayer(newPlayer);
                this.getPlayer().getInventory().getItemContainer().setItems(inventory);
                this.getPlayer().getBank().getContainer().setItems(bank);
                this.getPlayer().getEquipment().getItemContainer().setItems(equipment);
                this.getPlayer().getSkills().setTrainable(skills);
                this.getPlayer().setRunEnergy(runEnergy);
                this.getPlayer().setBanned(banned);
                this.getPlayer().setAutoRetaliate(retaliate);

                if (book == null) {
                    this.getPlayer().setSpellbook(Spellbook.NORMAL);
                } else {
                    this.getPlayer().setSpellbook(book);
                }

                for (Long l : friends) {
                    this.getPlayer().getFriends().add(l);
                }

                for (Long l : ignores) {
                    this.getPlayer().getIgnores().add(l);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Server.print("Error while reading data for " + this.getPlayer());
                this.setReturnCode(Misc.LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN);
            }
        }
    }

    @Override
    public File file() {
        return new File("./data/players/" + getPlayer().getUsername() + ".json");
    }

    /**
     * @return the returnCode.
     */
    public int getReturnCode() {
        return returnCode;
    }

    /**
     * @param returnCode
     *            the returnCode to set.
     */
    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
