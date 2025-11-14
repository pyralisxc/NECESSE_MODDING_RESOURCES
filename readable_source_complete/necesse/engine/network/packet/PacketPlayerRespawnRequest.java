/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerRespawnRequest
extends Packet {
    public PacketPlayerRespawnRequest(byte[] data) {
        super(data);
    }

    public PacketPlayerRespawnRequest() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.respawn();
    }
}

