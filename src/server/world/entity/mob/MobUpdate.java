package server.world.entity.mob;

import java.util.Iterator;

import server.logic.GameLogic;
import server.net.buffer.PacketBuffer;
import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.ValueType;
import server.util.Misc;
import server.world.World;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;
import server.world.map.Position;

/**
 * Provides static utility methods for updating NPCs.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class MobUpdate {

    /**
     * Updates all NPCs for the argued Player.
     * 
     * @param player
     *            the argued player.
     */
    public static void update(Player player) {
        // XXX: The buffer sizes may need to be tuned.
        PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(2048);
        PacketBuffer.OutBuffer block = PacketBuffer.newOutBuffer(1024);

        /** Initialize the update packet. */
        out.writeVariableShortPacketHeader(player.getNetwork().getEncryptor(), 65);
        out.setAccessType(PacketBuffer.AccessType.BIT_ACCESS);

        /** Update the NPCs in the local list. */
        out.writeBits(8, player.getNpcs().size());
        for (Iterator<Mob> i = player.getNpcs().iterator(); i.hasNext();) {
            Mob npc = i.next();
            if (npc.getPosition().isViewableFrom(player.getPosition()) && npc.isVisible()) {
                MobUpdate.updateNpcMovement(out, npc);
                if (npc.getFlags().isUpdateRequired()) {
                    MobUpdate.updateState(block, npc);
                }
            } else {
                /** Remove the NPC from the local list. */
                out.writeBit(true);
                out.writeBits(2, 3);
                i.remove();
            }
        }

        /** Update the local NPC list itself. */
        for (int i = 0; i < World.getNpcs().length; i++) {
            Mob npc = World.getNpcs()[i];

            if (npc == null || player.getNpcs().contains(npc) || !npc.isVisible()) {
                continue;
            }

            if (npc.getPosition().isViewableFrom(player.getPosition())) {
                /** Runesource npc updating fix here! - lare96 */
                npc.getFlags().flag(Flag.APPEARANCE);
                player.getNpcs().add(npc);
                /** Runesource npc updating fix here! - lare96 */

                addNpc(out, player, npc);

                if (npc.getFlags().isUpdateRequired()) {
                    MobUpdate.updateState(block, npc);
                }
            }
        }

        /** Append the update block to the packet if need be. */
        if (block.getBuffer().position() > 0) {
            out.writeBits(14, 16383);
            out.setAccessType(PacketBuffer.AccessType.BYTE_ACCESS);
            out.writeBytes(block.getBuffer());
        } else {
            out.setAccessType(PacketBuffer.AccessType.BYTE_ACCESS);
        }

        /** Ship the packet out to the client. */
        out.finishVariableShortPacketHeader();
        player.getNetwork().send(out.getBuffer());
    }

    /**
     * Adds the NPC to the client side local list.
     * 
     * @param out
     *            The buffer to write to.
     * @param player
     *            The player.
     * @param npc
     *            The NPC being added.
     */
    private static void addNpc(PacketBuffer.OutBuffer out, Player player, Mob npc) {
        out.writeBits(14, npc.getSlot());
        Position delta = Misc.delta(player.getPosition(), npc.getPosition());
        out.writeBits(5, delta.getY());
        out.writeBits(5, delta.getX());
        out.writeBit(npc.getFlags().isUpdateRequired());
        out.writeBits(12, npc.getMobId());
        out.writeBit(true);
    }

    /**
     * Updates the movement of a NPC for this cycle.
     * 
     * @param out
     *            The buffer to write to.
     * @param npc
     *            The NPC to update.
     */
    private static void updateNpcMovement(PacketBuffer.OutBuffer out, Mob npc) {
        if (npc.getPrimaryDirection() == -1) {
            if (npc.getFlags().isUpdateRequired()) {
                out.writeBit(true);
                out.writeBits(2, 0);
            } else {
                out.writeBit(false);
            }
        } else {
            out.writeBit(true);
            out.writeBits(2, 1);
            out.writeBits(3, npc.getPrimaryDirection());

            // XXX: could be the issue
            if (npc.getFlags().isUpdateRequired()) {
                out.writeBit(true);
            } else {
                out.writeBit(false);
            }
        }
    }

    /**
     * Updates the state of the NPC to the given update block.
     * 
     * @param block
     *            The update block to append to.
     * @param npc
     *            The NPC to update.
     */
    private static void updateState(PacketBuffer.OutBuffer block, Mob npc) {
        int mask = 0x0;

        /** NPC update masks. */
        if (npc.getFlags().get(Flag.ANIMATION)) {
            mask |= 0x10;
        }
        if (npc.getFlags().get(Flag.HIT_2)) {
            mask |= 8;
        }
        if (npc.getFlags().get(Flag.GRAPHICS)) {
            mask |= 0x80;
        }
        if (npc.getFlags().get(Flag.FACE_ENTITY)) {
            mask |= 0x20;
        }
        if (npc.getFlags().get(Flag.FORCED_CHAT)) {
            mask |= 1;
        }
        if (npc.getFlags().get(Flag.HIT)) {
            mask |= 0x40;
        }
        if (npc.getFlags().get(Flag.FACE_COORDINATE)) {
            mask |= 4;
        }

        /** Write the update masks. */
        if (mask >= 0x100) {
            mask |= 0x40;
            block.writeShort(mask, PacketBuffer.ByteOrder.LITTLE);
        } else {
            block.writeByte(mask);
        }

        /** Append the NPC update blocks. */
        if (npc.getFlags().get(Flag.ANIMATION)) {
            appendAnimation(block, npc);
        }
        if (npc.getFlags().get(Flag.HIT_2)) {
            appendSecondaryHit(block, npc);
        }
        if (npc.getFlags().get(Flag.GRAPHICS)) {
            appendGfxUpdate(block, npc);
        }
        if (npc.getFlags().get(Flag.FACE_ENTITY)) {
            appendFaceEntity(block, npc);
        }
        if (npc.getFlags().get(Flag.FORCED_CHAT)) {
            appendForcedChat(block, npc);
        }
        if (npc.getFlags().get(Flag.HIT)) {
            appendPrimaryHit(block, npc);
        }
        if (npc.getFlags().get(Flag.FACE_COORDINATE)) {
            appendFaceCoordinate(block, npc);
        }
    }

    /**
     * Update the GFX block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendGfxUpdate(PacketBuffer.OutBuffer out, Mob npc) {
        out.writeShort(npc.getGfx().getId());
        out.writeInt(npc.getGfx().getDelay());
    }

    /**
     * Update the secondary hit block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendSecondaryHit(PacketBuffer.OutBuffer out, Mob npc) {
        npc.decreaseHealth(npc.getSecondaryHit().getDamage());

        if (npc.getCurrentHealth() <= 0) {
            npc.setCurrentHealth(0);

            try {
                npc.setHasDied(true);
                GameLogic.getSingleton().submit(npc.onDeath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.writeByte(npc.getSecondaryHit().getDamage(), ValueType.A);
        out.writeByte(npc.getSecondaryHit().getDamageType().ordinal(), ValueType.C);
        out.writeByte(npc.getCurrentHealth(), ValueType.A);
        out.writeByte(npc.getMaxHealth());
    }

    /**
     * Update the face entity block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendFaceEntity(PacketBuffer.OutBuffer out, Mob npc) {
        out.writeShort(npc.getFaceIndex());
    }

    /**
     * Update the forced chat block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendForcedChat(PacketBuffer.OutBuffer out, Mob npc) {
        out.writeString(npc.getForcedText());
    }

    /**
     * Update the primary hit block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendPrimaryHit(PacketBuffer.OutBuffer out, Mob npc) {
        npc.decreaseHealth(npc.getPrimaryHit().getDamage());

        if (npc.getCurrentHealth() <= 0) {
            npc.setCurrentHealth(0);

            try {
                npc.setHasDied(true);
                GameLogic.getSingleton().submit(npc.onDeath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        out.writeByte(npc.getPrimaryHit().getDamage(), ValueType.C);
        out.writeByte(npc.getPrimaryHit().getDamageType().ordinal(), ValueType.S);
        out.writeByte(npc.getCurrentHealth(), ValueType.S);
        out.writeByte(npc.getMaxHealth(), ValueType.C);
    }

    /**
     * Update the face coordinate block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendFaceCoordinate(PacketBuffer.OutBuffer out, Mob npc) {
        out.writeShort(npc.getFaceCoordinates().getX(), ByteOrder.LITTLE);
        out.writeShort(npc.getFaceCoordinates().getY(), ByteOrder.LITTLE);
    }

    /**
     * Update the animation block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendAnimation(PacketBuffer.OutBuffer out, Mob npc) {
        out.writeShort(npc.getAnimation().getId(), ByteOrder.LITTLE);
        out.writeByte(npc.getAnimation().getDelay());
    }
}
