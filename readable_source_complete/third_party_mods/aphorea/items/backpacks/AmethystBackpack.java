/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.backpacks;

import aphorea.items.backpacks.AphBackpack;
import necesse.inventory.item.Item;

public class AmethystBackpack
extends AphBackpack {
    public AmethystBackpack() {
        this.rarity = Item.Rarity.COMMON;
    }

    public int getInternalInventorySize() {
        return 10;
    }
}

