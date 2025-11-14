/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.RestrictZone;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementRestrictZoneRenameEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int restrictZoneUniqueID;
    public final GameMessage name;

    public SettlementRestrictZoneRenameEvent(ServerSettlementData data, RestrictZone zone) {
        this.settlementUniqueID = data.uniqueID;
        this.restrictZoneUniqueID = zone.uniqueID;
        this.name = zone.name;
    }

    public SettlementRestrictZoneRenameEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.restrictZoneUniqueID = reader.getNextInt();
        this.name = GameMessage.fromPacket(reader);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.restrictZoneUniqueID);
        this.name.writePacket(writer);
    }
}

