/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class StringsVinyl1LootTable
extends LootTable {
    public static final StringsVinyl1LootTable instance = new StringsVinyl1LootTable();

    private StringsVinyl1LootTable() {
        super(new LootItem("adventurebeginsstringsvinyl"), new LootItem("forestpathstringsvinyl"), new LootItem("depthsoftheforeststringsvinyl"), new LootItem("awakeningtwilightstringsvinyl"));
    }
}

