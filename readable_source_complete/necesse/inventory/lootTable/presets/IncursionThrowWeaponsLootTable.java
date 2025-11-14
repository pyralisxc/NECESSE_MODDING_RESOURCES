/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionThrowWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionThrowWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionThrowWeaponsLootTable instance = new IncursionThrowWeaponsLootTable();

    private IncursionThrowWeaponsLootTable() {
        super(new LootItemInterface[]{incursionThrowWeapons});
    }
}

