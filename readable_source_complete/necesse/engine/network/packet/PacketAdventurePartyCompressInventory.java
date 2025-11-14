/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartyCompressInventory
extends Packet {
    public PacketAdventurePartyCompressInventory(byte[] data) {
        super(data);
    }

    public PacketAdventurePartyCompressInventory() {
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.playerMob.getInv().party.compressItems();
    }
}

