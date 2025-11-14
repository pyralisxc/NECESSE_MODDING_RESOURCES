/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestSettlementData;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationManager;

public class PacketSettlementNotificationFull
extends Packet {
    public final int settlementUniqueID;
    public final Packet content;

    public PacketSettlementNotificationFull(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.settlementUniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketSettlementNotificationFull(SettlementNotificationManager manager) {
        this.settlementUniqueID = manager.settlement.uniqueID;
        this.content = new Packet();
        manager.writeFullPacket(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        NetworkSettlementData settlement = SettlementsWorldData.getSettlementsData(client).getNetworkData(this.settlementUniqueID);
        if (settlement != null) {
            settlement.notifications.readFullPacket(new PacketReader(this.content));
        } else {
            client.network.sendPacket(new PacketRequestSettlementData(this.settlementUniqueID));
        }
    }
}

