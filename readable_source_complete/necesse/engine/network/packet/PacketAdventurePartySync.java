/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketAdventurePartyRequestUpdate;
import necesse.engine.network.server.ServerClient;

public class PacketAdventurePartySync
extends Packet {
    public final int mobsHash;

    public PacketAdventurePartySync(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobsHash = reader.getNextInt();
    }

    public PacketAdventurePartySync(ServerClient client) {
        this.mobsHash = client.adventureParty.getMobsHash();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobsHash);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (this.mobsHash != client.adventureParty.getMobsHash()) {
            client.network.sendPacket(new PacketAdventurePartyRequestUpdate());
        }
    }
}

