/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;

public class SettlementWorkZoneChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final SettlementWorkZone zone;

    public SettlementWorkZoneChangedEvent(ServerSettlementData data, SettlementWorkZone zone) {
        this.settlementUniqueID = data.uniqueID;
        this.zone = zone;
    }

    public SettlementWorkZoneChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        int zoneID = reader.getNextShortUnsigned();
        int uniqueID = reader.getNextInt();
        this.zone = SettlementWorkZoneRegistry.getNewZone(zoneID);
        this.zone.setUniqueID(uniqueID);
        this.zone.applyPacket(reader);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextShortUnsigned(this.zone.getID());
        writer.putNextInt(this.zone.getUniqueID());
        this.zone.writePacket(writer);
    }
}

