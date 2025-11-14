/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementSettlerDietChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int mobUniqueID;
    public final ItemCategoriesFilterChange change;

    public SettlementSettlerDietChangedEvent(ServerSettlementData data, int mobUniqueID, ItemCategoriesFilterChange change) {
        this.settlementUniqueID = data.uniqueID;
        this.mobUniqueID = mobUniqueID;
        this.change = change;
    }

    public SettlementSettlerDietChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.change = ItemCategoriesFilterChange.fromPacket(reader);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.mobUniqueID);
        this.change.write(writer);
    }
}

