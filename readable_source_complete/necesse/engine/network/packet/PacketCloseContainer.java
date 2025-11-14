/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketCloseContainer
extends Packet {
    public PacketCloseContainer(byte[] data) {
        super(data);
    }

    public PacketCloseContainer() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.closeContainer(false);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.closeContainer(false);
    }
}

