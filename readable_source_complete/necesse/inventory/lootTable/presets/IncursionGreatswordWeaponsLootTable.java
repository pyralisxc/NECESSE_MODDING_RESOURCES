/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionGreatswordWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionGreatswordWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionGreatswordWeaponsLootTable instance = new IncursionGreatswordWeaponsLootTable();

    private IncursionGreatswordWeaponsLootTable() {
        super(new LootItemInterface[]{incursionGreatswordWeapons});
    }
}

