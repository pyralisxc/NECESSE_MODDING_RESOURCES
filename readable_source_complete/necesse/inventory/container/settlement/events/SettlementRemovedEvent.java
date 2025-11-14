/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class SettlementRemovedEvent
extends ContainerEvent {
    public final int settlementUniqueID;

    public SettlementRemovedEvent(int settlementUniqueID) {
        this.settlementUniqueID = settlementUniqueID;
    }

    public SettlementRemovedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
    }
}

