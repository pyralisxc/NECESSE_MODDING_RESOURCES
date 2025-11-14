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
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public class PacketSettlementData
extends Packet {
    public final int levelIdentifierHashCode;
    public final int settlementUniqueID;
    public final boolean isFull;
    public final Packet content;

    public PacketSettlementData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.settlementUniqueID = reader.getNextInt();
        this.isFull = reader.getNextBoolean();
        this.content = reader.getNextContentPacket();
    }

    public PacketSettlementData(NetworkSettlementData settlementData, boolean isFull) {
        this.levelIdentifierHashCode = settlementData.level.getIdentifierHashCode();
        this.settlementUniqueID = settlementData.uniqueID;
        this.isFull = isFull;
        this.content = new Packet();
        settlementData.writePacket(new PacketWriter(this.content), isFull);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextBoolean(isFull);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        Level level = client.levelManager.getLevel();
        SettlementsWorldData settlements = SettlementsWorldData.getSettlementsData(client);
        settlements.applyNetworkDataPacket(level, this.settlementUniqueID, new PacketReader(this.content), this.isFull);
    }
}

