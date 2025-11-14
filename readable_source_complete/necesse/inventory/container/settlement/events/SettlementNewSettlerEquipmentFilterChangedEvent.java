/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlementNewSettlerEquipmentFilterChangedEvent
extends ContainerEvent {
    public final int settlementUniqueID;
    public final boolean selfManageEquipment;
    public final boolean preferArmorSets;
    public final ItemCategoriesFilterChange change;

    public SettlementNewSettlerEquipmentFilterChangedEvent(ServerSettlementData data) {
        this.settlementUniqueID = data.uniqueID;
        this.selfManageEquipment = data.newSettlerSelfManageEquipment;
        this.preferArmorSets = data.newSettlerEquipmentPreferArmorSets;
        this.change = ItemCategoriesFilterChange.fullChange(data.getNewSettlerEquipmentFilter());
    }

    public SettlementNewSettlerEquipmentFilterChangedEvent(ServerSettlementData data, boolean selfManageEquipment, boolean preferArmorSets, ItemCategoriesFilterChange change) {
        this.settlementUniqueID = data.uniqueID;
        this.selfManageEquipment = selfManageEquipment;
        this.preferArmorSets = preferArmorSets;
        this.change = change;
    }

    public SettlementNewSettlerEquipmentFilterChangedEvent(PacketReader reader) {
        super(reader);
        this.settlementUniqueID = reader.getNextInt();
        this.selfManageEquipment = reader.getNextBoolean();
        this.preferArmorSets = reader.getNextBoolean();
        this.change = reader.getNextBoolean() ? ItemCategoriesFilterChange.fromPacket(reader) : null;
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.settlementUniqueID);
        writer.putNextBoolean(this.selfManageEquipment);
        writer.putNextBoolean(this.preferArmorSets);
        writer.putNextBoolean(this.change != null);
        if (this.change != null) {
            this.change.write(writer);
        }
    }
}

