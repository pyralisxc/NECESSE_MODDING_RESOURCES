/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementForestryZone;

public class SettlementForestryZoneUpdateEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int zoneUniqueID;
    public final boolean choppingAllowed;
    public final boolean replantChoppedDownTrees;
    public final int autoPlantSaplingID;

    public SettlementForestryZoneUpdateEvent(ServerSettlementData data, SettlementForestryZone zone) {
        this.settlementUniqueID = data.uniqueID;
        this.zoneUniqueID = zone.getUniqueID();
        this.choppingAllowed = zone.isChoppingAllowed();
        this.replantChoppedDownTrees = zone.replantChoppedDownTrees();
        this.autoPlantSaplingID = zone.getAutoPlantSaplingID();
    }

    public SettlementForestryZoneUpdateEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.zoneUniqueID = reader.getNextInt();
        this.choppingAllowed = reader.getNextBoolean();
        this.replantChoppedDownTrees = reader.getNextBoolean();
        this.autoPlantSaplingID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.zoneUniqueID);
        writer.putNextBoolean(this.choppingAllowed);
        writer.putNextBoolean(this.replantChoppedDownTrees);
        writer.putNextInt(this.autoPlantSaplingID);
    }
}

