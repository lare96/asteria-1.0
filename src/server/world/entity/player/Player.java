package server.world.entity.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import server.Server;
import server.logic.GameLogic;
import server.logic.task.Task;
import server.logic.task.Task.Time;
import server.net.buffer.PacketBuffer;
import server.net.packet.ServerPacketBuilder;
import server.util.Misc;
import server.util.ServerGUI;
import server.util.Misc.Stopwatch;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Teleport;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.magic.TeleportSpell;
import server.world.entity.mob.Mob;
import server.world.entity.mob.MobDialogue;
import server.world.entity.player.container.BankContainer;
import server.world.entity.player.container.EquipmentContainer;
import server.world.entity.player.container.InventoryContainer;
import server.world.entity.player.content.PrivateMessage;
import server.world.entity.player.content.DynamicEnergyTask;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.content.Trade;
import server.world.entity.player.file.ReadPlayerFileEvent;
import server.world.entity.player.file.WritePlayerFileEvent;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameManager;
import server.world.entity.player.skill.SkillContainer;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.entity.player.skill.impl.Cooking.Cook;
import server.world.entity.player.skill.impl.Fishing.Fish;
import server.world.entity.player.skill.impl.Smithing.Smelt;
import server.world.item.Item;
import server.world.item.WorldItem;
import server.world.map.Location;
import server.world.map.Position;
import server.world.object.WorldObject;

/**
 * Represents a logged-in player that is able to receive and send packets and
 * interact with entities and world models.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Player extends Entity {

    /**
     * The network for this player.
     */
    private final PlayerNetwork network;

    /**
     * Animation played when this player dies.
     */
    private static final Animation DEATH = new Animation(0x900);

    /**
     * If this player is visible.
     */
    private boolean isVisible = true;

    /**
     * The food you are cooking.
     */
    private Cook cook;

    /**
     * The bar you are currently smelting.
     */
    private Smelt smelt;

    /**
     * The amount of bars you are currently smelting.
     */
    private int smeltAmount;

    /**
     * The shop you currently have open.
     */
    private int openShopId;

    /**
     * The amount of food you are cooking.
     */
    private int cookAmount;

    /**
     * Amount of logs the tree you're cutting holds.
     */
    private int woodcuttingLogAmount;

    /**
     * All possible fish you are able to catch.
     */
    private List<Fish> fish = new ArrayList<Fish>();

    /**
     * Skill flags.
     */
    private boolean[] skillingAction = new boolean[15];

    /**
     * Combat prayer flags.
     */
    private boolean[] prayer = new boolean[18];

    /**
     * If this player is banned.
     */
    private boolean isBanned;

    /**
     * The players skills.
     */
    private SkillContainer skills = new SkillContainer();

    /**
     * Options used for npc dialogues,
     */
    private int option;

    /**
     * The players head icon.
     */
    private int headIcon = -1;

    /**
     * The players skull icon.
     */
    private int skullIcon = -1;

    /**
     * The player's run energy.
     */
    private int runEnergy = 100;

    /**
     * The mob teleporting the player.
     */
    private Mob runecraftingMob;

    /**
     * Handles a trading session with another player.
     */
    private Trade trading = new Trade(this);

    /**
     * A collection of anti-massing timers.
     */
    private Stopwatch eatingTimer = new Stopwatch().reset(),
            buryTimer = new Stopwatch().reset(),
            altarTimer = new Stopwatch().reset(),
            mobTheftTimer = new Stopwatch().reset(),
            objectTheftTimer = new Stopwatch().reset();

    /**
     * The last delay when stealing.
     */
    private long lastTheftDelay;

    /**
     * The username.
     */
    private String username;

    /**
     * The password.
     */
    private String password;

    /**
     * If this player is new.
     */
    private boolean newPlayer = true;

    /**
     * Options for banking.
     */
    private boolean insertItem, withdrawAsNote;

    /**
     * The conversation id the character is in.
     */
    private int mobDialogue;

    /**
     * The stage in the conversation the player is in.
     */
    private int conversationStage;

    /**
     * If this is the first packet recieved. Used for global items.
     */
    private boolean firstPacket;

    /**
     * A list of local players.
     */
    private final List<Player> players = new LinkedList<Player>();

    /**
     * A list of local npcs.
     */
    private final List<Mob> npcs = new LinkedList<Mob>();

    /**
     * The players rights.
     */
    private int staffRights = 0;

    /**
     * The players current spellbook.
     */
    private Spellbook spellbook = Spellbook.NORMAL;

    /**
     * Variables for public chatting.
     */
    private int chatColor, chatEffects;

    /**
     * The chat message in bytes.
     */
    private byte[] chatText;

    /**
     * The gender.
     */
    private int gender = Misc.GENDER_MALE;

    /**
     * The appearance.
     */
    private int[] appearance = new int[7], colors = new int[5];

    /**
     * The player's bonuses.
     */
    private int[] playerBonus = new int[12];

    /**
     * The friends list.
     */
    private List<Long> friends = new ArrayList<Long>(200);

    /**
     * The ignores list.
     */
    private List<Long> ignores = new ArrayList<Long>(100);

    /**
     * Flag that determines if this player has entered an incorrect password.
     */
    private boolean incorrectPassword;

    /**
     * An instance of this player.
     */
    private Player player = this;

    /**
     * For player npcs (pnpc).
     */
    private int npcAppearanceId = -1;

    /**
     * The players inventory.
     */
    private InventoryContainer inventory = new InventoryContainer(this);

    /**
     * The players bank.
     */
    private BankContainer bank = new BankContainer(this);

    /**
     * The players equipment.
     */
    private EquipmentContainer equipment = new EquipmentContainer(this);

    /**
     * Private messaging for this player.
     */
    private PrivateMessage privateMessage = new PrivateMessage(this);

    /**
     * Field that determines if you are using a stove for cooking.
     */
    private boolean usingStove;

    /**
     * Creates a new Player.
     * 
     * @param network
     *            the network.
     */
    public Player(PlayerNetwork network) {
        this.network = network;

        /** Set the default appearance. */
        getAppearance()[Misc.APPEARANCE_SLOT_CHEST] = 18;
        getAppearance()[Misc.APPEARANCE_SLOT_ARMS] = 26;
        getAppearance()[Misc.APPEARANCE_SLOT_LEGS] = 36;
        getAppearance()[Misc.APPEARANCE_SLOT_HEAD] = 0;
        getAppearance()[Misc.APPEARANCE_SLOT_HANDS] = 33;
        getAppearance()[Misc.APPEARANCE_SLOT_FEET] = 42;
        getAppearance()[Misc.APPEARANCE_SLOT_BEARD] = 10;

        /** Set the default colors. */
        getColors()[0] = 7;
        getColors()[1] = 8;
        getColors()[2] = 9;
        getColors()[3] = 5;
        getColors()[4] = 0;
    }

    @Override
    public void engineWork() throws Exception {
        getMovementQueue().execute();
    }

    @Override
    public Task onDeath() throws Exception {
        return new Task(1, true, Time.TICK) {
            @Override
            public void logic() {
                if (getDeathTicks() == 0) {
                    getMovementQueue().reset();
                } else if (getDeathTicks() == 1) {
                    animation(DEATH);
                    TrainableSkill.check(player);
                    player.getTrading().resetTrade(false);
                    // duel, whatever
                    // send message to whoever killed this player and do
                    // whatever
                } else if (getDeathTicks() == 5) {
                    // decide what items/equipment to keep, drop all other
                    // items/equipment, take into account the protect item
                    // prayer,
                    // and
                    // if the player is skulled

                    // unskull the player, and stop combat

                    move(new Position(3093, 3244));
                } else if (getDeathTicks() == 6) {
                    getServerPacketBuilder().resetAnimation();
                    getServerPacketBuilder().sendMessage("Oh dear, you're dead!");
                    getServerPacketBuilder().walkableInterface(65535);
                    // Prayer.getSingleton().stopAllCombatPrayer(player);
                    heal(player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevelForExperience());

                    Minigame minigame = MinigameManager.inAnyMinigame(player);

                    if (minigame != null) {
                        minigame.onDeath(player);
                    }

                    setHasDied(false);
                    setDeathTicks(0);
                    this.cancel();
                    return;
                }

                incrementDeathTicks();
            }
        };
    }

    /**
     * The default teleport method that teleports the player to a position based
     * on the spellbook they have open.
     * 
     * @param position
     *            the position to teleport to.
     */
    public void teleport(final Position position) {
        teleport(new TeleportSpell() {
            @Override
            public Position teleportTo() {
                return position;
            }

            @Override
            public Teleport type() {
                return player.getSpellbook().getTeleport();
            }

            @Override
            public int baseExperience() {
                return 0;
            }

            @Override
            public Item[] itemsRequired() {
                return null;
            }

            @Override
            public int levelRequired() {
                return 1;
            }
        });
    }

    @Override
    public void teleport(TeleportSpell spell) {
        Minigame minigame = MinigameManager.inAnyMinigame(player);

        if (minigame != null) {
            if (!minigame.canTeleport()) {
                return;
            }
        }

        if (getTeleportStage() > 0) {
            return;
        }

        if (spell.itemsRequired() != null) {
            for (Item item : spell.itemsRequired()) {
                if (item == null) {
                    continue;
                }

                if (!getInventory().getItemContainer().contains(item)) {
                    getServerPacketBuilder().sendMessage("You need " + item.getAmount() + " " + item.getDefinition().getItemName() + " to teleport here!");
                    return;
                } else {
                    player.getInventory().removeItem(item);
                }
            }
        }

        if (!getSkills().getTrainable()[Skill.MAGIC.ordinal()].reqLevel(spell.levelRequired())) {
            getServerPacketBuilder().sendMessage("You need a magic level of " + spell.levelRequired() + " to teleport here!");
            return;
        }

        setTeleportStage(1);
        getServerPacketBuilder().closeWindows();
        SkillManager.getSingleton().addExperience(spell.baseExperience(), Skill.MAGIC, player);
        getTeleport().setAs(spell.teleportTo());
        spell.type().getAction().run(this);
    }

    @Override
    public void move(Position position) {
        getMovementQueue().reset();
        getServerPacketBuilder().closeWindows();
        getPosition().setAs(position);
        setResetMovementQueue(true);
        setNeedsPlacement(true);
        getServerPacketBuilder().sendMapRegion();

        if (position.getZ() != 0) {
            WorldItem.removeAllHeight(this);
            WorldObject.removeAllHeight(this);
        }
    }

    @Override
    public void register() {
        for (int i = 1; i < World.getPlayers().length; i++) {
            if (World.getPlayers()[i] == null) {
                World.getPlayers()[i] = this;
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

        World.getPlayers()[this.getSlot()] = null;
        this.setUnregistered(true);
    }

    @Override
    public String toString() {
        return getUsername() == null ? "Client(" + network.getHost() + ")" : "Player(" + getUsername() + ":" + getPassword() + " - " + network.getHost() + ")";
    }

    /**
     * Finishes the login for the player.
     */
    public void login() throws Exception {
        int response = Misc.LOGIN_RESPONSE_OK;

        /** Check if the player is already logged in. */
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }
            if (player.getUsername().equals(getUsername())) {
                response = Misc.LOGIN_RESPONSE_ACCOUNT_ONLINE;
            }
        }

        /** Load saved data. */
        if (response == 2) {
            ReadPlayerFileEvent read = new ReadPlayerFileEvent(this);
            read.run();
            response = read.getReturnCode();
        }

        if (isBanned()) {
            response = Misc.LOGIN_RESPONSE_ACCOUNT_DISABLED;
        }

        /** Load player rights and the client response code. */
        PacketBuffer.OutBuffer resp = PacketBuffer.newOutBuffer(3);
        resp.writeByte(response);

        if (getStaffRights() == 3) {
            resp.writeByte(2);
        } else {
            resp.writeByte(getStaffRights());
        }

        resp.writeByte(0);
        network.send(resp.getBuffer());

        if (response != 2) {
            network.disconnect();
            return;
        }

        /** Register this player for processing. */
        World.register(this);

        /** Update their appearance. */
        getServerPacketBuilder().sendMapRegion();
        this.getFlags().flag(Flag.APPEARANCE);

        /** Load sidebar interfaces. */
        getServerPacketBuilder().sendSidebarInterface(1, 3917);
        getServerPacketBuilder().sendSidebarInterface(2, 638);
        getServerPacketBuilder().sendSidebarInterface(3, 3213);
        getServerPacketBuilder().sendSidebarInterface(4, 1644);
        getServerPacketBuilder().sendSidebarInterface(5, 5608);
        getServerPacketBuilder().sendSidebarInterface(6, getSpellbook().getSidebarInterface());
        getServerPacketBuilder().sendSidebarInterface(8, 5065);
        getServerPacketBuilder().sendSidebarInterface(9, 5715);
        getServerPacketBuilder().sendSidebarInterface(10, 2449);
        getServerPacketBuilder().sendSidebarInterface(11, 4445);
        getServerPacketBuilder().sendSidebarInterface(12, 147);
        getServerPacketBuilder().sendSidebarInterface(13, 6299);
        getServerPacketBuilder().sendSidebarInterface(0, 2423);

        /** Teleport the player to the saved position. */
        if (Server.isInDeveloperMode()) {
            if (player.getUsername().equals("lare96")) {
                move(getPosition());
            } else {
                move(new Location(new Position(3002, 3225), new Position(3266, 3360)).randomPosition());
            }
        } else {
            move(getPosition());
        }

        /** Refresh skills. */
        SkillManager.getSingleton().refreshAll(player);

        /** Refresh equipment. */
        equipment.refresh();

        /** Refresh inventory. */
        inventory.sendInventoryOnLogin();

        /** Send the bonuses. */
        writeBonus();

        /** Send skills to the client. */
        for (int i = 0; i < getSkills().getTrainable().length; i++) {
            getServerPacketBuilder().sendSkill(i, getSkills().getTrainable()[i].getLevel(), getSkills().getTrainable()[i].getExperience());
        }

        /** Update private messages on login. */
        getPrivateMessage().sendPrivateMessageOnLogin();

        /** Update interface text. */
        loadText();

        /** Update context menus. */
        getServerPacketBuilder().sendPlayerMenu("Attack", 3);
        getServerPacketBuilder().sendPlayerMenu("Trade with", 4);
        getServerPacketBuilder().sendPlayerMenu("Follow", 5);

        /** Starter package and makeover mage. */
        if (isNewPlayer()) {
            // FIXME: Starter package & Makeover Mage.

            setNewPlayer(false);
        }

        /** Schedule a worker for run energy. */
        GameLogic.getSingleton().submit(new DynamicEnergyTask(player));

        /** Send welcome message. */
        if (Server.isInBetaMode()) {
            getServerPacketBuilder().sendMessage("Welcome to the " + Server.getSingleton() + " beta [version 0.0]!");
        } else {
            getServerPacketBuilder().sendMessage("Welcome to " + Server.getSingleton() + "!");
        }

        /** Update the gui. */
        ServerGUI.updatePlayers();

        /** Activates the minigame method if needed. */
        Minigame minigame = MinigameManager.inAnyMinigame(this);

        if (minigame != null) {
            minigame.login(this);
        }

        Server.print(this + " has logged in.");
    }

    /**
     * Logs the player out.
     */
    public void logout() throws Exception {
        WritePlayerFileEvent write = new WritePlayerFileEvent(this);
        write.run();

        World.unregister(this);

        if (!this.getNetwork().isPacketDisconnect()) {
            getServerPacketBuilder().sendLogout();
        }

        ServerGUI.updatePlayers();
        Server.print(this + " has logged out.");
    }

    /**
     * Heals this player.
     * 
     * @param amount
     *            the amount of heal this player by.
     */
    public void heal(int amount) {
        if (this.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevel() + amount >= this.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevelForExperience()) {
            this.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].setLevel(this.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevelForExperience());
        } else {
            this.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].increaseLevel(amount);
        }

        SkillManager.getSingleton().refresh(this, Skill.HITPOINTS);
    }

    /**
     * Starts a dialogue.
     * 
     * @param id
     *            the dialogue to start.
     */
    public void dialogue(int id) {
        if (MobDialogue.getDialogues().containsKey(id)) {
            this.setMobDialogue(id);
            MobDialogue.getDialogues().get(this.getMobDialogue()).dialogue(this);
        }
    }

    /**
     * Calculates and writes the players bonuses.
     */
    public void writeBonus() {
        for (int i = 0; i < playerBonus.length; i++) {
            playerBonus[i] = 0;
        }

        for (Item item : this.getEquipment().getItemContainer().toArray()) {
            if (item == null || item.getId() < 1 || item.getAmount() < 1) {
                continue;
            }

            for (int i = 0; i < playerBonus.length; i++) {
                playerBonus[i] += item.getDefinition().getBonus()[i];
            }
        }

        int offset = 0;
        String send = "";

        for (int i = 0; i < playerBonus.length; i++) {
            if (playerBonus[i] >= 0) {
                send = Misc.BONUS_NAMES[i] + ": +" + playerBonus[i];
            } else {
                send = Misc.BONUS_NAMES[i] + ": -" + Math.abs(playerBonus[i]);
            }

            if (i == 10) {
                offset = 1;
            }

            getServerPacketBuilder().sendString(send, (1675 + i + offset));
        }
    }

    /**
     * Loads interface text on login.
     */
    public void loadText() {

    }

    /**
     * Sets the username.
     * 
     * @param username
     *            the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Mob> getNpcs() {
        return npcs;
    }

    public void setNpcAppearanceId(int npcAppearanceId) {
        this.npcAppearanceId = npcAppearanceId;
    }

    public int getNpcAppearanceId() {
        return npcAppearanceId;
    }

    public InventoryContainer getInventory() {
        return inventory;
    }

    public BankContainer getBank() {
        return bank;
    }

    public EquipmentContainer getEquipment() {
        return equipment;
    }

    /**
     * @return the newPlayer
     */
    public boolean isNewPlayer() {
        return newPlayer;
    }

    /**
     * @param newPlayer
     *            the newPlayer to set
     */
    public void setNewPlayer(boolean newPlayer) {
        this.newPlayer = newPlayer;
    }

    /**
     * @return the withdrawAsNote
     */
    public boolean isWithdrawAsNote() {
        return withdrawAsNote;
    }

    /**
     * @param withdrawAsNote
     *            the withdrawAsNote to set
     */
    public void setWithdrawAsNote(boolean withdrawAsNote) {
        this.withdrawAsNote = withdrawAsNote;
    }

    /**
     * @return the insertItem
     */
    public boolean isInsertItem() {
        return insertItem;
    }

    /**
     * @param insertItem
     *            the insertItem to set
     */
    public void setInsertItem(boolean insertItem) {
        this.insertItem = insertItem;
    }

    /**
     * @return the incorrectPassword
     */
    public boolean isIncorrectPassword() {
        return incorrectPassword;
    }

    /**
     * @param incorrectPassword
     *            the incorrectPassword to set
     */
    public void setIncorrectPassword(boolean incorrectPassword) {
        this.incorrectPassword = incorrectPassword;
    }

    public SkillContainer getSkills() {
        return skills;
    }

    /**
     * @return the firstPacket
     */
    public boolean isFirstPacket() {
        return firstPacket;
    }

    /**
     * @param firstPacket
     *            the firstPacket to set
     */
    public void setFirstPacket(boolean firstPacket) {
        this.firstPacket = firstPacket;
    }

    /**
     * @return the privateMessage
     */
    public PrivateMessage getPrivateMessage() {
        return privateMessage;
    }

    /**
     * @return the friends
     */
    public List<Long> getFriends() {
        return friends;
    }

    /**
     * @param friends
     *            the friends to set
     */
    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }

    /**
     * @return the ignores
     */
    public List<Long> getIgnores() {
        return ignores;
    }

    /**
     * @param ignores
     *            the ignores to set
     */
    public void setIgnores(List<Long> ignores) {
        this.ignores = ignores;
    }

    /**
     * @return the skillingAction
     */
    public boolean[] getSkillingAction() {
        return skillingAction;
    }

    /**
     * @return the usingStove
     */
    public boolean isUsingStove() {
        return usingStove;
    }

    /**
     * @param usingStove
     *            the usingStove to set
     */
    public void setUsingStove(boolean usingStove) {
        this.usingStove = usingStove;
    }

    /**
     * @return the cook
     */
    public Cook getCook() {
        return cook;
    }

    /**
     * @param cook
     *            the cook to set
     */
    public void setCook(Cook cook) {
        this.cook = cook;
    }

    /**
     * @return the trading
     */
    public Trade getTrading() {
        return trading;
    }

    /**
     * @return the runEnergy
     */
    public int getRunEnergy() {
        return runEnergy;
    }

    /**
     * @param runEnergy
     *            the runEnergy to set
     */
    public void decrementRunEnergy() {
        this.runEnergy -= 1;
        getServerPacketBuilder().sendString(getRunEnergy() + "%", 149);
    }

    /**
     * @param runEnergy
     *            the runEnergy to set
     */
    public void incrementRunEnergy() {
        this.runEnergy += 1;
        getServerPacketBuilder().sendString(getRunEnergy() + "%", 149);
    }

    /**
     * @param runEnergy
     *            the runEnergy to set
     */
    public void setRunEnergy(int runEnergy) {
        this.runEnergy = runEnergy;
    }

    /**
     * @return the eatingTimer
     */
    public Stopwatch getEatingTimer() {
        return eatingTimer;
    }

    /**
     * @param eatingTimer
     *            the eatingTimer to set
     */
    public void setEatingTimer(Stopwatch eatingTimer) {
        this.eatingTimer = eatingTimer;
    }

    /**
     * @return the fish
     */
    public List<Fish> getFish() {
        return fish;
    }

    /**
     * @param fish
     *            the fish to set
     */
    public void setFish(List<Fish> fish) {
        this.fish = fish;
    }

    public ServerPacketBuilder getServerPacketBuilder() {
        return network.getServerPacketBuilder();
    }

    /**
     * @return the mobDialogue
     */
    public int getMobDialogue() {
        return mobDialogue;
    }

    /**
     * @param mobDialogue
     *            the mobDialogue to set
     */
    public void setMobDialogue(int mobDialogue) {
        this.mobDialogue = mobDialogue;
    }

    /**
     * @return the buryTimer
     */
    public Stopwatch getBuryTimer() {
        return buryTimer;
    }

    /**
     * @return the option
     */
    public int getOption() {
        return option;
    }

    /**
     * @param option
     *            the option to set
     */
    public void setOption(int option) {
        this.option = option;
    }

    /**
     * @return the runecraftingMob
     */
    public Mob getRunecraftingMob() {
        return runecraftingMob;
    }

    /**
     * @param runecraftingMob
     *            the runecraftingMob to set
     */
    public void setRunecraftingMob(Mob runecraftingMob) {
        this.runecraftingMob = runecraftingMob;
    }

    /**
     * @return the cookAmount
     */
    public int getCookAmount() {
        return cookAmount;
    }

    /**
     * @param cookAmount
     *            the cookAmount to set
     */
    public void setCookAmount(int cookAmount) {
        this.cookAmount = cookAmount;
    }

    public void addCookAmount() {
        cookAmount++;
    }

    /**
     * @return the prayer
     */
    public boolean[] getPrayer() {
        return prayer;
    }

    /**
     * @param prayer
     *            the prayer to set
     */
    public void setPrayer(boolean[] prayer) {
        this.prayer = prayer;
    }

    /**
     * @return the headIcon
     */
    public int getHeadIcon() {
        return headIcon;
    }

    /**
     * @param headIcon
     *            the headIcon to set
     */
    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    /**
     * @return the openShopId
     */
    public int getOpenShopId() {
        return openShopId;
    }

    /**
     * @param openShopId
     *            the openShopId to set
     */
    public void setOpenShopId(int openShopId) {
        this.openShopId = openShopId;
    }

    /**
     * @return the skullIcon
     */
    public int getSkullIcon() {
        return skullIcon;
    }

    /**
     * @param skullIcon
     *            the skullIcon to set
     */
    public void setSkullIcon(int skullIcon) {
        this.skullIcon = skullIcon;
    }

    /**
     * @return the altarTimer
     */
    public Stopwatch getAltarTimer() {
        return altarTimer;
    }

    /**
     * @return the spellbook
     */
    public Spellbook getSpellbook() {
        return spellbook;
    }

    /**
     * @param spellbook
     *            the spellbook to set
     */
    public void setSpellbook(Spellbook spellbook) {
        this.spellbook = spellbook;
    }

    /**
     * @return the woodcuttingLogAmount
     */
    public int getWoodcuttingLogAmount() {
        return woodcuttingLogAmount;
    }

    /**
     * @param woodcuttingLogAmount
     *            the woodcuttingLogAmount to set
     */
    public void setWoodcuttingLogAmount(int woodcuttingLogAmount) {
        this.woodcuttingLogAmount = woodcuttingLogAmount;
    }

    public void decrementWoodcuttingLogAmount() {
        this.woodcuttingLogAmount--;
    }

    /**
     * @return the smelt
     */
    public Smelt getSmelt() {
        return smelt;
    }

    /**
     * @param smelt
     *            the smelt to set
     */
    public void setSmelt(Smelt smelt) {
        this.smelt = smelt;
    }

    /**
     * @return the smeltAmount
     */
    public int getSmeltAmount() {
        return smeltAmount;
    }

    /**
     * @param smeltAmount
     *            the smeltAmount to set
     */
    public void setSmeltAmount(int smeltAmount) {
        this.smeltAmount = smeltAmount;
    }

    public void addSmeltAmount() {
        smeltAmount++;
    }

    /**
     * @return the playerBonus
     */
    public int[] getPlayerBonus() {
        return playerBonus;
    }

    /**
     * @param playerBonus
     *            the playerBonus to set
     */
    public void setPlayerBonus(int[] playerBonus) {
        this.playerBonus = playerBonus;
    }

    /**
     * @return the conversationStage
     */
    public int getConversationStage() {
        return conversationStage;
    }

    /**
     * @param conversationStage
     *            the conversationStage to set
     */
    public void setConversationStage(int conversationStage) {
        this.conversationStage = conversationStage;
    }

    public PlayerNetwork getNetwork() {
        return network;
    }

    public void setStaffRights(int staffRights) {
        this.staffRights = staffRights;
    }

    public int getStaffRights() {
        return staffRights;
    }

    public void setChatColor(int chatColor) {
        this.chatColor = chatColor;
    }

    public int getChatColor() {
        return chatColor;
    }

    public void setChatEffects(int chatEffects) {
        this.chatEffects = chatEffects;
    }

    public int getChatEffects() {
        return chatEffects;
    }

    public void setChatText(byte[] chatText) {
        this.chatText = chatText;
    }

    public byte[] getChatText() {
        return chatText;
    }

    public int[] getAppearance() {
        return appearance;
    }

    public void setAppearance(int[] appearance) {
        this.appearance = appearance;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

    /**
     * @param skills
     *            the skills to set
     */
    public void setSkills(SkillContainer skills) {
        this.skills = skills;
    }

    /**
     * @return the isVisible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * @param isVisible
     *            the isVisible to set
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * @return the lastTheftDelay
     */
    public long getLastTheftDelay() {
        return lastTheftDelay;
    }

    /**
     * @param lastTheftDelay
     *            the lastTheftDelay to set
     */
    public void setLastTheftDelay(long lastTheftDelay) {
        this.lastTheftDelay = lastTheftDelay;
    }

    /**
     * @return the theftTimer
     */
    public Stopwatch getMobTheftTimer() {
        return mobTheftTimer;
    }

    /**
     * @return the isBanned
     */
    public boolean isBanned() {
        return isBanned;
    }

    /**
     * @param isBanned
     *            the isBanned to set
     */
    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    /**
     * @return the objectTheftTimer
     */
    public Stopwatch getObjectTheftTimer() {
        return objectTheftTimer;
    }
}
