/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionGreatbowWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionGreatbowWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionGreatbowWeaponsLootTable instance = new IncursionGreatbowWeaponsLootTable();

    private IncursionGreatbowWeaponsLootTable() {
        super(new LootItemInterface[]{incursionGreatbowWeapons});
    }
}

