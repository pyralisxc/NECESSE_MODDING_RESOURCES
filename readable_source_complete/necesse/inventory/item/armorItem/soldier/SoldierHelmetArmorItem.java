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

public class SoldierHelmetArmorItem
extends SetHelmetArmorItem {
    public SoldierHelmetArmorItem() {
        super(4, DamageTypeRegistry.MELEE, 375, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.COMMON, "soldierhelmet", "soldierchestplate", "soldierboots", "soldierhelmetsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
        this.canBeUsedForRaids = true;
    }
}

