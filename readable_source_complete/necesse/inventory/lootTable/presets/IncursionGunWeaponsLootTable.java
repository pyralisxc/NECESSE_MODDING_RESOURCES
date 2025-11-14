/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class IncursionGunWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems incursionGunWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final IncursionGunWeaponsLootTable instance = new IncursionGunWeaponsLootTable();

    private IncursionGunWeaponsLootTable() {
        super(new LootItemInterface[]{incursionGunWeapons});
    }
}

