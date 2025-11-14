/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory;

import necesse.entity.mobs.PlayerMob;
import necesse.inventory.PlayerTempInventory;

@FunctionalInterface
public interface PlayerTempInventoryConstructor {
    public PlayerTempInventory create(PlayerMob var1, int var2, int var3);
}

