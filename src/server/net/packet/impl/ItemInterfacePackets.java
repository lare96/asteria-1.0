package server.net.packet.impl;

import server.net.buffer.PacketBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.player.Player;
import server.world.entity.player.container.InventoryContainer;
import server.world.entity.player.skill.TrainableSkill;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.shop.Shop;

/**
 * Sent when the player tries to bank, trade, buy, sell, equip, remove, insert,
 * or swap items.
 * 
 * @author lare96
 */
public class ItemInterfacePackets implements ClientPacketBuilder {

    @Override
    public void execute(Player player, PacketBuffer.InBuffer in) {

        int interfaceId, slot, itemId;

        switch (player.getNetwork().getPacketOpcode()) {
            case 145:
                interfaceId = in.readShort(PacketBuffer.ValueType.A);
                slot = in.readShort(PacketBuffer.ValueType.A);
                itemId = in.readShort(PacketBuffer.ValueType.A);

                TrainableSkill.check(player);

                switch (interfaceId) {

                    case 1688:
                        player.getEquipment().removeItem(slot);
                        break;

                    case 5064:
                        player.getBank().depositItem(slot, itemId, 1);
                        break;

                    case 5382:
                        player.getBank().withdrawItem(slot, itemId, 1);
                        break;
                    case 3900:
                        Shop.getShop(player.getOpenShopId()).sendItemBuyingPrice(player, new Item(itemId));
                        break;
                    case 3823:
                        Shop.getShop(player.getOpenShopId()).sendItemSellingPrice(player, new Item(itemId));
                        break;
                    case 3322:
                        player.getTrading().offer(new Item(itemId, 1), slot);
                        break;
                    case 3415:
                        player.getTrading().unoffer(new Item(itemId, 1));
                        break;
                }
                break;

            case 117:
                interfaceId = in.readShort(true, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
                itemId = in.readShort(true, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
                slot = in.readShort(true, PacketBuffer.ByteOrder.LITTLE);

                switch (interfaceId) {

                    case 5064:
                        player.getBank().depositItem(slot, itemId, 5);
                        break;

                    case 5382:
                        player.getBank().withdrawItem(slot, itemId, 5);
                        break;
                    case 3900:
                        Shop.getShop(player.getOpenShopId()).buyItem(player, new Item(itemId, 1));
                        break;
                    case 3823:
                        Shop.getShop(player.getOpenShopId()).sellItem(player, new Item(itemId, 1), slot);
                        break;
                    case 3322:
                        player.getTrading().offer(new Item(itemId, 5), slot);
                        break;
                    case 3415:
                        player.getTrading().unoffer(new Item(itemId, 5));
                        break;
                }
                break;

            case 43:
                interfaceId = in.readShort(PacketBuffer.ByteOrder.LITTLE);
                itemId = in.readShort(PacketBuffer.ValueType.A);
                slot = in.readShort(PacketBuffer.ValueType.A);

                switch (interfaceId) {

                    case 5064:
                        player.getBank().depositItem(slot, itemId, 10);
                        break;

                    case 5382:
                        player.getBank().withdrawItem(slot, itemId, 10);
                        break;
                    case 3900:
                        Shop.getShop(player.getOpenShopId()).buyItem(player, new Item(itemId, 5));
                        break;
                    case 3823:
                        Shop.getShop(player.getOpenShopId()).sellItem(player, new Item(itemId, 5), slot);
                        break;
                    case 3322:
                        player.getTrading().offer(new Item(itemId, 10), slot);
                        break;
                    case 3415:
                        player.getTrading().unoffer(new Item(itemId, 10));
                        break;

                }
                break;

            case 129:
                slot = in.readShort(PacketBuffer.ValueType.A);
                interfaceId = in.readShort();
                itemId = in.readShort(PacketBuffer.ValueType.A);

                switch (interfaceId) {

                    case 5064:
                        player.getBank().depositItem(slot, itemId, player.getInventory().getItemContainer().getCount(itemId));
                        break;

                    case 5382:
                        int withdrawAmount = 0;
                        if (player.isWithdrawAsNote()) {
                            withdrawAmount = player.getBank().getContainer().getCount(itemId);
                        } else {
                            Item itemWithdrew = new Item(itemId, 1);
                            withdrawAmount = ItemDefinition.getDefinitions()[itemWithdrew.getId()].isStackable() ? player.getBank().getContainer().getCount(itemId) : 28;
                        }
                        player.getBank().withdrawItem(slot, itemId, withdrawAmount);
                        break;
                    case 3900:
                        Shop.getShop(player.getOpenShopId()).buyItem(player, new Item(itemId, 10));
                        break;

                    case 3823:
                        Shop.getShop(player.getOpenShopId()).sellItem(player, new Item(itemId, 10), slot);
                        break;
                    case 3322:
                        player.getTrading().offer(new Item(itemId, player.getInventory().getItemContainer().getCount(itemId)), slot);
                        break;
                    case 3415:
                        player.getTrading().unoffer(new Item(itemId, player.getTrading().getOffering().getCount(itemId)));
                        break;
                }

                break;

            case 41:
                @SuppressWarnings("unused")
                int wear = in.readShort(false);
                slot = in.readShort(false, PacketBuffer.ValueType.A);
                interfaceId = in.readShort(false, PacketBuffer.ValueType.A);

                player.getEquipment().equipItem(slot);
                TrainableSkill.check(player);
                break;

            case 214:
                interfaceId = in.readShort(PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
                in.readByte(PacketBuffer.ValueType.C);
                int fromSlot = in.readShort(PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
                int toSlot = in.readShort(PacketBuffer.ByteOrder.LITTLE);

                switch (interfaceId) {

                    case 3214:
                        player.getInventory().swap(fromSlot, toSlot);
                        player.getInventory().refresh(InventoryContainer.DEFAULT_INVENTORY_INTERFACE);
                        break;

                    case 5382:
                        if (player.isInsertItem()) {
                            player.getBank().getContainer().swap(fromSlot, toSlot);
                        } else {
                            player.getBank().getContainer().insert(fromSlot, toSlot);
                        }
                        Item[] bankItems = player.getBank().getContainer().toArray();
                        player.getServerPacketBuilder().sendUpdateItems(5382, bankItems);
                        break;
                }

                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 145, 41, 117, 43, 129, 214 };
    }
}
