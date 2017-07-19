package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.buffer.PacketBuffer.ValueType;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;
import server.world.entity.player.skill.impl.Cooking;
import server.world.entity.player.skill.impl.Prayer;
import server.world.entity.player.skill.impl.Smithing;
import server.world.entity.player.skill.impl.Cooking.Cook;
import server.world.entity.player.skill.impl.Prayer.Bone;
import server.world.entity.player.skill.impl.Smithing.Smelt;
import server.world.item.Item;
import server.world.map.Position;
import server.world.object.ObjectDistance;

/**
 * Sent when the player uses an item on an object.
 * 
 * @author lare96
 */
public class ItemOnObjectPacket implements ClientPacketBuilder {

    @Override
    public void execute(final Player player, InBuffer in) {
        in.readShort(false);
        final int objectId = in.readShort(true, ByteOrder.LITTLE);
        final int objectY = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        in.readShort(false);
        final int objectX = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        final int itemId = in.readShort(false);
        final int slot = player.getInventory().getItemContainer().getSlotById(itemId);
        if (!player.getInventory().getItemContainer().contains(itemId)) {
            return;
        }

        player.facePosition(new Position(objectX, objectY));

        player.getMovementQueueListener().submit(new Runnable() {
            @Override
            public void run() {
                if (player.getPosition().withinDistance(new Position(objectX, objectY, player.getPosition().getZ()), ObjectDistance.getDefault())) {
                    switch (objectId) {
                        case 409:
                            for (Bone b : Bone.values()) {
                                if (b.getBoneId() == itemId) {
                                    Prayer.getSingleton().altar(player, b, slot);
                                }
                            }
                            break;
                        case 2732:
                            for (Cook fish : Cook.values()) {
                                if (fish.getId() == itemId) {
                                    player.setUsingStove(false);
                                    player.setCook(fish);
                                    Cooking.getSingleton().viewCookInterface(player, fish.getId());
                                }
                            }
                            break;
                        case 114:
                        case 2728:
                            for (Cook fish : Cook.values()) {
                                if (fish.getId() == itemId) {
                                    player.setUsingStove(true);
                                    player.setCook(fish);
                                    Cooking.getSingleton().viewCookInterface(player, fish.getId());
                                }
                            }
                            break;
                        case 2781:
                        case 2785:
                        case 2966:
                        case 6189:
                        case 3044:
                        case 3294:
                        case 4304:
                            for (Smelt smelt : Smelt.values()) {
                                for (Item item : smelt.getItemsNeeded()) {
                                    if (item.getId() == itemId) {
                                        Smithing.getSingleton().smeltInterface(player);
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        });
    }

    @Override
    public int[] opcode() {
        return new int[] { 192 };
    }
}
