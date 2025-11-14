/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketClientStats;

public class PacketRequestClientStats
extends Packet {
    public PacketRequestClientStats(byte[] data) {
        super(data);
    }

    public PacketRequestClientStats() {
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.network.sendPacket(new PacketClientStats(GlobalData.stats(), GlobalData.achievements()));
    }
}

