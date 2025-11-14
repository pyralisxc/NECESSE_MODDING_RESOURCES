/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class DeepCaveChestLootTable {
    public static final RotationLootItem basicMainItems = RotationLootItem.presetRotation(new LootItem("elderlywand"), new LootItem("firestone"), new LootItem("challengerspauldron"), new LootItem("depthscatcher"));
    public static final RotationLootItem snowMainItems = RotationLootItem.presetRotation(new LootItem("spikedboots"), new LootItem("froststone"), new LootItem("siphonshield"), new LootItem("icepickaxe"));
    public static final RotationLootItem plainsMainItems = RotationLootItem.presetRotation(new LootItem("parrybuckler"), new LootItem("spiritboard"), new LootItem("guardianbracelet"), new LootItem("secondwindcharm"));
    public static final RotationLootItem swampMainItems = RotationLootItem.presetRotation(new LootItem("agedchampionshield"), new LootItem("agedchampionscabbard"), new LootItem("swampdwellerstaff"), new LootItem("druidsgreatbow"));
    public static final RotationLootItem desertMainItems = RotationLootItem.presetRotation(new LootItem("scryingmirror"), new LootItem("diggingclaw"), new LootItem("antiquerifle"), new LootItem("clockworkheart"), new LootItem("forbiddenspellbook"));
    public static final OneOfLootItems potions = new OneOfLootItems(LootItem.between("attackspeedpotion", 2, 4), LootItem.between("battlepotion", 2, 4), LootItem.between("resistancepotion", 2, 4), LootItem.between("thornspotion", 2, 4), LootItem.between("accuracypotion", 2, 4));
    public static final OneOfLootItems basicBars = new OneOfLootItems(LootItem.offset("ironbar", 8, 2), LootItem.offset("goldbar", 5, 2), LootItem.offset("tungstenbar", 6, 2));
    public static final OneOfLootItems snowBars = new OneOfLootItems(LootItem.offset("goldbar", 5, 2), LootItem.offset("tungstenbar", 6, 2), LootItem.offset("glacialbar", 6, 2));
    public static final OneOfLootItems plainsBars = new OneOfLootItems(LootItem.offset("goldbar", 5, 2), LootItem.offset("tungstenbar", 6, 2), LootItem.offset("amber", 6, 2));
    public static final OneOfLootItems swampBars = new OneOfLootItems(LootItem.offset("goldbar", 5, 2), LootItem.offset("tungstenbar", 6, 2), LootItem.offset("myceliumbar", 6, 2));
    public static final OneOfLootItems desertBars = new OneOfLootItems(LootItem.offset("goldbar", 5, 2), LootItem.offset("tungstenbar", 6, 2), LootItem.offset("ancientfossilbar", 6, 2));
    public static final LootTable extraItems = new LootTable(ChanceLootItem.offset(0.5f, "torch", 15, 5), new ChanceLootItemList(0.75f, new OneOfTicketLootItems(3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3))), new ChanceLootItem(0.33f, "enchantingscroll"));
    public static final RotationLootItem basicVinyls = RotationLootItem.presetRotation(new LootItem("forestpathvinyl"), new LootItem("meadowmeanderingvinyl"), new LootItem("fieldsofserenityvinyl"), new LootItem("awakeningtwilightvinyl"), new LootItem("secretsoftheforestvinyl"));
    public static final RotationLootItem snowVinyls = RotationLootItem.presetRotation(new LootItem("auroratundravinyl"), new LootItem("polarnightvinyl"), new LootItem("icestarvinyl"));
    public static final RotationLootItem plainsVinyls = RotationLootItem.presetRotation(new LootItem("meadowmeanderingvinyl"), new LootItem("fieldsofserenityvinyl"), new LootItem("forgottendepthsvinyl"));
    public static final RotationLootItem swampVinyls = RotationLootItem.presetRotation(new LootItem("watersideserenadevinyl"), new LootItem("gatorslullabyvinyl"), new LootItem("swampcavernvinyl"));
    public static final RotationLootItem desertVinyls = RotationLootItem.presetRotation(new LootItem("oasisserenadevinyl"), new LootItem("nightinthedunesvinyl"), new LootItem("sandcatacombsvinyl"));
    public static final LootTable basicDeepCaveChest = new LootTable(basicMainItems, potions, basicBars, extraItems, new ChanceLootItemList(0.4f, basicVinyls), new ChanceLootItem(0.5f, "shadowgate"));
    public static final LootTable snowDeepCaveChest = new LootTable(snowMainItems, potions, snowBars, extraItems, new ChanceLootItemList(0.4f, snowVinyls), new ChanceLootItem(0.5f, "icecrown"));
    public static final LootTable plainsDeepCaveChest = new LootTable(plainsMainItems, potions, plainsBars, extraItems, new ChanceLootItemList(0.4f, plainsVinyls), new ChanceLootItem(0.5f, "spiriturn"));
    public static final LootTable swampDeepCaveChest = new LootTable(swampMainItems, potions, swampBars, extraItems, new ChanceLootItemList(0.4f, swampVinyls), new ChanceLootItem(0.5f, "decayingleaf"));
    public static final LootTable desertDeepCaveChest = new LootTable(desertMainItems, potions, desertBars, extraItems, new ChanceLootItemList(0.4f, desertVinyls), new ChanceLootItem(0.5f, "dragonsouls"));
}

