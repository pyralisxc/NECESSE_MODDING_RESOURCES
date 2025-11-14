/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.armorItem.cosmetics.misc;

import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;

public class EmpressMaskArmorItem
extends SetHelmetArmorItem {
    public EmpressMaskArmorItem() {
        super(0, null, 0, null, null, Item.Rarity.COMMON, "empressmask", null, null, null);
        this.hairDrawOptions = ArmorItem.HairDrawMode.OVER_HAIR;
    }
}

