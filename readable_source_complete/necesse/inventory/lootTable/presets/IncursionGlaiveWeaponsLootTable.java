/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionGlaiveWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionGlaiveWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionGlaiveWeaponsLootTable instance = new IncursionGlaiveWeaponsLootTable();

    private IncursionGlaiveWeaponsLootTable() {
        super(new LootItemInterface[]{incursionGlaiveWeapons});
    }
}

