/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.gold;

import necesse.engine.registries.DamageTypeRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public class GoldHelmetArmorItem
extends SetHelmetArmorItem {
    public GoldHelmetArmorItem() {
        super(4, DamageTypeRegistry.MELEE, 350, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, Item.Rarity.NORMAL, "goldhelmet", "goldchestplate", "goldboots", "goldsetbonus");
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HAIR;
        this.facialFeatureDrawOptions = ArmorItem.FacialFeatureDrawMode.OVER_FACIAL_FEATURE;
        this.canBeUsedForRaids = true;
    }
}

