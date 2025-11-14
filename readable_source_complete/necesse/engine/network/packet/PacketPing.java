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
import necesse.engine.network.server.ServerClient;

public class PacketPing
extends Packet {
    public int responseKey;

    public PacketPing(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.responseKey = reader.getNextInt();
    }

    public PacketPing(int responseKey) {
        this.responseKey = responseKey;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(responseKey);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.submitPingPacket(this);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.submitPingPacket(this);
    }
}

