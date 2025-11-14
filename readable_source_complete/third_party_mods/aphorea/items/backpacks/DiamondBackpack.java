/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.backpacks;

import aphorea.items.backpacks.AphBackpack;
import necesse.inventory.item.Item;

public class DiamondBackpack
extends AphBackpack {
    public DiamondBackpack() {
        this.rarity = Item.Rarity.EPIC;
    }

    public int getInternalInventorySize() {
        return 16;
    }
}

