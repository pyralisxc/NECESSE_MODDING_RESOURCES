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

public class PacketPlayerDie
extends Packet {
    public final int slot;
    public final int respawnTime;

    public PacketPlayerDie(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.respawnTime = reader.getNextInt();
    }

    public PacketPlayerDie(int slot, int respawnTime) {
        this.slot = slot;
        this.respawnTime = respawnTime;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(respawnTime);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient c = client.getClient(this.slot);
        if (c != null) {
            c.die(this.respawnTime);
        }
    }
}

