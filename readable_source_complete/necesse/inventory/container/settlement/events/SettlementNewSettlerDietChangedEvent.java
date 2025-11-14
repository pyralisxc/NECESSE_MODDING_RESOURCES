/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementNewSettlerDietChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final ItemCategoriesFilterChange change;

    public SettlementNewSettlerDietChangedEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.change = ItemCategoriesFilterChange.fullChange(data.getNewSettlerDiet());
    }

    public SettlementNewSettlerDietChangedEvent(ServerSettlementData data, ItemCategoriesFilterChange change) {
        this.settlementUniqueID = data.uniqueID;
        this.change = change;
    }

    public SettlementNewSettlerDietChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.change = ItemCategoriesFilterChange.fromPacket(reader);
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        this.change.write(writer);
    }
}

