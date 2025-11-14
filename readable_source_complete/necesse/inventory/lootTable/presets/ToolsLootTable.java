/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class ToolsLootTable
extends OneOfLootItems {
    public static final OneOfLootItems tools = new OneOfLootItems(new LootItemInterface[0]);
    public static final ToolsLootTable instance = new ToolsLootTable();

    private ToolsLootTable() {
        super(new LootItemInterface[]{tools});
    }
}

