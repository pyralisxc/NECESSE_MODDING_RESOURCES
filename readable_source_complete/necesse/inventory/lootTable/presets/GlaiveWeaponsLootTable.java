/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class GlaiveWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems glaiveWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final GlaiveWeaponsLootTable instance = new GlaiveWeaponsLootTable();

    private GlaiveWeaponsLootTable() {
        super(new LootItemInterface[]{glaiveWeapons});
    }
}

