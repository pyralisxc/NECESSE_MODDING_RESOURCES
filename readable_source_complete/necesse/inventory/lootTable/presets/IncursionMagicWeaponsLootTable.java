/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionMagicWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionMagicWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionMagicWeaponsLootTable instance = new IncursionMagicWeaponsLootTable();

    private IncursionMagicWeaponsLootTable() {
        super(new LootItemInterface[]{incursionMagicWeapons});
    }
}

