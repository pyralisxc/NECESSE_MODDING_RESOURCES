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
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerSync
extends Packet {
    public final int slot;
    private final PacketReader reader;

    public PacketPlayerSync(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.reader = reader;
    }

    public PacketPlayerSync(ServerClient client, ServerClient receiver) {
        this.slot = client.slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        this.reader = new PacketReader(writer);
        client.setupSyncUpdate(writer, receiver);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (target != null) {
            target.applySyncPacket(this.reader);
        } else {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        }
    }
}

