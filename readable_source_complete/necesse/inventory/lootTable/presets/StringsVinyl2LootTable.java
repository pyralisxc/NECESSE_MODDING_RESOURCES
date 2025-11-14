/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class StringsVinyl2LootTable
extends LootTable {
    public static final StringsVinyl2LootTable instance = new StringsVinyl2LootTable();

    private StringsVinyl2LootTable() {
        super(new LootItem("voidsembracestringsvinyl"), new LootItem("gatorslullabystringsvinyl"), new LootItem("dustyhollowsstringsvinyl"), new LootItem("losttemplestringsvinyl"));
    }
}

