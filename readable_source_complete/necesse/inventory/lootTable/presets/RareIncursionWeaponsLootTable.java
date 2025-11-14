/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class RareIncursionWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems rareIncursionWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final RareIncursionWeaponsLootTable instance = new RareIncursionWeaponsLootTable();

    private RareIncursionWeaponsLootTable() {
        super(new LootItemInterface[]{rareIncursionWeapons});
    }
}

