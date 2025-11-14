/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.dlc.DLCProvider;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketClientInstalledDLC;

public class PacketRequestClientInstalledDLC
extends Packet {
    public PacketRequestClientInstalledDLC(byte[] data) {
        super(data);
    }

    public PacketRequestClientInstalledDLC() {
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.network.sendPacket(new PacketClientInstalledDLC(client.getSlot(), DLCProvider.getInstalledDLCs()));
    }
}

