package server.world.entity.player;

import java.util.Iterator;

import server.logic.GameLogic;
import server.net.buffer.PacketBuffer;
import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.ValueType;
import server.util.Misc;
import server.world.World;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.Skill;
import server.world.map.Position;

/**
 * Provides static utility methods for updating Players.
 * 
 * @author blakeman8192
 * @author lare96
 */
public final class PlayerUpdate {

    /**
     * Updates the player.
     * 
     * @param player
     *            the player to update.
     */
    public static void update(Player player) {
        // XXX: The buffer sizes may need to be tuned.
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(16384); // 8192
        PacketBuffer.OutBuffer block = PacketBuffer.newOutBuffer(8192); // 4096

        /** Initialize the update packet. */
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 81);
        out.setAccessType(PacketBuffer.AccessType.BIT_ACCESS);

        /** Update this player. */
        PlayerUpdate.updateLocalPlayerMovement(player, out);

        if (player.getFlags().isUpdateRequired()) {
            PlayerUpdate.updateState(player, block, false, true);
        }

        /** Update other local players. */
        out.writeBits(8, player.getPlayers().size());
        for (Iterator<Player> i = player.getPlayers().iterator(); i.hasNext();) {
            Player other = i.next();
            if (other.getPosition().isViewableFrom(player.getPosition()) && other.getNetwork().getStage() == PlayerNetwork.Stage.LOGGED_IN && !other.isNeedsPlacement() && other.isVisible()) {
                PlayerUpdate.updateOtherPlayerMovement(other, out);
                if (other.getFlags().isUpdateRequired()) {
                    PlayerUpdate.updateState(other, block, false, false);
                }
            } else {
                out.writeBit(true);
                out.writeBits(2, 3);
                i.remove();
            }
        }

        int added = 0;

        /** Update the local player list. */
        for (int i = 0; i < World.getPlayers().length; i++) {
            if (added == 15 || player.getPlayers().size() >= 220) {

                /** Player limit has been reached. */
                break;
            }
            Player other = World.getPlayers()[i];
            if (other == null || other == player || other.getNetwork().getStage() != PlayerNetwork.Stage.LOGGED_IN) {
                continue;
            }
            if (!player.getPlayers().contains(other) && other.getPosition().isViewableFrom(player.getPosition())) {
                added++;
                player.getPlayers().add(other);
                PlayerUpdate.addPlayer(out, player, other);
                PlayerUpdate.updateState(other, block, true, false);
            }
        }

        /** Append the attributes block to the main packet. */
        if (block.getBuffer().position() > 0) {
            out.writeBits(11, 2047);
            out.setAccessType(PacketBuffer.AccessType.BYTE_ACCESS);
            out.writeBytes(block.getBuffer());
        } else {
            out.setAccessType(PacketBuffer.AccessType.BYTE_ACCESS);
        }

        /** Finish the packet and send it. */
        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
    }

    /**
     * Appends the state of a player's chat to a buffer.
     * 
     * @param player
     *            the player.
     * @param out
     *            the buffer.
     */
    public static void appendChat(Player player, PacketBuffer.OutBuffer out) {
        out.writeShort(((player.getChatColor() & 0xff) << 8) + (player.getChatEffects() & 0xff), PacketBuffer.ByteOrder.LITTLE);
        out.writeByte(player.getStaffRights());
        out.writeByte(player.getChatText().length, PacketBuffer.ValueType.C);
        out.writeBytesReverse(player.getChatText());
    }

    /**
     * Appends the state of a player's appearance to a buffer.
     * 
     * @param player
     *            the player.
     * @param out
     *            the buffer.
     */
    public static void appendAppearance(Player player, PacketBuffer.OutBuffer out) {
        PacketBuffer.OutBuffer block = PacketBuffer.newOutBuffer(128);

        /** Gender. */
        block.writeByte(player.getGender()); // Gender

        /** Head icon. */
        block.writeByte(player.getHeadIcon());

        /** Skull icon. */
        block.writeByte(player.getSkullIcon());

        if (player.getNpcAppearanceId() == -1) {

            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HEAD) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HEAD));
            } else {
                block.writeByte(0);
            }

            /** Cape. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_CAPE) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_CAPE));
            } else {
                block.writeByte(0);
            }

            /** Amulet. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_AMULET) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_AMULET));
            } else {
                block.writeByte(0);
            }

            /** Weapon. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON));
            } else {
                block.writeByte(0);
            }

            /** Chest. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_CHEST) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_CHEST));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_CHEST]);
            }

            /** Shield. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_SHIELD) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_SHIELD));
            } else {
                block.writeByte(0);
            }

            /** Arms. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_CHEST) > 1) {

                if (!Misc.getIsPlatebody()[player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_CHEST)]) {
                    block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_ARMS]);
                } else {
                    block.writeByte(0);
                }
            } else {
                block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_ARMS]);
            }

            /** Legs. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_LEGS) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_LEGS));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_LEGS]);
            }

            /** Head. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HEAD) > 1 && Misc.getIsFullHelm()[player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HEAD)]) {
                block.writeByte(0);
            } else {
                block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_HEAD]);
            }

            /** Hands. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HANDS) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HANDS));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_HANDS]);
            }

            /** Feet. */
            if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_FEET) > 1) {
                block.writeShort(0x200 + player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_FEET));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_FEET]);
            }

            /** Beard. */
            if (player.getGender() == Misc.GENDER_MALE) {
                if (player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HEAD) > 1 && !Misc.getIsFullHelm()[player.getEquipment().getItemContainer().getItemId(Misc.EQUIPMENT_SLOT_HEAD)]) {
                    block.writeShort(0x100 + player.getAppearance()[Misc.APPEARANCE_SLOT_BEARD]);
                } else {
                    block.writeByte(0);
                }
            }

        } else {
            block.writeShort(-1);
            block.writeShort(player.getNpcAppearanceId());
        }

        /** Player colors */
        block.writeByte(player.getColors()[0]);
        block.writeByte(player.getColors()[1]);
        block.writeByte(player.getColors()[2]);
        block.writeByte(player.getColors()[3]);
        block.writeByte(player.getColors()[4]);

        /** Movement animations */
        block.writeShort(PlayerAnimation.getStandEmote()); // stand
        block.writeShort(PlayerAnimation.getStandTurnEmote()); // stand turn
        block.writeShort(PlayerAnimation.getWalkEmote()); // walk
        block.writeShort(PlayerAnimation.getTurn180Emote()); // turn 180
        block.writeShort(PlayerAnimation.getTurn90CWEmote()); // turn 90 cw
        block.writeShort(PlayerAnimation.getTurn90CCWEmote()); // turn 90 ccw
        block.writeShort(PlayerAnimation.getRunEmote()); // run

        /** Player context menus */
        block.writeLong(Misc.nameToLong(player.getUsername()));
        block.writeByte(player.getSkills().getCombatLevel());
        block.writeShort(0);

        /** Append the block length and the block to the packet. */
        out.writeByte(block.getBuffer().position(), PacketBuffer.ValueType.C);
        out.writeBytes(block.getBuffer());
    }

    /**
     * Adds a player to the local player list of another player.
     * 
     * @param out
     *            the packet to write to.
     * @param player
     *            the host player.
     * @param other
     *            the player being added.
     */
    public static void addPlayer(PacketBuffer.OutBuffer out, Player player, Player other) {
        out.writeBits(11, other.getSlot()); // Server slot.
        out.writeBit(true); // Yes, an update is required.
        out.writeBit(true); // Discard walking queue(?)

        // Write the relative position.
        Position delta = Misc.delta(player.getPosition(), other.getPosition());
        out.writeBits(5, delta.getY());
        out.writeBits(5, delta.getX());
    }

    /**
     * Updates movement for this local player. The difference between this
     * method and the other player method is that this will make use of sector
     * 2,3 to place the player in a specific position while sector 2,3 is not
     * present in updating of other players (it simply flags local list removal
     * instead).
     * 
     * @param player
     *            the player to update movement for.
     * @param out
     *            the packet to write to.
     */
    public static void updateLocalPlayerMovement(Player player, PacketBuffer.OutBuffer out) {
        boolean updateRequired = player.getFlags().isUpdateRequired();
        if (player.isNeedsPlacement()) { // Do they need placement?
            out.writeBit(true); // Yes, there is an update.
            int posX = player.getPosition().getLocalX(player.getCurrentRegion());
            int posY = player.getPosition().getLocalY(player.getCurrentRegion());
            appendPlacement(out, posX, posY, player.getPosition().getZ(), player.isResetMovementQueue(), updateRequired);
            player.setNeedsPlacement(false);
        } else { // No placement update, check for movement.
            int pDir = player.getPrimaryDirection();
            int sDir = player.getSecondaryDirection();
            if (pDir != -1) { // If they moved.
                out.writeBit(true); // Yes, there is an update.
                if (sDir != -1) { // If they ran.
                    appendRun(out, pDir, sDir, updateRequired);
                } else { // Movement but no running - they walked.
                    appendWalk(out, pDir, updateRequired);
                }
            } else { // No movement.
                if (updateRequired) { // Does the state need to be updated?
                    out.writeBit(true); // Yes, there is an update.
                    appendStand(out);
                } else { // No update whatsoever.
                    out.writeBit(false);
                }
            }
        }
    }

    /**
     * Updates the movement of a player for another player (does not make use of
     * sector 2,3).
     * 
     * @param player
     *            the player to update movement for.
     * @param out
     *            the packet to write to.
     */
    public static void updateOtherPlayerMovement(Player player, PacketBuffer.OutBuffer out) {
        boolean updateRequired = player.getFlags().isUpdateRequired();
        int pDir = player.getPrimaryDirection();
        int sDir = player.getSecondaryDirection();
        if (pDir != -1) { // If they moved.
            out.writeBit(true); // Yes, there is an update.
            if (sDir != -1) { // If they ran.
                appendRun(out, pDir, sDir, updateRequired);
            } else { // Movement but no running - they walked.
                appendWalk(out, pDir, updateRequired);
            }
        } else { // No movement.
            if (updateRequired) { // Does the state need to be updated?
                out.writeBit(true); // Yes, there is an update.
                appendStand(out);
            } else { // No update whatsoever.
                out.writeBit(false);
            }
        }
    }

    /**
     * Updates the state of a player.
     * 
     * @param player
     *            the player to update state for.
     * @param block
     *            the update block.
     */
    public static void updateState(Player player, PacketBuffer.OutBuffer block, boolean forceAppearance, boolean noChat) {
        /** First we must prepare the mask. */
        int mask = 0x0;

        if (player.getFlags().get(Flag.GRAPHICS)) {
            mask |= 0x100;
        }
        if (player.getFlags().get(Flag.ANIMATION)) {
            mask |= 8;
        }
        if (player.getFlags().get(Flag.FORCED_CHAT)) {
            mask |= 4;
        }
        if (player.getFlags().get(Flag.CHAT) && !noChat) {
            mask |= 0x80;
        }
        if (player.getFlags().get(Flag.APPEARANCE) || forceAppearance) {
            mask |= 0x10;
        }
        if (player.getFlags().get(Flag.FACE_ENTITY)) {
            mask |= 1;
        }
        if (player.getFlags().get(Flag.FACE_COORDINATE)) {
            mask |= 2;
        }
        if (player.getFlags().get(Flag.HIT)) {
            mask |= 0x20;
        }
        if (player.getFlags().get(Flag.HIT_2)) {
            mask |= 0x200;
        }

        /** Now, we write the actual mask. */
        if (mask >= 0x100) {
            mask |= 0x40;
            block.writeShort(mask, PacketBuffer.ByteOrder.LITTLE);
        } else {
            block.writeByte(mask);
        }

        /** Finally, we append the attributes blocks. */
        // Graphics
        if (player.getFlags().get(Flag.GRAPHICS)) {
            appendGfx(player, block);
        }
        // Animation
        if (player.getFlags().get(Flag.ANIMATION)) {
            appendAnimation(player, block);
        }
        // Forced chat
        if (player.getFlags().get(Flag.FORCED_CHAT)) {
            appendForcedChat(player, block);
        }
        // Regular chat
        if (player.getFlags().get(Flag.CHAT) && !noChat) {
            appendChat(player, block);
        }
        // Face entity
        if (player.getFlags().get(Flag.FACE_ENTITY)) {
            appendFaceEntity(player, block);
        }
        // Appearance
        if (player.getFlags().get(Flag.APPEARANCE) || forceAppearance) {
            appendAppearance(player, block);
        }
        // Face coordinates
        if (player.getFlags().get(Flag.FACE_COORDINATE)) {
            appendFaceCoordinate(player, block);
        }
        // Primary hit
        if (player.getFlags().get(Flag.HIT)) {
            appendPrimaryHit(player, block);
        }
        // Secondary hit
        if (player.getFlags().get(Flag.HIT_2)) {
            appendSecondaryHit(player, block);
        }
    }

    /**
     * Update the forced chat block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendForcedChat(Player player, PacketBuffer.OutBuffer out) {
        out.writeString(player.getForcedText());
    }

    /**
     * Update the face entity block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendFaceEntity(Player player, PacketBuffer.OutBuffer out) {
        out.writeShort(player.getFaceIndex(), ByteOrder.LITTLE);
    }

    /**
     * Update the face coordinate block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendFaceCoordinate(Player player, PacketBuffer.OutBuffer out) {
        out.writeShort(player.getFaceCoordinates().getX(), ValueType.A, ByteOrder.LITTLE);
        out.writeShort(player.getFaceCoordinates().getY(), ByteOrder.LITTLE);
    }

    /**
     * Update the animation block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendAnimation(Player player, PacketBuffer.OutBuffer out) {
        out.writeShort(player.getAnimation().getId(), ByteOrder.LITTLE);
        out.writeByte(player.getAnimation().getDelay(), ValueType.C);
    }

    /**
     * Update the primary hitmark block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendPrimaryHit(Player player, PacketBuffer.OutBuffer out) {
        out.writeByte(player.getPrimaryHit().getDamage());
        out.writeByte(player.getPrimaryHit().getDamageType().ordinal(), ValueType.A);

        if (player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevel() <= 0) {
            player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].setLevel(0);

            try {
                player.setHasDied(true);
                GameLogic.getSingleton().submit(player.onDeath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.writeByte(player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevel(), ValueType.C);
        out.writeByte(player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevelForExperience());
    }

    /**
     * Update the secondary hitmark block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendSecondaryHit(Player player, PacketBuffer.OutBuffer out) {
        player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].decreaseLevel(player.getSecondaryHit().getDamage());

        out.writeByte(player.getSecondaryHit().getDamage());
        out.writeByte(player.getSecondaryHit().getDamageType().ordinal(), ValueType.S);

        if (player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevel() <= 0) {
            player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].setLevel(0);

            try {
                player.setHasDied(true);
                GameLogic.getSingleton().submit(player.onDeath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.writeByte(player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevel());
        out.writeByte(player.getSkills().getTrainable()[Skill.HITPOINTS.ordinal()].getLevelForExperience(), ValueType.C);
        SkillManager.getSingleton().refresh(player, Skill.HITPOINTS);
    }

    /**
     * Update the graphics block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendGfx(Player player, PacketBuffer.OutBuffer out) {
        out.writeShort(player.getGfx().getId(), ByteOrder.LITTLE);
        out.writeInt(player.getGfx().getDelay());
    }

    /**
     * Appends the stand version of the movement section of the update packet
     * (sector 2,0). Appending this (instead of just a zero bit) automatically
     * assumes that there is a required attribute update afterwards.
     * 
     * @param out
     *            the buffer to append to.
     */
    public static void appendStand(PacketBuffer.OutBuffer out) {
        out.writeBits(2, 0); // 0 - no movement.
    }

    /**
     * Appends the walk version of the movement section of the update packet
     * (sector 2,1).
     * 
     * @param out
     *            the buffer to append to
     * @param direction
     *            the walking direction
     * @param attributesUpdate
     *            whether or not a player attributes update is required
     */
    public static void appendWalk(PacketBuffer.OutBuffer out, int direction, boolean attributesUpdate) {
        out.writeBits(2, 1); // 1 - walking.

        /** Append the actual sector. */
        out.writeBits(3, direction);
        out.writeBit(attributesUpdate);
    }

    /**
     * Appends the walk version of the movement section of the update packet
     * (sector 2,2).
     * 
     * @param out
     *            the buffer to append to.
     * @param direction
     *            the walking direction.
     * @param direction2
     *            the running direction.
     * @param attributesUpdate
     *            whether or not a player attributes update is required.
     */
    public static void appendRun(PacketBuffer.OutBuffer out, int direction, int direction2, boolean attributesUpdate) {
        out.writeBits(2, 2); // 2 - running.

        /** Append the actual sector. */
        out.writeBits(3, direction);
        out.writeBits(3, direction2);
        out.writeBit(attributesUpdate);
    }

    /**
     * Appends the player placement version of the movement section of the
     * update packet (sector 2,3). Note that by others this was previously
     * called the "teleport update".
     * 
     * @param out
     *            the buffer to append to.
     * @param localX
     *            the local X coordinate.
     * @param localY
     *            the local Y coordinate.
     * @param z
     *            the Z coordinate.
     * @param discardMovementQueue
     *            whether or not the client should discard the movement queue.
     * @param attributesUpdate
     *            whether or not a plater attributes update is required.
     */
    public static void appendPlacement(PacketBuffer.OutBuffer out, int localX, int localY, int z, boolean discardMovementQueue, boolean attributesUpdate) {
        out.writeBits(2, 3); // 3 - placement.

        /** Append the actual sector. */
        out.writeBits(2, z);
        out.writeBit(discardMovementQueue);
        out.writeBit(attributesUpdate);
        out.writeBits(7, localY);
        out.writeBits(7, localX);
    }
}
