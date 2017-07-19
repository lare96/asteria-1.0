package server.net.packet;

import server.net.buffer.PacketBuffer;
import server.net.buffer.PacketBuffer.AccessType;
import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.ValueType;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.WorldItem;
import server.world.map.MapRegion;
import server.world.map.MapRegionTile;
import server.world.map.Position;
import server.world.object.WorldObject;

/**
 * A collection of packets sent by the server that will be read by the client.
 * 
 * @author lare96
 */
public final class ServerPacketBuilder {

    /**
     * The player sending these packets.
     */
    private final Player player;

    /**
     * Construct a new packet builder.
     * 
     * @param player
     *            the player sending these packets.
     */
    public ServerPacketBuilder(Player player) {
        this.player = player;
    }

    /**
     * Plays an interface animation.
     * 
     * @param interfaceId
     *            the interface to play the animation on.
     * @param animation
     *            the animation to play.
     * @return this packet builder.
     */
    public ServerPacketBuilder interfaceAnimation(int interfaceId, int animation) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 200);
        out.writeShort(interfaceId);
        out.writeShort(animation);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends items to the selected slot on the interface.
     * 
     * @param frame
     *            the frame to display the items on.
     * @param item
     *            the item to display on the interface.
     * @param slot
     *            the slot to display the items on.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendItemOnInterfaceSlot(int frame, Item item, int slot) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(32);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 34);
        out.writeShort(frame);
        out.writeByte(slot);
        out.writeShort(item.getId() + 1);
        if (item.getAmount() > 254) {
            out.writeByte(255);
            out.writeShort(item.getAmount());
        } else {
            out.writeByte(item.getAmount());
        }
        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the head model of a mob to an interface.
     * 
     * @param id
     *            the id of the head model.
     * @param size
     *            the size of the head model.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendMobHeadModel(int id, int size) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 75);
        out.writeShort(id, ValueType.A, ByteOrder.LITTLE);
        out.writeShort(size, ValueType.A, ByteOrder.LITTLE);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends a custom map region.
     * 
     * @param region
     *            the map region to send.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendCustomMapRegion(MapRegion region) {
        this.sendMapRegion();

        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(50);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 241);
        out.writeShort(player.getPosition().getRegionY() + 6, ValueType.A);
        out.setAccessType(AccessType.BIT_ACCESS);
        for (int z = 0; z < MapRegion.SIZE_LENGTH_Z; z++) {
            for (int x = 0; x < MapRegion.SIZE_LENGTH_X; x++) {
                for (int y = 0; y < MapRegion.SIZE_LENGTH_Y; y++) {
                    MapRegionTile tile = region.getTile(x, y, z);

                    out.writeBit(tile != null);

                    if (tile != null) {
                        out.writeBits(26, tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1);
                    }
                }
            }
        }
        out.setAccessType(AccessType.BYTE_ACCESS);
        out.writeShort(player.getPosition().getRegionX() + 6);
        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the head model of a player to an interface.
     * 
     * @param i
     *            the size of the head model.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendPlayerHeadModel(int size) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 185);
        out.writeShort(size, ValueType.A, ByteOrder.LITTLE);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Flashes the selected sidebar.
     * 
     * @param id
     *            the id of the sidebar to flash.
     * @return this packet builder.
     */
    public ServerPacketBuilder flashSelectedSidebar(int id) {
        // XXX: does not work, you have to fix the packet client sided.

        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2);
        out.writeHeader(player.getNetwork().getEncryptor(), 24);
        out.writeByte(id, ValueType.A);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the enter name interface.
     * 
     * @return this packet builder.
     */
    public ServerPacketBuilder enterName() {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(1);
        out.writeHeader(player.getNetwork().getEncryptor(), 187);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Changes the state of the minimap.
     * 
     * @param state
     *            the new state of the minimap.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendMapState(int state) {
        // States:
        // 0 - Active: Clickable and viewable
        // 1 - Locked: viewable but not clickable
        // 2 - Blacked-out: Minimap is replaced with black background
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2);
        out.writeHeader(player.getNetwork().getEncryptor(), 99);
        out.writeByte(state);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Resets the cameras rotation.
     * 
     * @return this packet builder.
     */
    public ServerPacketBuilder sendResetCameraRotation() {
        // XXX: disconnects the player when used?

        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(1);
        out.writeHeader(player.getNetwork().getEncryptor(), 108);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Spins the camera.
     * 
     * @param x
     *            the x coordinate within the loaded map.
     * @param y
     *            the y coordinate within the loaded map.
     * @param height
     *            the height of the camera.
     * @param speed
     *            the speed of the camera.
     * @param angle
     *            the angle of the camera.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendCameraSpin(int x, int y, int height, int speed, int angle) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(7);
        out.writeHeader(player.getNetwork().getEncryptor(), 177);
        out.writeByte(x / 64);
        out.writeByte(y / 64);
        out.writeShort(height);
        out.writeByte(speed);
        out.writeByte(angle);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Moves the camera.
     * 
     * @param x
     *            the x coordinate within the loaded map.
     * @param y
     *            the y coordinate within the loaded map.
     * @param height
     *            the height of the camera.
     * @param speed
     *            the speed of the camera.
     * @param angle
     *            the angle of the camera.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendCameraMovement(int x, int y, int height, int speed, int angle) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(7);
        out.writeHeader(player.getNetwork().getEncryptor(), 166);
        out.writeByte(x / 64);
        out.writeByte(y / 64);
        out.writeShort(height);
        out.writeByte(speed);
        out.writeByte(angle);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Shakes the screen.
     * 
     * @param intensity
     *            the intensity of the shake.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendScreenShake(int intensity) {
        if (intensity > 4) {
            throw new IllegalArgumentException("Intensity must be below 5!");
        }

        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 35);
        out.writeByte(intensity);
        out.writeByte(intensity);
        out.writeByte(intensity);
        out.writeByte(intensity);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Resets the position of the camera.
     * 
     * @return this packet builder.
     */
    public ServerPacketBuilder sendResetCamera() {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(7);
        out.writeHeader(player.getNetwork().getEncryptor(), 107);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Plays music from the cache.
     * 
     * @param id
     *            the id of the music to play.
     * @return this packet builder.
     */
    public ServerPacketBuilder playMusic(int id) {
        // XXX: does not work, you have to fix the packet client sided.

        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 74);
        out.writeShort(id, ByteOrder.LITTLE);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the system update time.
     * 
     * @param time
     *            the amount of time to send.
     * @return this packet builder.
     */
    public ServerPacketBuilder systemUpdate(int time) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 114);
        out.writeShort(time, ByteOrder.LITTLE);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Changes the color on an interface.
     * 
     * @param interfaceId
     *            the interface.
     * @param color
     *            the new color.
     * @return this packer builder.
     */
    public ServerPacketBuilder changeColorOnInterface(int interfaceId, int color) {
        // XXX: afaik, doesn't work but I have no clue what this packet is for
        // and I might have been trying to use it wrong.

        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 122);
        out.writeShort(interfaceId, ValueType.A, ByteOrder.LITTLE);
        out.writeShort(color, ValueType.A, ByteOrder.LITTLE);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends an item to an interface.
     * 
     * @param id
     *            the id of the item.
     * @param zoom
     *            the zoom of the item.
     * @param model
     *            the model of the item.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendItemOnInterface(int id, int zoom, int model) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(7);
        out.writeHeader(player.getNetwork().getEncryptor(), 246);
        out.writeShort(id, PacketBuffer.ByteOrder.LITTLE);
        out.writeShort(zoom);
        out.writeShort(model);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Creates a projectile for the specified player.
     * 
     * @param position
     *            the position of the projectile.
     * @param offset
     *            the offset position of the projectile.
     * @param angle
     *            the angle of the projectile.
     * @param speed
     *            the speed of the projectile.
     * @param gfxMoving
     *            the rate that projectile gfx moves in.
     * @param startHeight
     *            the starting height of the projectile.
     * @param endHeight
     *            the ending height of the projectile.
     * @param lockon
     *            the lockon value of this projectile.
     * @param time
     *            the time it takes for this projectile to hit its desired
     *            position.
     * @return this packet builder.
     */
    public ServerPacketBuilder createProjectile(Position position, Position offset, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time) {
        this.sendCoordinates(position);
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(16);
        out.writeHeader(player.getNetwork().getEncryptor(), 117);
        out.writeByte(angle);
        out.writeByte(offset.getY());
        out.writeByte(offset.getX());
        out.writeShort(lockon);
        out.writeShort(gfxMoving);
        out.writeByte(startHeight);
        out.writeByte(endHeight);
        out.writeShort(time);
        out.writeShort(speed);
        out.writeByte(16);
        out.writeByte(64);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends a client config.
     * 
     * @param id
     *            the id of the config.
     * @param state
     *            the state to put this config in.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendConfig(int id, int state) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(4);
        out.writeHeader(player.getNetwork().getEncryptor(), 36);
        out.writeShort(id, ByteOrder.LITTLE);
        out.writeByte(state);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the image of an object to the world.
     * 
     * @param object
     *            the object to send.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendObject(WorldObject object) {
        sendCoordinates(object.getPosition());
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 151);
        out.writeByte(0, ValueType.S);
        out.writeShort(object.getId(), ByteOrder.LITTLE);
        out.writeByte((object.getType() << 2) + (object.getFace().getFaceId() & 3), ValueType.S);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Removes the image of an object from the world.
     * 
     * @param object
     *            the object to remove.
     * @return this packet builder.
     */
    public ServerPacketBuilder removeObject(WorldObject object) {
        sendCoordinates(object.getPosition());
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 101);
        out.writeByte((object.getType() << 2) + (object.getFace().getFaceId() & 3), ValueType.C);
        out.writeByte(0);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the players skills to the client.
     * 
     * @param skillID
     *            the id of the skill being sent.
     * @param level
     *            the level of the skill being sent.
     * @param exp
     *            the experience of the skill being sent.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendSkill(int skillID, int level, int exp) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(8);
        out.writeHeader(player.getNetwork().getEncryptor(), 134);
        out.writeByte(skillID);
        out.writeInt(exp, ByteOrder.MIDDLE);
        out.writeByte(level);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Closes any interfaces this player has open.
     * 
     * @return this packet builder.
     */
    public ServerPacketBuilder closeWindows() {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(1);
        out.writeHeader(player.getNetwork().getEncryptor(), 219);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the list of people you have on your friends and ignores list.
     * 
     * @param i
     *            the world you're in? Not completely sure what this is.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendPrivateMessagingList(int i) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2);
        out.writeHeader(player.getNetwork().getEncryptor(), 221);
        out.writeByte(i);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the chat options.
     * 
     * @param publicChat
     *            the public chat option.
     * @param privateChat
     *            the private chat option.
     * @param tradeBlock
     *            the trade/challenge option.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendChatOptions(int publicChat, int privateChat, int tradeBlock) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(4);
        out.writeHeader(player.getNetwork().getEncryptor(), 206);
        out.writeByte(publicChat);
        out.writeByte(privateChat);
        out.writeByte(tradeBlock);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Loads a player in your friends list.
     * 
     * @param playerName
     *            the player's name.
     * @param world
     *            the world they are on.
     * @return this packet builder.
     */
    public ServerPacketBuilder loadPrivateMessage(long playerName, int world) {
        if (world != 0) {
            world += 9;
        }

        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(10);
        out.writeHeader(player.getNetwork().getEncryptor(), 50);
        out.writeLong(playerName);
        out.writeByte(world);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends a hint arrow on the specified coordinates.
     * 
     * @param coordinates
     *            the coordinates to send the arrow on.
     * @param position
     *            the position of the arrow on the coordinates.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendPositionHintArrow(Position coordinates, int position) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(7);
        out.writeHeader(player.getNetwork().getEncryptor(), 254);
        out.writeByte(position);
        out.writeShort(coordinates.getX());
        out.writeShort(coordinates.getY());
        out.writeByte(coordinates.getZ());
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Send a private message to another player.
     * 
     * @param name
     *            the name of the player you are sending the message to.
     * @param rights
     *            your player rights.
     * @param chatMessage
     *            the message.
     * @param messageSize
     *            the message size.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendPrivateMessage(long name, int rights, byte[] chatMessage, int messageSize) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(messageSize + 15);
        out.writeVariablePacketHeader(player.getNetwork().getEncryptor(), 196);
        out.writeLong(name);
        out.writeInt(player.getPrivateMessage().getLastPrivateMessageId());
        out.writeByte(rights);
        out.writeBytes(chatMessage, messageSize);
        out.finishVariablePacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends a hint arrow on an entity.
     * 
     * @param type
     *            the type of entity.
     * @param id
     *            the id of the entity.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendEntityHintArrow(int type, int id) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 254);
        out.writeByte(type);
        out.writeShort(id);
        out.writeByte(0);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the players current coordinates to the client.
     * 
     * @param position
     *            the coordinates.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendCoordinates(Position position) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 85);
        out.writeByte(position.getY() - (player.getCurrentRegion().getRegionY() * 8), ValueType.C);
        out.writeByte(position.getX() - (player.getCurrentRegion().getRegionX() * 8), ValueType.C);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Opens a walkable interface for this player.
     * 
     * @param id
     *            the walkable interface to open.
     * @return this packet builder.
     */
    public ServerPacketBuilder walkableInterface(int id) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 208);
        out.writeShort(id, ByteOrder.LITTLE);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the image of a ground item to the world.
     * 
     * @param item
     *            the item to send.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendGroundItem(WorldItem item) {
        sendCoordinates(item.getPosition());
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(6);
        out.writeHeader(player.getNetwork().getEncryptor(), 44);
        out.writeShort(item.getItem().getId(), ValueType.A, ByteOrder.LITTLE);
        out.writeShort(item.getItem().getAmount());
        out.writeByte(0);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Removes the image of a ground item from the world.
     * 
     * @param item
     *            the item to remove.
     * @return this packet builder.
     */
    public ServerPacketBuilder removeGroundItem(WorldItem item) {
        sendCoordinates(item.getPosition());
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(4);
        out.writeHeader(player.getNetwork().getEncryptor(), 156);
        out.writeByte(0, ValueType.S);
        out.writeShort(item.getItem().getId());
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends player context menus.
     * 
     * @param option
     *            the option.
     * @param slot
     *            the slot for the option to be placed in.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendPlayerMenu(String option, int slot) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(option.length() + 6);
        out.writeVariablePacketHeader(player.getNetwork().getEncryptor(), 104);
        out.writeByte(slot, PacketBuffer.ValueType.C);
        out.writeByte(0, PacketBuffer.ValueType.A);
        out.writeString(option);
        out.finishVariablePacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends a string to an interface.
     * 
     * @param text
     *            the string to send.
     * @param id
     *            where the string should be sent.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendString(String text, int id) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(text.length() + 6);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 126);
        out.writeString(text);
        out.writeShort(id, ValueType.A);
        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the equipment you current have on to the client.
     * 
     * @param slot
     *            the equipment slot.
     * @param itemID
     *            the item id.
     * @param itemAmount
     *            the item amount.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendEquipment(int slot, int itemID, int itemAmount) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(32);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 34);
        out.writeShort(1688);
        out.writeByte(slot);
        out.writeShort(itemID + 1);
        if (itemAmount > 254) {
            out.writeByte(255);
            out.writeShort(itemAmount);
        } else {
            out.writeByte(itemAmount);
        }
        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Updates an array of items on an interface.
     * 
     * @param interfaceId
     *            the interface to send the items on.
     * @param items
     *            the items to send.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendUpdateItems(int interfaceId, Item[] items) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2048);
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 53);
        out.writeShort(interfaceId);
        if (items == null) {
            out.writeShort(0);
            out.writeByte(0);
            out.writeShort(0, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
            out.finishVariableShortPacketHeader();
            player.getNetwork().send(out.getBuffer());
            return this;
        }
        out.writeShort(items.length);
        for (Item item : items) {
            if (item != null) {
                if (item.getAmount() > 254) {
                    out.writeByte(255);
                    out.writeInt(item.getAmount(), PacketBuffer.ByteOrder.INVERSE_MIDDLE);
                } else {
                    out.writeByte(item.getAmount());
                }
                out.writeShort(item.getId() + 1, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
            } else {
                out.writeByte(0);
                out.writeShort(0, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
            }
        }
        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends an interface to your inventory.
     * 
     * @param interfaceId
     *            the interface to send.
     * @param inventoryId
     *            the inventory to send on.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendInventoryInterface(int interfaceId, int inventoryId) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 248);
        out.writeShort(interfaceId, PacketBuffer.ValueType.A);
        out.writeShort(inventoryId);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Opens an interface for this player.
     * 
     * @param interfaceId
     *            the interface to open for this player.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendInterface(int interfaceId) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 97);
        out.writeShort(interfaceId);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends the player a message to the chatbox.
     * 
     * @param message
     *            the message to send.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendMessage(String message) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(message.length() + 3);
        out.writeVariablePacketHeader(player.getNetwork().getEncryptor(), 253);
        out.writeString(message);
        out.finishVariablePacketHeader();
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends a sidebar interface.
     * 
     * @param menuId
     *            the sidebar to send the interface on.
     * @param form
     *            the interface to send on the sidebar.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendSidebarInterface(int menuId, int form) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(4);
        out.writeHeader(player.getNetwork().getEncryptor(), 71);
        out.writeShort(form);
        out.writeByte(menuId, PacketBuffer.ValueType.A);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Refreshes the map region.
     * 
     * @return this packet builder.
     */
    public ServerPacketBuilder sendMapRegion() {
        player.getCurrentRegion().setAs(player.getPosition());
        player.setNeedsPlacement(true);
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(5);
        out.writeHeader(player.getNetwork().getEncryptor(), 73);
        out.writeShort(player.getPosition().getRegionX() + 6, PacketBuffer.ValueType.A);
        out.writeShort(player.getPosition().getRegionY() + 6);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Disconnects the player.
     * 
     * @return this packet builder.
     */
    public ServerPacketBuilder sendLogout() {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(1);
        out.writeHeader(player.getNetwork().getEncryptor(), 109);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Sends an interface to your chatbox.
     * 
     * @param frame
     *            the interface to send to the chatbox.
     * @return this packet builder.
     */
    public ServerPacketBuilder sendChatInterface(int frame) {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(3);
        out.writeHeader(player.getNetwork().getEncryptor(), 164);
        out.writeShort(frame, ByteOrder.LITTLE);
        player.getNetwork().send(out.getBuffer());
        return this;
    }

    /**
     * Resets this players animation.
     * 
     * @return this packet builder.
     */
    public ServerPacketBuilder resetAnimation() {
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(1);
        out.writeHeader(player.getNetwork().getEncryptor(), 1);
        player.getNetwork().send(out.getBuffer());
        return this;
    }
}
