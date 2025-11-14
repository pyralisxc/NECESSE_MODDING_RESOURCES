/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.settler.FoodQuality;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class SettlementStorageFoodQualityIndex
extends SettlementStorageIndex {
    protected HashMap<FoodQuality, SettlementStorageRecordsRegionData> regions = new HashMap();
    protected int totalItems;
    protected int totalNutrition;

    public SettlementStorageFoodQualityIndex(Level level) {
        super(level);
    }

    @Override
    public void clear() {
        this.regions.clear();
    }

    protected SettlementStorageRecordsRegionData getRegionData(FoodQuality quality) {
        return this.regions.compute(quality, (id, last) -> {
            if (last == null) {
                return new SettlementStorageRecordsRegionData(this, item -> item.item.isFoodItem() && ((FoodConsumableItem)item.item).quality == quality);
            }
            return last;
        });
    }

    @Override
    public void add(InventoryItem inventoryItem, SettlementStorageRecord record) {
        if (inventoryItem.item.isFoodItem()) {
            FoodConsumableItem foodItem = (FoodConsumableItem)inventoryItem.item;
            this.totalNutrition += foodItem.nutrition * inventoryItem.getAmount();
            this.totalItems += inventoryItem.getAmount();
            this.getRegionData(foodItem.quality).add(record);
        }
    }

    public SettlementStorageRecordsRegionData getFoodQuality(FoodQuality quality) {
        return this.regions.get(quality);
    }

    public int getTotalNutrition() {
        return this.totalNutrition;
    }

    public int getTotalItems() {
        return this.totalItems;
    }
}

