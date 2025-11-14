/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestPlayerData;

public class PacketNeedRequestSelf
extends Packet {
    public PacketNeedRequestSelf(byte[] data) {
        super(data);
    }

    public PacketNeedRequestSelf() {
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getSlot() == -1) {
            return;
        }
        client.network.sendPacket(new PacketRequestPlayerData(client.getSlot()));
    }
}

