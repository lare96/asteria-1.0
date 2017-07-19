package server.net.event.impl;

import server.Server;
import server.net.buffer.PacketBuffer;
import server.net.event.NetworkEvent;
import server.net.packet.PacketRegistry;
import server.util.Misc;
import server.world.entity.player.PlayerNetwork;
import server.world.entity.player.PlayerNetwork.Stage;

/**
 * An implementation of the {@link NetworkEvent} that reads and handles incoming
 * packets.
 * 
 * @author lare96
 */
public final class IncomingPacketNetworkEvent implements NetworkEvent {

    @Override
    public void event(PlayerNetwork network) {
        try {

            /** Read the incoming data. */
            if (network.getSocketChannel().read(network.getInData()) == -1) {
                network.disconnect();
                return;
            }

            /** Handle the received data. */
            network.getInData().flip();

            while (network.getInData().hasRemaining()) {

                /** Handle login if we need to. */
                if (network.getStage() != Stage.LOGGED_IN) {
                    network.handleLogin();
                    break;
                }

                /** Decode the packet opcode. */
                if (network.getPacketOpcode() == -1) {
                    network.setPacketOpcode(network.getInData().get() & 0xff);
                    network.setPacketOpcode(network.getPacketOpcode() - network.getDecryptor().getNextValue() & 0xff);
                }

                /** Decode the packet length. */
                if (network.getPacketLength() == -1) {
                    network.setPacketLength(Misc.packetLengths[network.getPacketOpcode()]);

                    if (network.getPacketLength() == -1) {
                        if (!network.getInData().hasRemaining()) {
                            network.getInData().flip();
                            network.getInData().compact();
                            break;
                        }

                        network.setPacketLength(network.getInData().get() & 0xff);
                    }
                }

                /** Decode the packet payload. */
                if (network.getInData().remaining() >= network.getPacketLength()) {

                    /** Reset the timeout counter. */
                    network.getTimeoutStopwatch().reset();

                    /** Gets the buffer's position before this packet is read. */
                    int positionBefore = network.getInData().position();

                    /**
                     * Creates a new buffer for writing to packets backed by the
                     * set data.
                     */
                    PacketBuffer.InBuffer in = PacketBuffer.newInBuffer(network.getInData());

                    /** Execute the packet. */
                    try {
                        if (PacketRegistry.getPackets()[network.getPacketOpcode()] != null) {
                            PacketRegistry.getPackets()[network.getPacketOpcode()].execute(network.getPlayer(), in);
                        } else {
                            Server.print(network.getPlayer() + " unhandled packet " + network.getPacketOpcode());
                        }

                        /**
                         * Handles any errors we may have came across during the
                         * execution of this packet.
                         */
                    } catch (Exception ex) {
                        ex.printStackTrace();

                        /**
                         * Regardless of if there was an error or not, make sure
                         * we have finished reading all of this packet.
                         */
                    } finally {
                        int read = network.getInData().position() - positionBefore;

                        for (int i = read; i < network.getPacketLength(); i++) {
                            network.getInData().get();
                        }
                    }

                    /** Reset for the next packet. */
                    network.setPacketOpcode(-1);
                    network.setPacketLength(-1);
                } else {
                    network.getInData().flip();
                    network.getInData().compact();
                    break;
                }
            }

            /** Clear everything for the next read. */
            network.getInData().clear();
        } catch (Exception e) {
            e.printStackTrace();
            network.setPacketDisconnect(true);
            network.disconnect();
        }
    }
}
