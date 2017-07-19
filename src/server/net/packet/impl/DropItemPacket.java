package server.net.packet.impl;

import server.net.buffer.PacketBuffer.InBuffer;
import server.net.buffer.PacketBuffer.ValueType;
import server.net.packet.ClientPacketBuilder;
import server.world.World;
import server.world.entity.player.Player;
import server.world.entity.player.skill.TrainableSkill;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.item.WorldItem;
import server.world.map.Position;

/**
 * Sent when the player drops an item.
 * 
 * @author lare96
 */
public class DropItemPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        int item = in.readShort(false, ValueType.A);
        in.readByte(false);
        in.readByte(false);
        int slot = in.readShort(false, ValueType.A);

        TrainableSkill.check(player);

        int amount = ItemDefinition.getDefinitions()[item].isStackable() ? amount = player.getInventory().getItemContainer().getCount(item) : 1;

        if (!World.isCanDrop()) {
            player.getInventory().removeItemSlot(new Item(item, amount), slot);
            player.getServerPacketBuilder().sendMessage("As you drop the item it is miraculously swallowed up by the ground!");
        } else {
            if (player.getInventory().getItemContainer().contains(item)) {
                player.getInventory().removeItemSlot(new Item(item, amount), slot);
                final Position itemLocation = new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
                new WorldItem(new Item(item, amount), itemLocation, player).register();
            }
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 87 };
    }
}
