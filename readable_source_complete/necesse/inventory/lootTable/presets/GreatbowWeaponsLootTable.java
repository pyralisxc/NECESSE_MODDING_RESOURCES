/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class GreatbowWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems greatbowWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final GreatbowWeaponsLootTable instance = new GreatbowWeaponsLootTable();

    private GreatbowWeaponsLootTable() {
        super(new LootItemInterface[]{greatbowWeapons});
    }
}

