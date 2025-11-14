/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;

public class BiomeOresLootTable {
    public static final LootTable defaultOres = new LootTable(ChanceLootItem.between(0.5f, "copperore", 5, 25), ChanceLootItem.between(0.4f, "ironore", 5, 20), ChanceLootItem.between(0.25f, "goldore", 5, 15));
    public static final LootTable extraItems = new LootTable(ChanceLootItem.offset(0.5f, "torch", 10, 5), new ChanceLootItemList(0.5f, new OneOfTicketLootItems(3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3))), new ChanceLootItem(0.2f, "miningpotion"), new ChanceLootItem(0.1f, "spelunkerpotion"));
    public static final LootTable forestOres = new LootTable(defaultOres, LootItem.between("clay", 5, 20), extraItems);
    public static final LootTable snowOres = new LootTable(LootItem.between("frostshard", 5, 20), defaultOres, extraItems);
    public static final LootTable plainsOres = new LootTable(LootItem.between("runestone", 5, 14), defaultOres, extraItems);
    public static final LootTable swampOres = new LootTable(LootItem.between("ivyore", 5, 12), defaultOres, extraItems);
    public static final LootTable desertOres = new LootTable(LootItem.between("quartz", 5, 8), defaultOres, extraItems);
    public static final LootTable deepForestOres = new LootTable(LootItem.between("tungstenore", 5, 14), ChanceLootItem.between(0.25f, "lifequartz", 3, 12), defaultOres, extraItems);
    public static final LootTable deepSnowOres = new LootTable(LootItem.between("glacialore", 5, 12), ChanceLootItem.between(0.25f, "lifequartz", 3, 12), defaultOres, extraItems);
    public static final LootTable deepPlainsOres = new LootTable(LootItem.between("amber", 5, 10), ChanceLootItem.between(0.25f, "lifequartz", 3, 12), defaultOres, extraItems);
    public static final LootTable deepSwampOres = new LootTable(LootItem.between("myceliumore", 5, 9), ChanceLootItem.between(0.25f, "lifequartz", 3, 12), defaultOres, extraItems);
    public static final LootTable deepDesertOres = new LootTable(LootItem.between("ancientfossilore", 5, 8), ChanceLootItem.between(0.25f, "lifequartz", 3, 12), defaultOres, extraItems);
}

