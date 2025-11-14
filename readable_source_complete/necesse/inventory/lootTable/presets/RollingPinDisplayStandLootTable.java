/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class RollingPinDisplayStandLootTable
extends LootTable {
    public static final RotationLootItem items = RotationLootItem.presetRotation(new LootItem("rollingpin"));
    public static final RollingPinDisplayStandLootTable instance = new RollingPinDisplayStandLootTable();

    private RollingPinDisplayStandLootTable() {
        super(items);
    }
}

