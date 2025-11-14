/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.space;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class SpaceSuitArmorItem
extends ChestArmorItem {
    public SpaceSuitArmorItem() {
        super(0, 0, Item.Rarity.COMMON, "spacesuit", "spacesuitarms", CosmeticArmorLootTable.cosmeticArmor);
    }
}

