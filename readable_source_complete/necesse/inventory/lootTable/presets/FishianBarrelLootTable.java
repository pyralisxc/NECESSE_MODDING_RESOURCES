/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class FishianBarrelLootTable
extends LootTable {
    public static final RotationLootItem mainItems = RotationLootItem.presetRotation(new LootItem("anchorandchain"), new LootItem("butcherscleaver"));
    public static final FishianBarrelLootTable instance = new FishianBarrelLootTable();

    private FishianBarrelLootTable() {
        super(mainItems, LootItem.offset("frogleg", 10, 5), ChanceLootItem.offset(0.5f, "torch", 15, 5));
    }
}

