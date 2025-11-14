/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class GreatswordWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems greatswordWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final GreatswordWeaponsLootTable instance = new GreatswordWeaponsLootTable();

    private GreatswordWeaponsLootTable() {
        super(new LootItemInterface[]{greatswordWeapons});
    }
}

