/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CrateLootTable;

public class CaveRuinsLootTable {
    public static final RotationLootItem seeds1 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("wheatseed", 2, 6), LootItem.between("wheat", 4, 12)), new LootItemList(LootItem.between("cornseed", 2, 6), LootItem.between("corn", 4, 12)), new LootItemList(LootItem.between("tomatoseed", 2, 6), LootItem.between("tomato", 4, 12)), new LootItemList(LootItem.between("cabbageseed", 2, 6), LootItem.between("cabbage", 4, 12)));
    public static final RotationLootItem seeds2 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("tomatoseed", 2, 6), LootItem.between("tomato", 4, 12)), new LootItemList(LootItem.between("cabbageseed", 2, 6), LootItem.between("cabbage", 4, 12)), new LootItemList(LootItem.between("chilipepperseed", 2, 6), LootItem.between("chilipepper", 4, 12)), new LootItemList(LootItem.between("sugarbeetseed", 2, 6), LootItem.between("sugarbeet", 4, 12)));
    public static final RotationLootItem seeds3 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("chilipepperseed", 2, 6), LootItem.between("chilipepper", 4, 12)), new LootItemList(LootItem.between("sugarbeetseed", 2, 6), LootItem.between("sugarbeet", 4, 12)), new LootItemList(LootItem.between("eggplantseed", 2, 6), LootItem.between("eggplant", 4, 12)), new LootItemList(LootItem.between("potatoseed", 2, 6), LootItem.between("potato", 4, 12)));
    public static final RotationLootItem seeds4 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("eggplantseed", 2, 6), LootItem.between("eggplant", 4, 12)), new LootItemList(LootItem.between("potatoseed", 2, 6), LootItem.between("potato", 4, 12)), new LootItemList(LootItem.between("riceseed", 2, 6), LootItem.between("riceseed", 4, 12)), new LootItemList(LootItem.between("carrotseed", 2, 6), LootItem.between("carrot", 4, 12)));
    public static final LootTable basicItems = new LootTable(seeds1, CrateLootTable.basicCrate, CrateLootTable.basicCrate);
    public static final LootTable snowItems = new LootTable(seeds2, CrateLootTable.snowCrate, CrateLootTable.snowCrate);
    public static final LootTable swampItems = new LootTable(seeds3, CrateLootTable.swampCrate, CrateLootTable.swampCrate);
    public static final LootTable desertItems = new LootTable(seeds4, CrateLootTable.desertCrate, CrateLootTable.desertCrate);
    public static final LootTable plainsItems = new LootTable(seeds1, CrateLootTable.basicCrate, CrateLootTable.basicCrate);
    public static final LootTable extraItems = new LootTable(ChanceLootItem.offset(0.5f, "torch", 10, 5), new ChanceLootItemList(0.5f, new OneOfTicketLootItems(3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3))), new ChanceLootItem(0.2f, "enchantingscroll"));
    public static final LootTable basicChest = new LootTable(basicItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.forestBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "mysteriousportal"));
    public static final LootTable snowChest = new LootTable(snowItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.snowBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "royalegg"));
    public static final LootTable swampChest = new LootTable(swampItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.swampBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "spikedfossil"));
    public static final LootTable desertChest = new LootTable(desertItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.desertBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "ancientstatue"));
    public static final LootTable plainsChest = new LootTable(plainsItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.forestBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "boneoffering"));
}

