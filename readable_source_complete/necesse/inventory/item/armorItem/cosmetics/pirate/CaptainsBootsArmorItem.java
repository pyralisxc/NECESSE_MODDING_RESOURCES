/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.pirate;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.BootsArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;

public class CaptainsBootsArmorItem
extends BootsArmorItem {
    public CaptainsBootsArmorItem() {
        super(0, 0, Item.Rarity.UNCOMMON, "captainsboots", CosmeticArmorLootTable.cosmeticArmor);
        this.drawBodyPart = false;
    }
}

