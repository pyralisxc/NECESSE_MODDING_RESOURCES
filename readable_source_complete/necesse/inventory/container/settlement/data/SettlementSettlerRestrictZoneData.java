/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerRestrictZoneData
extends SettlementSettlerData {
    public int restrictZoneUniqueID;

    public SettlementSettlerRestrictZoneData(LevelSettler settler) {
        super(settler);
        this.restrictZoneUniqueID = settler.getRestrictZoneUniqueID();
    }

    public SettlementSettlerRestrictZoneData(PacketReader reader) {
        super(reader);
        this.restrictZoneUniqueID = reader.getNextInt();
    }

    @Override
    public void writeContentPacket(PacketWriter writer) {
        super.writeContentPacket(writer);
        writer.putNextInt(this.restrictZoneUniqueID);
    }
}

