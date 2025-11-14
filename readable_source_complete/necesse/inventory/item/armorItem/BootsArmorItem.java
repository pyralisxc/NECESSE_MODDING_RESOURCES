/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem;

import necesse.engine.localization.Localization;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class BootsArmorItem
extends ArmorItem {
    public BootsArmorItem(int armorValue, int enchantCost, String textureName, OneOfLootItems lootTableCategory) {
        super(ArmorItem.ArmorType.FEET, armorValue, enchantCost, lootTableCategory, textureName);
        this.tierOneEssencesUpgradeRequirement = "bioessence";
        this.tierTwoEssencesUpgradeRequirement = "spideressence";
    }

    public BootsArmorItem(int armorValue, int enchantCost, Item.Rarity itemRarity, String textureName, OneOfLootItems lootTableCategory) {
        this(armorValue, enchantCost, textureName, lootTableCategory);
        this.rarity = itemRarity;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "boots");
    }
}

