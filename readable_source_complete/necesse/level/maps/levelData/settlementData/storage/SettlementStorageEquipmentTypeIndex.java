/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import java.util.HashMap;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class SettlementStorageEquipmentTypeIndex
extends SettlementStorageIndex {
    private static final int[] armorTypeToEquipmentTypeOrdinal = new int[ArmorItem.ArmorType.values().length];
    protected HashMap<EquipmentType, SettlementStorageRecordsRegionData> regions = new HashMap();

    public static EquipmentType armorTypeToEquipmentType(ArmorItem.ArmorType armorType) {
        return EquipmentType.values()[armorTypeToEquipmentTypeOrdinal[armorType.ordinal()]];
    }

    public SettlementStorageEquipmentTypeIndex(Level level) {
        super(level);
    }

    @Override
    public void clear() {
        this.regions.clear();
    }

    protected SettlementStorageRecordsRegionData getRegionData(EquipmentType type) {
        return this.regions.compute(type, (id, last) -> {
            if (last == null) {
                if (type == EquipmentType.WEAPON) {
                    return new SettlementStorageRecordsRegionData(this, item -> item.item instanceof ItemAttackerWeaponItem);
                }
                return new SettlementStorageRecordsRegionData(this, item -> item.item.isArmorItem() && ((ArmorItem)item.item).armorType == type.armorType);
            }
            return last;
        });
    }

    @Override
    public void add(InventoryItem inventoryItem, SettlementStorageRecord record) {
        if (inventoryItem.item.isArmorItem()) {
            ArmorItem armorItem = (ArmorItem)inventoryItem.item;
            if (!armorItem.isCosmetic) {
                this.getRegionData(SettlementStorageEquipmentTypeIndex.armorTypeToEquipmentType(armorItem.armorType)).add(record);
            }
        } else if (inventoryItem.item instanceof ItemAttackerWeaponItem) {
            this.getRegionData(EquipmentType.WEAPON).add(record);
        }
    }

    public SettlementStorageRecordsRegionData getEquipmentType(EquipmentType type) {
        return this.regions.get((Object)type);
    }

    static {
        for (EquipmentType value : EquipmentType.values()) {
            if (value.armorType == null) continue;
            SettlementStorageEquipmentTypeIndex.armorTypeToEquipmentTypeOrdinal[value.armorType.ordinal()] = value.ordinal();
        }
    }

    public static enum EquipmentType {
        WEAPON(null),
        HEAD(ArmorItem.ArmorType.HEAD),
        CHEST(ArmorItem.ArmorType.CHEST),
        FEET(ArmorItem.ArmorType.FEET);

        public final ArmorItem.ArmorType armorType;

        private EquipmentType(ArmorItem.ArmorType armorType) {
            this.armorType = armorType;
        }
    }
}

