/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.backpacks;

import aphorea.items.backpacks.AphBackpack;
import necesse.inventory.item.Item;

public class EmeraldBackpack
extends AphBackpack {
    public EmeraldBackpack() {
        this.rarity = Item.Rarity.RARE;
    }

    public int getInternalInventorySize() {
        return 14;
    }
}

