/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.seasons;

import java.util.ArrayList;
import java.util.function.Supplier;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;

public class SeasonalMobLootTable {
    public Supplier<Boolean> isActive;
    public LootTable lootTable;

    public SeasonalMobLootTable(Supplier<Boolean> isActive, LootTable lootTable) {
        this.isActive = isActive;
        this.lootTable = lootTable;
    }

    public void addDrops(Mob mob, ArrayList<InventoryItem> drops, GameRandom random, float lootMultiplier) {
        this.lootTable.addItems(drops, random, lootMultiplier, mob);
    }
}

