/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionBowWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionBowWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionBowWeaponsLootTable instance = new IncursionBowWeaponsLootTable();

    private IncursionBowWeaponsLootTable() {
        super(new LootItemInterface[]{incursionBowWeapons});
    }
}

