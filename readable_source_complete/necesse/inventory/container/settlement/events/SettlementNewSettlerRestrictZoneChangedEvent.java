/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementNewSettlerRestrictZoneChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int restrictZoneUniqueID;

    public SettlementNewSettlerRestrictZoneChangedEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.restrictZoneUniqueID = data.getNewSettlerRestrictZoneUniqueID();
    }

    public SettlementNewSettlerRestrictZoneChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.restrictZoneUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.restrictZoneUniqueID);
    }
}

