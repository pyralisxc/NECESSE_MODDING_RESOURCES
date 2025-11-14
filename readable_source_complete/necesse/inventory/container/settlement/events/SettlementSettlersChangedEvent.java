/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementSettlersChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;

    public SettlementSettlersChangedEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
    }

    public SettlementSettlersChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
    }
}

