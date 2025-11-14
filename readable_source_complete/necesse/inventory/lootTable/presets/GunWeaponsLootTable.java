/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class GunWeaponsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems gunWeapons = new OneOfLootItems(new LootItemInterface[0]);
    public static final GunWeaponsLootTable instance = new GunWeaponsLootTable();

    private GunWeaponsLootTable() {
        super(new LootItemInterface[]{gunWeapons});
    }
}

