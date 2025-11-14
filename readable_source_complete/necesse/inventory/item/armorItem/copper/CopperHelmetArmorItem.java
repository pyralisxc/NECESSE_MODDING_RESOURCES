/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.copper;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class CopperHelmetArmorItem
extends SetHelmetArmorItem {
    public CopperHelmetArmorItem() {
        super(1, DamageTypeRegistry.MELEE, 200, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.NORMAL, "copperhelmet", "copperchestplate", "copperboots", "coppersetbonus");
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.canBeUsedForRaids = true;
    }
}

