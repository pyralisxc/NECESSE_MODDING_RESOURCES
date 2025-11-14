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

public class SettlementSettlerDietsData
extends SettlementSettlerData {
    public final ItemCategoriesFilter dietFilter = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);

    public SettlementSettlerDietsData(LevelSettler settler) {
        super(settler);
        this.dietFilter.loadFromCopy(settler.dietFilter);
    }

    public SettlementSettlerDietsData(PacketReader reader) {
        super(reader);
        this.dietFilter.readPacket(reader);
    }

    @Override
    public void writeContentPacket(PacketWriter writer) {
        super.writeContentPacket(writer);
        this.dietFilter.writePacket(writer);
    }
}

