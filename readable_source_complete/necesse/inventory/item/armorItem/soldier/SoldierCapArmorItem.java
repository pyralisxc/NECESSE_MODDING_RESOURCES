/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.soldier;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class SoldierCapArmorItem
extends SetHelmetArmorItem {
    public SoldierCapArmorItem() {
        super(3, DamageTypeRegistry.RANGED, 375, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.COMMON, "soldiercap", "soldierchestplate", "soldierboots", "soldiercapsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
        this.canBeUsedForRaids = true;
    }
}

