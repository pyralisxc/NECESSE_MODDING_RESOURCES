/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.iron;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class IronHelmetArmorItem
extends SetHelmetArmorItem {
    public IronHelmetArmorItem() {
        super(2, DamageTypeRegistry.MELEE, 300, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.NORMAL, "ironhelmet", "ironchestplate", "ironboots", "ironsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
        this.canBeUsedForRaids = true;
    }
}

