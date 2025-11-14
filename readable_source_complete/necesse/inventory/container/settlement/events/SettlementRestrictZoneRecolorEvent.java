/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementRestrictZoneRecolorEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int restrictZoneUniqueID;
    public final int hue;

    public SettlementRestrictZoneRecolorEvent(ServerSettlementData data, RestrictZone zone) {
        this.settlementUniqueID = data.uniqueID;
        this.restrictZoneUniqueID = zone.uniqueID;
        this.hue = zone.colorHue;
    }

    public SettlementRestrictZoneRecolorEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.restrictZoneUniqueID = reader.getNextInt();
        this.hue = reader.getNextMaxValue(360);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.restrictZoneUniqueID);
        writer.putNextMaxValue(this.hue, 360);
    }
}

