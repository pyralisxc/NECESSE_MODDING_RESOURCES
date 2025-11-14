/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.horse;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;
import necesse.inventory.lootTable.presets.CosmeticSetArmorLootTable;

public class HorseMaskArmorItem
extends SetHelmetArmorItem {
    public HorseMaskArmorItem() {
        super(0, null, 0, CosmeticArmorLootTable.cosmeticArmor, CosmeticSetArmorLootTable.cosmeticSetArmor, Item.Rarity.RARE, "horsemask", "horsecostumeshirt", "horsecostumeboots", null);
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
        this.drawBodyPart = false;
    }
}

