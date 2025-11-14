/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import necesse.inventory.InventoryItem;

public interface FeedingTroughMob {
    public InventoryItem onFed(InventoryItem var1);

    public boolean canFeed(InventoryItem var1);

    public boolean isOnFeedCooldown();
}

