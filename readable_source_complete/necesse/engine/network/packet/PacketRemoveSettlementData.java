/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.world.worldData.SettlementsWorldData;

public class PacketRemoveSettlementData
extends Packet {
    public final int settlementUniqueID;

    public PacketRemoveSettlementData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.settlementUniqueID = reader.getNextInt();
    }

    public PacketRemoveSettlementData(int settlementUniqueID) {
        this.settlementUniqueID = settlementUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(settlementUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        SettlementsWorldData settlements = SettlementsWorldData.getSettlementsData(client);
        settlements.unloadSettlement(this.settlementUniqueID, true);
        settlements.submitSettlementRequestFulfilled(this.settlementUniqueID);
    }
}

