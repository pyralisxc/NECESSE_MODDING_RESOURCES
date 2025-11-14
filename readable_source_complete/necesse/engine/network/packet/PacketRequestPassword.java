/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public class PacketRequestPassword
extends Packet {
    public final long worldUniqueID;

    public PacketRequestPassword(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.worldUniqueID = reader.getNextLong();
    }

    public PacketRequestPassword(Server server) {
        this.worldUniqueID = server.world.getUniqueID();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(this.worldUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.loading.connectingPhase.submitRequestPasswordPacket(this);
    }
}

