/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;

public class PacketNetworkUpdate
extends Packet {
    public final long totalInPackets;
    public final long totalOutPackets;

    public PacketNetworkUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.totalInPackets = reader.getNextLong();
        this.totalOutPackets = reader.getNextLong();
    }

    public PacketNetworkUpdate(ServerClient client) {
        this.totalInPackets = client.getPacketsInTotal();
        this.totalOutPackets = client.getPacketsOutTotal();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(this.totalInPackets);
        writer.putNextLong(this.totalOutPackets);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.packetManager.applyNetworkUpdate(this);
    }
}

