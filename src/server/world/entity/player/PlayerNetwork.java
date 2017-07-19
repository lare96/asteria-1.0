package server.world.entity.player;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;

import server.Server;
import server.net.HostGateway;
import server.net.ISAACCipher;
import server.net.buffer.PacketBuffer;
import server.net.packet.ServerPacketBuilder;
import server.util.Misc;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameManager;

/**
 * The class behind a Player that handles all networking-related things.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class PlayerNetwork {

    /**
     * The selection key.
     */
    private final SelectionKey key;

    /**
     * If the player has disconnected.
     */
    private boolean disconnected;

    /**
     * If the player has disconnected from a packet issue.
     */
    private boolean packetDisconnect;

    /**
     * The buffer for reading.
     */
    private final ByteBuffer inData;

    /**
     * The buffer for writing.
     */
    private final ByteBuffer outData;

    /**
     * Packet timeout stopwatch.
     */
    private final Misc.Stopwatch timeoutStopwatch = new Misc.Stopwatch();

    /**
     * The socket channel.
     */
    private SocketChannel socketChannel;

    /**
     * The login stage.
     */
    private Stage stage;

    /**
     * The packet opcode.
     */
    private int packetOpcode = -1;

    /**
     * The packet length.
     */
    private int packetLength = -1;

    /**
     * The packet encryptor.
     */
    private ISAACCipher encryptor;

    /**
     * The packet decryptor.
     */
    private ISAACCipher decryptor;

    /**
     * The player.
     */
    private Player player;

    /**
     * Packets being sent to the client.
     */
    private ServerPacketBuilder serverPacketBuilder;

    /**
     * The host.
     */
    private String host;

    /**
     * The current connection stage of the client.
     * 
     * @author blakeman8192
     */
    public enum Stage {
        CONNECTED, LOGGING_IN, LOGGED_IN, LOGGED_OUT
    }

    /**
     * Creates a new io network.
     * 
     * @param key
     *            the SelectionKey of the client.
     */
    public PlayerNetwork(SelectionKey key) {
        this.key = key;
        setStage(Stage.CONNECTED);
        inData = ByteBuffer.allocateDirect(512);
        outData = ByteBuffer.allocateDirect(8192);
        if (key != null) {
            socketChannel = (SocketChannel) key.channel();
            host = socketChannel.socket().getInetAddress().getHostAddress();
            player = new Player(this);
            serverPacketBuilder = new ServerPacketBuilder(player);
        }
    }

    /**
     * Disconnects the player from this io network.
     */
    public void disconnect() {
        player.getTrading().resetTrade(false);

        Minigame minigame = MinigameManager.inAnyMinigame(player);

        if (minigame != null) {
            minigame.logout(player);
        }

        player.getPrivateMessage().sendPrivateMessageOnLogout();
        key.attach(null);
        key.cancel();
        setStage(Stage.LOGGED_OUT);
        setDisconnected(true);
        try {
            if (player != null) {
                player.logout();
            }
            socketChannel.close();
            HostGateway.exit(host);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sends the buffer to the socket.
     * 
     * @param buffer
     *            the buffer to send.
     */
    public void send(ByteBuffer buffer) {
        if (!socketChannel.isOpen())
            return;

        /** Prepare the buffer for writing. */
        buffer.flip();

        try {
            /** ...and write it! */
            socketChannel.write(buffer);

            /** If not all the data was sent. */
            if (buffer.hasRemaining()) {

                /** Queue it. */
                synchronized (outData) {
                    outData.put(buffer);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.setPacketDisconnect(true);
            disconnect();
        }
    }

    /**
     * Handles the login process of the client.
     */
    public void handleLogin() throws Exception {
        switch (getStage()) {
            case CONNECTED:
                if (inData.remaining() < 2) {
                    inData.compact();
                    return;
                }

                /** Validate the request. */
                int request = inData.get() & 0xff;
                inData.get();

                if (request != 14) {
                    Server.print("Invalid login request: " + request);
                    disconnect();
                    return;
                }

                /** Write the response. */
                PacketBuffer.OutBuffer out = PacketBuffer.newOutBuffer(17);
                out.writeLong(0); // First 8 bytes are ignored by the client.
                out.writeByte(0); // The response opcode, 0 for logging in.
                out.writeLong(new SecureRandom().nextLong()); // SSK.
                send(out.getBuffer());

                setStage(Stage.LOGGING_IN);
                break;
            case LOGGING_IN:
                if (inData.remaining() < 2) {
                    inData.compact();
                    return;
                }

                /** Validate the login type. */
                int loginType = inData.get();

                if (loginType != 16 && loginType != 18) {
                    Server.print("Invalid login type: " + loginType);
                    disconnect();
                    return;
                }

                /** Ensure that we can read all of the login block. */
                int blockLength = inData.get() & 0xff;

                if (inData.remaining() < blockLength) {
                    inData.flip();
                    inData.compact();
                    return;
                }

                /** Read the login block. */
                PacketBuffer.InBuffer in = PacketBuffer.newInBuffer(inData);

                in.readByte(); // Skip the magic ID value 255.

                /** Validate the client version. */
                int clientVersion = in.readShort();

                if (clientVersion != 317) {
                    Server.print("Invalid client version: " + clientVersion);
                    disconnect();
                    return;
                }

                in.readByte(); // Skip the high/low memory version.

                for (int i = 0; i < 9; i++) { // Skip the CRC keys.
                    in.readInt();
                }

                in.readByte(); // Skip RSA block length. If we wanted to, we
                // would decode RSA at this point.

                /** Validate that the RSA block was decoded properly. */
                int rsaOpcode = in.readByte();
                if (rsaOpcode != 10) {
                    Server.print("Unable to decode RSA block properly!");
                    disconnect();
                    return;
                }

                /** Set up the ISAAC ciphers. */
                long clientHalf = in.readLong();
                long serverHalf = in.readLong();

                int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };

                setDecryptor(new ISAACCipher(isaacSeed));

                for (int i = 0; i < isaacSeed.length; i++) {
                    isaacSeed[i] += 50;

                }

                setEncryptor(new ISAACCipher(isaacSeed));

                /** Read the user authentication. */
                in.readInt(); // Skip the user ID.
                String username = in.readString();
                String password = in.readString();

                player.setUsername(username);
                player.setPassword(password);

                player.login();
                setStage(Stage.LOGGED_IN);
                break;
            case LOGGED_OUT:
                disconnect();
                break;
            case LOGGED_IN:
                disconnect();
                break;
        }
    }

    /**
     * Gets the remote host of the client.
     * 
     * @return the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the encryptor.
     * 
     * @param encryptor
     *            the encryptor.
     */
    public void setEncryptor(ISAACCipher encryptor) {
        this.encryptor = encryptor;
    }

    /**
     * Gets the encryptor.
     * 
     * @return the encryptor.
     */
    public ISAACCipher getEncryptor() {
        return encryptor;
    }

    /**
     * Sets the decryptor.
     * 
     * @param decryptor
     *            the decryptor.
     */
    public void setDecryptor(ISAACCipher decryptor) {
        this.decryptor = decryptor;
    }

    /**
     * Gets the decryptor.
     * 
     * @return the decryptor.
     */
    public ISAACCipher getDecryptor() {
        return decryptor;
    }

    /**
     * Gets the Player.
     * 
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the SocketChannel.
     * 
     * @return the SocketChannel.
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * @return the stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @param stage
     *            the stage to set.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * @return the timeoutStopwatch.
     */
    public Misc.Stopwatch getTimeoutStopwatch() {
        return timeoutStopwatch;
    }

    /**
     * @return the packetOpcode.
     */
    public int getPacketOpcode() {
        return packetOpcode;
    }

    /**
     * @param packetOpcode
     *            the packetOpcode to set.
     */
    public void setPacketOpcode(int packetOpcode) {
        this.packetOpcode = packetOpcode;
    }

    /**
     * @return the packetLength.
     */
    public int getPacketLength() {
        return packetLength;
    }

    /**
     * @param packetLength
     *            the packetLength to set.
     */
    public void setPacketLength(int packetLength) {
        this.packetLength = packetLength;
    }

    /**
     * @return the inData.
     */
    public ByteBuffer getInData() {
        return inData;
    }

    /**
     * @return the outData.
     */
    public ByteBuffer getOutData() {
        return outData;
    }

    /**
     * @return the rs2Packet.
     */
    public ServerPacketBuilder getServerPacketBuilder() {
        return serverPacketBuilder;
    }

    /**
     * @return the disconnected.
     */
    public boolean isDisconnected() {
        return disconnected;
    }

    /**
     * @param disconnected
     *            the disconnected to set.
     */
    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    /**
     * @return the packetDisconnect.
     */
    public boolean isPacketDisconnect() {
        return packetDisconnect;
    }

    /**
     * @param packetDisconnect
     *            the packetDisconnect to set.
     */
    public void setPacketDisconnect(boolean packetDisconnect) {
        this.packetDisconnect = packetDisconnect;
    }
}
