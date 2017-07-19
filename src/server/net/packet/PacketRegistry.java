package server.net.packet;

import server.Server;
import server.net.packet.impl.AttackPlayerPacket;
import server.net.packet.impl.ClickButtonPacket;
import server.net.packet.impl.ClickScreenPacket;
import server.net.packet.impl.CommandPacket;
import server.net.packet.impl.DefaultPacket;
import server.net.packet.impl.DropItemPacket;
import server.net.packet.impl.FirstClickItemPacket;
import server.net.packet.impl.FollowPlayerPacket;
import server.net.packet.impl.ForwardDialoguePacket;
import server.net.packet.impl.InterfaceClickPacket;
import server.net.packet.impl.ItemInterfacePackets;
import server.net.packet.impl.ItemOnItemPacket;
import server.net.packet.impl.ItemOnObjectPacket;
import server.net.packet.impl.MobActionPackets;
import server.net.packet.impl.MovementPacket;
import server.net.packet.impl.ObjectActionPackets;
import server.net.packet.impl.PickupItemPacket;
import server.net.packet.impl.PrivateMessagingPackets;
import server.net.packet.impl.PublicChatPacket;
import server.net.packet.impl.RequestPacket;
import server.net.packet.impl.RotateCameraPacket;
import server.net.packet.impl.UpdateChatOptionsPacket;
import server.net.packet.impl.UpdateRegionPacket;

/**
 * Class that contains static utility methods for registering client built
 * packets.
 * 
 * @author lare96
 */
public final class PacketRegistry {

    /**
     * An array of the active packets.
     */
    private static ClientPacketBuilder[] packets = new ClientPacketBuilder[256];

    /**
     * The amount of packets parsed.
     */
    private static int parsed;

    /**
     * Register a new packet.
     * 
     * @param packet
     *            the packet to register.
     */
    private static void register(ClientPacketBuilder packet) {
        for (int opcode : packet.opcode()) {
            packets[opcode] = packet;
            parsed++;
        }
    }

    /**
     * Register the packets to the array.
     */
    public static void load() {
        register(new ClickButtonPacket());
        register(new PublicChatPacket());
        register(new CommandPacket());
        register(new DefaultPacket());
        register(new UpdateRegionPacket());
        register(new ClickScreenPacket());
        register(new RotateCameraPacket());
        register(new DropItemPacket());
        register(new PickupItemPacket());
        register(new UpdateChatOptionsPacket());
        register(new FollowPlayerPacket());
        register(new RequestPacket());
        register(new FirstClickItemPacket());
        register(new ItemOnObjectPacket());
        register(new ForwardDialoguePacket());
        register(new ItemOnItemPacket());
        register(new InterfaceClickPacket());
        register(new AttackPlayerPacket());
        register(new MovementPacket());
        register(new MobActionPackets());
        register(new ItemInterfacePackets());
        register(new ObjectActionPackets());
        register(new PrivateMessagingPackets());

        Server.print("Registered " + parsed + " packets!");
    }

    /**
     * @return the packets.
     */
    public static ClientPacketBuilder[] getPackets() {
        return packets;
    }
}
