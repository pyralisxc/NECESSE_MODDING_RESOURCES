/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.lootItem;

import java.util.List;
import necesse.engine.util.GameRandom;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;

public class LootItemMultiplierIgnored
implements LootItemInterface {
    protected LootItemInterface child;

    public LootItemMultiplierIgnored(LootItemInterface child) {
        this.child = child;
    }

    @Override
    public void addPossibleLoot(LootList list, Object ... extra) {
        this.child.addPossibleLoot(list, extra);
    }

    @Override
    public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
        this.child.addItems(list, random, 1.0f, extra);
    }
}

