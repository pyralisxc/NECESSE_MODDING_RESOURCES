/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.tungsten;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class TungstenHelmetArmorItem
extends SetHelmetArmorItem {
    public TungstenHelmetArmorItem() {
        super(24, DamageTypeRegistry.MELEE, 1300, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.UNCOMMON, "tungstenhelmet", "tungstenchestplate", "tungstenboots", "tungstensetbonus");
        this.canBeUsedForRaids = true;
    }
}

