package server.net.packet.impl;

import server.net.buffer.PacketBuffer;
import server.net.buffer.PacketBuffer.InBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;
import server.world.entity.player.skill.impl.Firemaking;
import server.world.entity.player.skill.impl.Firemaking.Logs;
import server.world.item.Item;

/**
 * Sent when the player uses an item on another item.
 * 
 * @author lare96
 */
public class ItemOnItemPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, InBuffer in) {
        int itemSecondClickSlot = in.readShort();
        int itemFirstClickSlot = in.readShort(PacketBuffer.ValueType.A);
        in.readShort();
        in.readShort();

        Item itemUsed = player.getInventory().getItemContainer().getItem(itemFirstClickSlot);
        Item itemOn = player.getInventory().getItemContainer().getItem(itemSecondClickSlot);

        if (itemUsed == null || itemOn == null) {
            return;
        }

        switch (itemOn.getId()) {
            case 7156:
                for (Logs l : Logs.values()) {
                    if (l == null) {
                        continue;
                    }

                    if (itemUsed.getId() == l.getLogId()) {
                        Firemaking.getSingleton().lightLog(player, l);
                    }
                }
                break;
        }

        switch (itemUsed.getId()) {
            case 7156:
                for (Logs l : Logs.values()) {
                    if (l == null) {
                        continue;
                    }

                    if (itemOn.getId() == l.getLogId()) {
                        Firemaking.getSingleton().lightLog(player, l);
                    }
                }
                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 53 };
    }
}
