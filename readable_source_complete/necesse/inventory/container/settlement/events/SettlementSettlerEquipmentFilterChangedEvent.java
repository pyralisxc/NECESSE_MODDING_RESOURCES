/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementSettlerEquipmentFilterChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final int mobUniqueID;
    public final boolean preferArmorSets;
    public final ItemCategoriesFilterChange change;

    public SettlementSettlerEquipmentFilterChangedEvent(ServerSettlementData data, int mobUniqueID, boolean preferArmorSets, ItemCategoriesFilterChange change) {
        this.settlementUniqueID = data.uniqueID;
        this.mobUniqueID = mobUniqueID;
        this.change = change;
        this.preferArmorSets = preferArmorSets;
    }

    public SettlementSettlerEquipmentFilterChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.preferArmorSets = reader.getNextBoolean();
        this.change = reader.getNextBoolean() ? ItemCategoriesFilterChange.fromPacket(reader) : null;
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextBoolean(this.preferArmorSets);
        writer.putNextBoolean(this.change != null);
        if (this.change != null) {
            this.change.write(writer);
        }
    }
}

