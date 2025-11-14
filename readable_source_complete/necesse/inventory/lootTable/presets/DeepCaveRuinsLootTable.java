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
import necesse.inventory.lootTable.presets.DeepCrateLootTable;

public class DeepCaveRuinsLootTable {
    public static final RotationLootItem deepSeeds1 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)), new LootItemList(LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)), new LootItemList(LootItem.between("onionseed", 2, 6), LootItem.between("onion", 4, 12)), new LootItemList(LootItem.between("pumpkinseed", 2, 6), LootItem.between("pumpkin", 4, 12)));
    public static final RotationLootItem deepSeeds2 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)), new LootItemList(LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)), new LootItemList(LootItem.between("pumpkinseed", 2, 6), LootItem.between("pumpkin", 4, 12)), new LootItemList(LootItem.between("strawberryseed", 2, 6), LootItem.between("strawberry", 4, 12)));
    public static final RotationLootItem deepSeeds3 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)), new LootItemList(LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)), new LootItemList(LootItem.between("pumpkinseed", 2, 6), LootItem.between("pumpkin", 4, 12)), new LootItemList(LootItem.between("strawberryseed", 2, 6), LootItem.between("strawberry", 4, 12)));
    public static final RotationLootItem deepSeeds4 = RotationLootItem.presetRotation(new LootItemList(LootItem.between("lemonsapling", 1, 3), LootItem.between("lemon", 4, 12)), new LootItemList(LootItem.between("bananasapling", 1, 3), LootItem.between("banana", 4, 12)), new LootItemList(LootItem.between("strawberryseed", 2, 6), LootItem.between("strawberry", 4, 12)), new LootItemList(LootItem.between("coffeebeans", 2, 6), LootItem.between("coffeebeans", 4, 12)));
    public static final LootTable basicDeepItems = new LootTable(deepSeeds1, DeepCrateLootTable.basicDeepCrate, DeepCrateLootTable.basicDeepCrate);
    public static final LootTable snowDeepItems = new LootTable(deepSeeds2, DeepCrateLootTable.snowDeepCrate, DeepCrateLootTable.snowDeepCrate);
    public static final LootTable plainsDeepItems = new LootTable(deepSeeds1, DeepCrateLootTable.plainsDeepCrate, DeepCrateLootTable.plainsDeepCrate);
    public static final LootTable swampDeepItems = new LootTable(deepSeeds3, DeepCrateLootTable.swampDeepCrate, DeepCrateLootTable.swampDeepCrate);
    public static final LootTable desertDeepItems = new LootTable(deepSeeds4, DeepCrateLootTable.desertDeepCrate, DeepCrateLootTable.desertDeepCrate);
    public static final LootTable extraItems = new LootTable(ChanceLootItem.offset(0.5f, "torch", 10, 5), new ChanceLootItemList(0.5f, new OneOfTicketLootItems(3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3))), new ChanceLootItem(0.2f, "enchantingscroll"));
    public static final LootTable basicDeepChest = new LootTable(basicDeepItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.forestBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "shadowgate"));
    public static final LootTable snowDeepChest = new LootTable(snowDeepItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.snowBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "icecrown"));
    public static final LootTable swampDeepChest = new LootTable(swampDeepItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.swampBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "decayingleaf"));
    public static final LootTable desertDeepChest = new LootTable(desertDeepItems, extraItems, new ChanceLootItemList(0.2f, LootTablePresets.desertBiomeOldVinylsLootTable), new ChanceLootItem(0.2f, "dragonsouls"));
    public static final LootTable plainsDeepChest = new LootTable(plainsDeepItems, extraItems, new ChanceLootItem(0.2f, "spiriturn"));
}

