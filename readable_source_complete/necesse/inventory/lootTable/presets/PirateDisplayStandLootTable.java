/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class PirateDisplayStandLootTable
extends LootTable {
    public static final RotationLootItem items = RotationLootItem.presetRotation(new LootItem("cutlass"), new LootItem("genielamp"), new LootItem("inducingamulet"), new LootItem("lifeline"), LootItem.offset("goldbar", 10, 3), LootItem.offset("mapfragment", 3, 1), new LootItem("villagemap"), new LootItem("dungeonmap"));
    public static final PirateDisplayStandLootTable instance = new PirateDisplayStandLootTable();

    private PirateDisplayStandLootTable() {
        super(items);
    }
}

