package server.net.packet.impl;

import server.net.buffer.PacketBuffer.ByteOrder;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.buffer.PacketBuffer.ValueType;
import server.net.packet.ClientPacketBuilder;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.entity.player.content.Eat;
import server.world.entity.player.content.Eat.Data;
import server.world.entity.player.skill.TrainableSkill;
import server.world.entity.player.skill.impl.Prayer;
import server.world.entity.player.skill.impl.Prayer.Bone;
import server.world.item.Item;

/**
 * Sent when the player uses the first click item option.
 * 
 * @author lare96
 */
public class FirstClickItemPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        int slot = in.readShort(false, ValueType.A);
        int id = in.readShort(false, ByteOrder.LITTLE);

        TrainableSkill.check(player);

        if (id != player.getInventory().getItemContainer().getIdBySlot(slot)) {
            return;
        }

        for (Data d : Data.values()) {
            if (d.getItemId() == id) {
                Eat.eat(player, d, slot);
            }
        }

        for (Bone b : Bone.values()) {
            if (b.getBoneId() == id) {
                Prayer.getSingleton().bury(player, b, slot);
            }
        }

        switch (id) {
            case 405:
                Item[] possibleItems = { new Item(995, 1000000), new Item(4151, 1) };
                Item chosen = Misc.randomElement(possibleItems);

                player.getServerPacketBuilder().sendMessage("You open the casket and recieve " + chosen.getDefinition().getItemName() + "x" + chosen.getAmount() + ".");
                player.getInventory().removeItemSlot(new Item(405, 1), slot);
                player.getInventory().addItemToSlot(chosen, slot);
                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 122 };
    }
}
