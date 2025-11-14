/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementWorkZoneRemovedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int zoneUniqueID;

    public SettlementWorkZoneRemovedEvent(ServerSettlementData data, int zoneUniqueID) {
        this.settlementUniqueID = data.uniqueID;
        this.zoneUniqueID = zoneUniqueID;
    }

    public SettlementWorkZoneRemovedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.zoneUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.zoneUniqueID);
    }
}

