/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketPerformanceResult
extends Packet {
    public final int uniqueID;
    public final String text;

    public PacketPerformanceResult(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.uniqueID = reader.getNextInt();
        this.text = reader.getNextStringLong();
    }

    public PacketPerformanceResult(int uniqueID, String text) {
        this.uniqueID = uniqueID;
        this.text = text;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(uniqueID);
        writer.putNextStringLong(text);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.performanceDumpCache.submitServerDump(this.uniqueID, this.text);
    }
}

