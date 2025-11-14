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

public class PacketAdventurePartyUpdate
extends Packet {
    private final PacketReader reader;

    public PacketAdventurePartyUpdate(byte[] data) {
        super(data);
        this.reader = new PacketReader(this);
    }

    public PacketAdventurePartyUpdate(ServerClient client) {
        PacketWriter writer = new PacketWriter(this);
        this.reader = new PacketReader(writer);
        client.adventureParty.writeUpdatePacket(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.adventureParty.readUpdatePacket(new PacketReader(this.reader));
        client.adventureParty.updateMobsFromLevel(client.getLevel());
    }
}

