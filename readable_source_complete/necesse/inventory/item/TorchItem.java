/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public interface TorchItem {
    public boolean canPlaceTorch(Level var1, int var2, int var3, InventoryItem var4, PlayerMob var5);

    public int getTorchPlaceRange(Level var1, InventoryItem var2, PlayerMob var3);
}

