/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.ZoningChange;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementRestrictZoneChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int restrictZoneUniqueID;
    public final ZoningChange change;

    public SettlementRestrictZoneChangedEvent(ServerSettlementData data, int restrictZoneUniqueID, ZoningChange change) {
        this.settlementUniqueID = data.uniqueID;
        this.restrictZoneUniqueID = restrictZoneUniqueID;
        this.change = change;
    }

    public SettlementRestrictZoneChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.restrictZoneUniqueID = reader.getNextInt();
        this.change = ZoningChange.fromPacket(reader);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.restrictZoneUniqueID);
        this.change.write(writer);
    }
}

