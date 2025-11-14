/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerEquipmentFilterData
extends SettlementSettlerData {
    public boolean preferArmorSets;
    public final ItemCategoriesFilter equipmentFilter = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);

    public SettlementSettlerEquipmentFilterData(LevelSettler settler) {
        super(settler);
        this.preferArmorSets = settler.preferArmorSets;
        this.equipmentFilter.loadFromCopy(settler.equipmentFilter);
    }

    public SettlementSettlerEquipmentFilterData(PacketReader reader) {
        super(reader);
        this.preferArmorSets = reader.getNextBoolean();
        this.equipmentFilter.readPacket(reader);
    }

    @Override
    public void writeContentPacket(PacketWriter writer) {
        super.writeContentPacket(writer);
        writer.putNextBoolean(this.preferArmorSets);
        this.equipmentFilter.writePacket(writer);
    }
}

