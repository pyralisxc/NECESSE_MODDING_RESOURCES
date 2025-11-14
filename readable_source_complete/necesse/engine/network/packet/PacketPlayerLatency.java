/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;

public class PacketPlayerLatency
extends Packet {
    public int slot;
    public int latency;

    public PacketPlayerLatency(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.latency = reader.getNextShortUnsigned();
    }

    public PacketPlayerLatency(int slot, int latency) {
        this.slot = slot;
        this.latency = latency;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextShortUnsigned(latency);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (target != null) {
            target.latency = this.latency;
        }
    }
}

