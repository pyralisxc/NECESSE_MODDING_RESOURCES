/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementHusbandryZone;

public class SettlementHusbandryZoneUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int zoneUniqueID;
    public final int maxAnimalsBeforeSlaughter;
    public final float slaughterMaleRatio;

    public SettlementHusbandryZoneUpdateEvent(ServerSettlementData data, SettlementHusbandryZone zone) {
        this.settlementUniqueID = data.uniqueID;
        this.zoneUniqueID = zone.getUniqueID();
        this.maxAnimalsBeforeSlaughter = zone.getMaxAnimalsBeforeSlaughter();
        this.slaughterMaleRatio = zone.getSlaughterMaleRatio();
    }

    public SettlementHusbandryZoneUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.zoneUniqueID = reader.getNextInt();
        this.maxAnimalsBeforeSlaughter = reader.getNextInt();
        this.slaughterMaleRatio = reader.getNextFloat();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.zoneUniqueID);
        writer.putNextInt(this.maxAnimalsBeforeSlaughter);
        writer.putNextFloat(this.slaughterMaleRatio);
    }
}

