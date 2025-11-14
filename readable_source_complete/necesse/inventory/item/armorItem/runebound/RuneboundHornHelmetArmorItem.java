/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.runebound;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.CosmeticArmorLootTable;
import necesse.inventory.lootTable.presets.CosmeticSetArmorLootTable;

public class RuneboundHornHelmetArmorItem
extends SetHelmetArmorItem {
    public RuneboundHornHelmetArmorItem() {
        super(0, null, 0, CosmeticArmorLootTable.cosmeticArmor, CosmeticSetArmorLootTable.cosmeticSetArmor, Item.Rarity.COMMON, "runeboundhornhelmet", "runeboundleatherchest", "runeboundboots", null);
        this.hairDrawOptions = ArmorItem.HairDrawMode.NO_HEAD;
    }
}

