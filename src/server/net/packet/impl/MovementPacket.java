package server.net.packet.impl;

import server.net.buffer.PacketBuffer;
import server.net.packet.ClientPacketBuilder;
import server.world.entity.mob.MobDialogue;
import server.world.entity.player.Player;
import server.world.entity.player.skill.TrainableSkill;
import server.world.map.Position;

/**
 * Sent whenever the player moves.
 * 
 * @author lare96
 */
public class MovementPacket implements ClientPacketBuilder {

    @Override
    public void execute(Player player, PacketBuffer.InBuffer in) {
        int length = player.getNetwork().getPacketLength();

        if (player.getNetwork().getPacketOpcode() == 248) {
            // player.getRS2Packet().sendMessage("248");
            length -= 14;
        }

        // yellow x
        if (player.getNetwork().getPacketOpcode() == 164) {
            // player.getRS2Packet().sendMessage("164");

            // red x
        } else if (player.getNetwork().getPacketOpcode() == 98) {
            // player.getRS2Packet().sendMessage("98");
        }

        if (player.getMovementQueue().isLockMovement()) {
            return;
        }

        if (player.getMobDialogue() != 0) {
            MobDialogue.getDialogues().get(player.getMobDialogue()).stop(player);
        }

        if (player.getTrading().inTrade()) {
            player.getTrading().resetTrade(false);
        }

        TrainableSkill.check(player);
        player.getServerPacketBuilder().closeWindows();
        player.setOpenShopId(-1);

        int steps = (length - 5) / 2;
        int[][] path = new int[steps][2];
        int firstStepX = in.readShort(PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
        for (int i = 0; i < steps; i++) {
            path[i][0] = in.readByte();
            path[i][1] = in.readByte();
        }
        int firstStepY = in.readShort(PacketBuffer.ByteOrder.LITTLE);

        player.getMovementQueue().reset();
        player.getMovementQueue().setRunPath(in.readByte(PacketBuffer.ValueType.C) == 1);
        player.getMovementQueue().addToPath(new Position(firstStepX, firstStepY));

        for (int i = 0; i < steps; i++) {
            path[i][0] += firstStepX;
            path[i][1] += firstStepY;
            player.getMovementQueue().addToPath(new Position(path[i][0], path[i][1]));
        }
        player.getMovementQueue().finish();

    }

    @Override
    public int[] opcode() {
        return new int[] { 248, 164, 98 };
    }
}
