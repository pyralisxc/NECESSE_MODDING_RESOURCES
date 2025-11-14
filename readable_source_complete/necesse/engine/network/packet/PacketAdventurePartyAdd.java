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
import necesse.engine.network.server.AdventureParty;

public class PacketAdventurePartyAdd
extends Packet {
    public final int mobUniqueID;
    public final int mobsHash;

    public PacketAdventurePartyAdd(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.mobsHash = reader.getNextInt();
    }

    public PacketAdventurePartyAdd(AdventureParty party, int mobUniqueID) {
        this.mobUniqueID = mobUniqueID;
        this.mobsHash = party.getMobsHash();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(mobUniqueID);
        writer.putNextInt(this.mobsHash);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.adventureParty.clientAdd(this.mobUniqueID);
        if (this.mobsHash != client.adventureParty.getMobsHash()) {
            client.network.sendPacket(new PacketAdventurePartyRequestUpdate());
        }
    }
}

