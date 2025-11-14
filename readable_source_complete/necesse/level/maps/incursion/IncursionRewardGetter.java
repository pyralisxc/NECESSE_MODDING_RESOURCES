/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.util.ArrayList;
import necesse.inventory.InventoryItem;

@FunctionalInterface
public interface IncursionRewardGetter {
    public ArrayList<InventoryItem> getRewards(boolean var1);
}

