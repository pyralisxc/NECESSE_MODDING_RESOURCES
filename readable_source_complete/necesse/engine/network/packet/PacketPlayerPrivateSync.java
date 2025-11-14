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

public class PacketPlayerPrivateSync
extends Packet {
    private final PacketReader reader;

    public PacketPlayerPrivateSync(byte[] data) {
        super(data);
        this.reader = new PacketReader(this);
    }

    public PacketPlayerPrivateSync(ServerClient client) {
        PacketWriter writer = new PacketWriter(this);
        this.reader = new PacketReader(writer);
        client.setupPrivateSyncUpdate(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getSlot() == -1) {
            return;
        }
        client.getClient().applyPrivateSyncPacket(this.reader);
    }
}

