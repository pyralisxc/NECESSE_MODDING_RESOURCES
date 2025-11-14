/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketAdventurePartyUpdate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartyRequestUpdate
extends Packet {
    public PacketAdventurePartyRequestUpdate(byte[] data) {
        super(data);
    }

    public PacketAdventurePartyRequestUpdate() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.sendPacket(new PacketAdventurePartyUpdate(client));
    }
}

