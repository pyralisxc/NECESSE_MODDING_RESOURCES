/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;

public class PacketStartCredits
extends Packet {
    public PacketStartCredits(byte[] data) {
        super(data);
    }

    public PacketStartCredits() {
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.startCreditsDraw();
    }
}

