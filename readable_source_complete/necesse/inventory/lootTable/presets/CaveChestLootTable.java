/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ChanceLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.inventory.lootTable.lootItem.OneOfTicketLootItems;
import necesse.inventory.lootTable.lootItem.RotationLootItem;

public class CaveChestLootTable {
    public static final RotationLootItem basicMainItems = RotationLootItem.presetRotation(new LootItem("zephyrcharm"), new LootItem("shinebelt"), new LootItem("heavyhammer"), new LootItem("noblehorseshoe"));
    public static final RotationLootItem desertMainItems = RotationLootItem.presetRotation(new LootItem("ancientfeather"), new LootItem("miningcharm"), new LootItem("cactusshield"), new LootItem("airvessel"), new LootItem("prophecyslab"));
    public static final RotationLootItem swampMainItems = RotationLootItem.presetRotation(new LootItem("swamptome"), new LootItem("slimecanister"), new LootItem("stinkflask"), new LootItem("vambrace"), new LootItem("overgrownfishingrod"));
    public static final RotationLootItem snowMainItems = RotationLootItem.presetRotation(new LootItem("frozenwave"), new LootItem("calmingrose"), new LootItem("frozenheart"), new LootItem("sparegemstones"), new LootItem("magicbranch"));
    public static final RotationLootItem plainsMainItems = RotationLootItem.presetRotation(new LootItem("companionlocket"), new LootItem("willowisplantern"), new LootItem("essenceofperspective"), new LootItem("essenceofprolonging"), new LootItem("sentientsword"));
    public static final OneOfLootItems potions = new OneOfLootItems(LootItem.between("attackspeedpotion", 2, 4), LootItem.between("healthregenpotion", 2, 4), LootItem.between("speedpotion", 2, 4), LootItem.between("battlepotion", 2, 4), LootItem.between("resistancepotion", 2, 4));
    public static final OneOfLootItems bars = new OneOfLootItems(LootItem.offset("ironbar", 8, 2), LootItem.offset("copperbar", 10, 2), LootItem.offset("goldbar", 5, 2));
    public static final LootTable extraItems = new LootTable(ChanceLootItem.offset(0.5f, "torch", 15, 5), new ChanceLootItemList(0.75f, new OneOfTicketLootItems(3, LootItem.between("ironbomb", 4, 6), 1, LootItem.between("dynamitestick", 2, 3))), LootItem.between("recallscroll", 1, 2), new ChanceLootItem(0.33f, "enchantingscroll"));
    public static final RotationLootItem basicVinyls = RotationLootItem.presetRotation(new LootItem("forestpathvinyl"), new LootItem("meadowmeanderingvinyl"), new LootItem("fieldsofserenityvinyl"), new LootItem("awakeningtwilightvinyl"), new LootItem("depthsoftheforestvinyl"));
    public static final RotationLootItem snowVinyls = RotationLootItem.presetRotation(new LootItem("auroratundravinyl"), new LootItem("polarnightvinyl"), new LootItem("glaciersembracevinyl"));
    public static final RotationLootItem plainsVinyls = RotationLootItem.presetRotation(new LootItem("meadowmeanderingvinyl"), new LootItem("fieldsofserenityvinyl"), new LootItem("runecarvedwallsvinyl"));
    public static final RotationLootItem swampVinyls = RotationLootItem.presetRotation(new LootItem("watersideserenadevinyl"), new LootItem("gatorslullabyvinyl"), new LootItem("murkymirevinyl"));
    public static final RotationLootItem desertVinyls = RotationLootItem.presetRotation(new LootItem("oasisserenadevinyl"), new LootItem("nightinthedunesvinyl"), new LootItem("dustyhollowsvinyl"));
    public static final LootTable basicChest = new LootTable(basicMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4f, basicVinyls), new ChanceLootItem(0.4f, "mysteriousportal"));
    public static final LootTable desertChest = new LootTable(desertMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4f, desertVinyls), new ChanceLootItem(0.4f, "ancientstatue"));
    public static final LootTable swampChest = new LootTable(swampMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4f, swampVinyls), new ChanceLootItem(0.4f, "spikedfossil"));
    public static final LootTable snowChest = new LootTable(snowMainItems, potions, bars, extraItems, new ChanceLootItemList(0.4f, snowVinyls), new ChanceLootItem(0.4f, "royalegg"));
    public static final LootTable plainsChest = new LootTable(plainsMainItems, potions, bars, extraItems, new ChanceLootItemList(0.5f, LootTablePresets.runeboundCosmeticArmorLootTable), new ChanceLootItemList(0.4f, plainsVinyls), new ChanceLootItem(0.4f, "boneoffering"));
}

