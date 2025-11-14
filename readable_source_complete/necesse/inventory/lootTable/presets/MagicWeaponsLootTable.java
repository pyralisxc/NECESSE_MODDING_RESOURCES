/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class MagicWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems magicWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final MagicWeaponsLootTable instance = new MagicWeaponsLootTable();

    private MagicWeaponsLootTable() {
        super(new LootItemInterface[]{magicWeapons});
    }
}

