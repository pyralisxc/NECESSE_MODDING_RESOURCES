/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementWorkZoneNameEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int zoneUniqueID;
    public final GameMessage name;

    public SettlementWorkZoneNameEvent(ServerSettlementData data, SettlementWorkZone zone) {
        this.settlementUniqueID = data.uniqueID;
        this.zoneUniqueID = zone.getUniqueID();
        this.name = zone.getName();
    }

    public SettlementWorkZoneNameEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.zoneUniqueID = reader.getNextInt();
        this.name = GameMessage.fromPacket(reader);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.zoneUniqueID);
        this.name.writePacket(writer);
    }
}

