/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketQuests;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestQuests
extends Packet {
    public PacketRequestQuests(byte[] data) {
        super(data);
    }

    public PacketRequestQuests() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        server.network.sendPacket((Packet)new PacketQuests(client.getQuests()), client);
    }
}

