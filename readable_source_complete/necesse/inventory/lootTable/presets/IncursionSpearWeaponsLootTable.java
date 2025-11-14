/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionSpearWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionSpearWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionSpearWeaponsLootTable instance = new IncursionSpearWeaponsLootTable();

    private IncursionSpearWeaponsLootTable() {
        super(new LootItemInterface[]{incursionSpearWeapons});
    }
}

