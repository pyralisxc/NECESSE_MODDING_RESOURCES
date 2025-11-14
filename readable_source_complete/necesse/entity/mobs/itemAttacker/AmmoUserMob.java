/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.inventory.item.Item;

public interface AmmoUserMob {
    public int getAvailableArrows(String var1);

    public int getAvailableBullets(String var1);

    public int getAvailableAmmo(Item[] var1, String var2);

    public Item getFirstAvailableArrow(String var1);

    public Item getFirstAvailableBullet(String var1);

    public Item getFirstAvailableAmmo(Item[] var1, String var2);

    public AmmoConsumed removeAmmo(Item var1, int var2, String var3);
}

