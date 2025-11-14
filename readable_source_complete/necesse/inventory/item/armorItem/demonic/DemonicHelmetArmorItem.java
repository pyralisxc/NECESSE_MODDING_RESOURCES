/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.demonic;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class DemonicHelmetArmorItem
extends SetHelmetArmorItem {
    public DemonicHelmetArmorItem() {
        super(6, DamageTypeRegistry.MELEE, 650, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.COMMON, "demonichelmet", "demonicchestplate", "demonicboots", "demonicsetbonus");
        this.canBeUsedForRaids = true;
    }
}

