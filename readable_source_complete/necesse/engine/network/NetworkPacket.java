/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.UnknownPacketException;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.PacketRegistry;

public class NetworkPacket {
    public static final int MAX_PACKET_SIZE = 1024;
    private static final int MAX_CONTENT_SIZE = 992;
    private static final Object identifierLock = new Object();
    private static int identifierIncrement = 0;
    public final int type;
    public final long timestamp;
    private int packetIndex;
    public final int identifier;
    private int complete;
    private final PacketData[] packets;
    private Packet packet;
    public final NetworkInfo networkInfo;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public NetworkPacket(Packet packet, NetworkInfo networkInfo) {
        this.packet = packet;
        this.networkInfo = networkInfo;
        this.type = PacketRegistry.getPacketID(packet.getClass());
        if (this.type == -1) {
            throw new IllegalStateException(packet.getClass().getSimpleName() + " class is not registered in PacketData.");
        }
        this.timestamp = PacketRegistry.hasTimestamp(this.type) ? System.nanoTime() : 0L;
        byte[] allContent = packet.getPacketData();
        int totalPackets = allContent.length / 992;
        this.packetIndex = 0;
        if (allContent.length % 992 != 0 || allContent.length == 0) {
            ++totalPackets;
        }
        if (totalPackets > 1) {
            Object object = identifierLock;
            synchronized (object) {
                if (++identifierIncrement == 0) {
                    ++identifierIncrement;
                }
                this.identifier = identifierIncrement;
            }
        } else {
            this.identifier = 0;
        }
        this.packets = new PacketData[totalPackets];
        this.complete = totalPackets;
        for (int i = 0; i < totalPackets; ++i) {
            Packet wrapper = new Packet();
            PacketWriter writer = new PacketWriter(wrapper);
            writer.putNextBoolean(totalPackets > 1);
            if (totalPackets > 1) {
                writer.putNextInt(this.identifier);
                writer.putNextShortUnsigned(i);
                writer.putNextShortUnsigned(totalPackets);
            }
            if (i == 0) {
                writer.putNextShortUnsigned(this.type);
                writer.putNextBoolean(this.timestamp != 0L);
                if (this.timestamp != 0L) {
                    writer.putNextLong(this.timestamp);
                }
            }
            byte[] content = Arrays.copyOfRange(allContent, i * 992, Math.min(allContent.length, (i + 1) * 992));
            writer.putNextBytes(content);
            this.packets[i] = new PacketData(content, wrapper.getPacketData());
        }
    }

    public NetworkPacket(DatagramSocket socket, DatagramPacket datagramPacket) throws UnknownPacketException {
        this(new DatagramNetworkInfo(socket, datagramPacket.getAddress(), datagramPacket.getPort()), Arrays.copyOf(datagramPacket.getData(), datagramPacket.getLength()));
    }

    public NetworkPacket(NetworkInfo networkInfo, byte[] data) throws UnknownPacketException {
        this.networkInfo = networkInfo;
        Packet wrapper = new Packet(data);
        PacketReader reader = new PacketReader(wrapper);
        reader.throwIfIndexAboveSize();
        try {
            int totalPackets;
            if (reader.getNextBoolean()) {
                this.identifier = reader.getNextInt();
                this.packetIndex = reader.getNextShortUnsigned();
                totalPackets = reader.getNextShortUnsigned();
            } else {
                this.identifier = 0;
                this.packetIndex = 0;
                totalPackets = 1;
            }
            if (this.packetIndex == 0) {
                this.type = reader.getNextShortUnsigned();
                if (this.type >= PacketRegistry.getTotalRegistered()) {
                    throw new UnknownPacketException("Unknown packet type: " + this.type);
                }
                this.timestamp = reader.getNextBoolean() ? reader.getNextLong() : 0L;
            } else {
                this.type = -1;
                this.timestamp = 0L;
            }
            this.packets = new PacketData[totalPackets];
            this.packets[this.packetIndex] = new PacketData(reader.getRemainingBytes(), data);
            this.complete = 1;
        }
        catch (IndexOutOfBoundsException e) {
            throw new UnknownPacketException("Read out of bounds", e);
        }
    }

    public NetworkPacket(NetworkPacket other) {
        this.networkInfo = other.networkInfo;
        this.type = other.type;
        this.timestamp = other.timestamp;
        this.identifier = other.identifier;
        this.packetIndex = other.packetIndex;
        this.packets = other.packets;
        this.complete = other.complete;
        this.packet = null;
    }

    public boolean canMerge(NetworkPacket packet) {
        return this.identifier != 0 && this.identifier == packet.identifier && Objects.equals(this.networkInfo, packet.networkInfo);
    }

    public NetworkPacket mergePackets(NetworkPacket packet) {
        if (!this.canMerge(packet)) {
            throw new IllegalArgumentException("Merge packet have different identifier");
        }
        if (this.packets[packet.packetIndex] == null && packet.packets[packet.packetIndex] != null) {
            ++this.complete;
            this.packets[packet.packetIndex] = packet.packets[packet.packetIndex];
        }
        if (packet.type != -1) {
            System.arraycopy(this.packets, 0, packet.packets, 0, this.packets.length);
            return packet;
        }
        return this;
    }

    public void sendPacket() throws IOException {
        if (!this.isComplete()) {
            throw new IllegalStateException("Cannot send incomplete packet");
        }
        for (PacketData packet : this.packets) {
            packet.send();
        }
    }

    public boolean isComplete() {
        return this.complete == this.packets.length;
    }

    public Packet getTypePacket() {
        if (this.packet != null) {
            return this.packet;
        }
        if (this.type == -1 || !this.isComplete()) {
            throw new IllegalStateException("Packet is part of a bigger one. Wait for remaining content");
        }
        byte[] allData = new byte[]{};
        for (PacketData data : this.packets) {
            int index = allData.length;
            allData = Arrays.copyOf(allData, allData.length + data.content.length);
            System.arraycopy(data.content, 0, allData, index, data.content.length);
        }
        try {
            this.packet = PacketRegistry.createPacket(this.type, allData);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NoSuchElementException e) {
            System.err.println("Could not find received packet with type 0x" + Integer.toHexString(this.type));
        }
        catch (InstantiationException e) {
            System.err.println("Could not instantiate packet " + PacketRegistry.getPacketSimpleName(this.type));
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
        catch (InvocationTargetException e) {
            System.err.println("Could not construct packet " + PacketRegistry.getPacketSimpleName(this.type));
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            } else {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            System.err.println("Unknown error creating packet " + PacketRegistry.getPacketSimpleName(this.type));
            e.printStackTrace();
        }
        return this.packet;
    }

    public int getCurrentByteSize() {
        int bytes = 0;
        for (PacketData packet : this.packets) {
            if (packet == null) continue;
            bytes += packet.packet.length;
        }
        return bytes;
    }

    public int getByteSize() {
        if (!this.isComplete()) {
            throw new IllegalStateException("Packet is not complete, use getCurrentByteSize");
        }
        return this.getCurrentByteSize();
    }

    public void processServer(Server server, ServerClient client) {
        Packet packet = this.getTypePacket();
        if (packet != null) {
            packet.processServer(this, server, client);
        }
    }

    public void processClient(Client client) {
        Packet packet = this.getTypePacket();
        if (packet != null) {
            packet.processClient(this, client);
        }
    }

    public String getInfoDisplayName() {
        if (this.networkInfo == null) {
            return "LOCAL";
        }
        return this.networkInfo.getDisplayName();
    }

    private class PacketData {
        private final byte[] content;
        private final byte[] packet;

        public PacketData(byte[] content, byte[] packet) {
            this.content = content;
            this.packet = packet;
        }

        public void send() throws IOException {
            NetworkPacket.this.networkInfo.send(this.packet);
        }
    }
}

