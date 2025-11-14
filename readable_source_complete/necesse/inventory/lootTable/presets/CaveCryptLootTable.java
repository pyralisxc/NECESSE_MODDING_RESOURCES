/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CrateLootTable;

public class CaveCryptLootTable
extends LootTable {
    public static final RotationLootItem uniqueItems = RotationLootItem.presetRotation(new LootItem("vampiresgift"), new LootItem("bloodbolt"));
    public static final CaveCryptLootTable instance = new CaveCryptLootTable();

    private CaveCryptLootTable() {
        super(new ChanceLootItem(0.15f, "enchantingscroll"), CrateLootTable.basicCrate, CrateLootTable.basicCrate, LootItem.offset("coin", 35, 5), ChanceLootItem.between(0.7f, "batwing", 3, 5));
    }
}

