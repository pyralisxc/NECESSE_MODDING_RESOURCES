/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementSettlerRestrictZoneChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int mobUniqueID;
    public final int restrictZoneUniqueID;

    public SettlementSettlerRestrictZoneChangedEvent(ServerSettlementData data, LevelSettler settler) {
        this.settlementUniqueID = data.uniqueID;
        this.mobUniqueID = settler.mobUniqueID;
        this.restrictZoneUniqueID = settler.getRestrictZoneUniqueID();
    }

    public SettlementSettlerRestrictZoneChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.restrictZoneUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextInt(this.restrictZoneUniqueID);
    }
}

