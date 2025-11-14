/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent
 *  necesse.entity.mobs.hostile.DeepCaveSpiritMob
 *  necesse.entity.mobs.hostile.FishianHealerMob
 *  necesse.entity.mobs.hostile.FishianHookWarriorMob
 *  necesse.entity.mobs.hostile.FishianShamanMob
 *  necesse.entity.mobs.hostile.GoblinMob
 *  necesse.entity.mobs.hostile.TrenchcoatGoblinHelmetMob
 *  necesse.entity.mobs.hostile.VampireMob
 *  necesse.entity.mobs.hostile.bosses.AncientVultureMob
 *  necesse.entity.mobs.hostile.bosses.CryoQueenMob
 *  necesse.entity.mobs.hostile.bosses.CrystalDragonHead
 *  necesse.entity.mobs.hostile.bosses.EvilsProtectorMob
 *  necesse.entity.mobs.hostile.bosses.FallenWizardMob
 *  necesse.entity.mobs.hostile.bosses.FlyingSpiritsHead
 *  necesse.entity.mobs.hostile.bosses.MoonlightDancerMob
 *  necesse.entity.mobs.hostile.bosses.MotherSlimeMob
 *  necesse.entity.mobs.hostile.bosses.PestWardenHead
 *  necesse.entity.mobs.hostile.bosses.QueenSpiderMob
 *  necesse.entity.mobs.hostile.bosses.ReaperMob
 *  necesse.entity.mobs.hostile.bosses.SpiderEmpressMob
 *  necesse.entity.mobs.hostile.bosses.SunlightChampionMob
 *  necesse.entity.mobs.hostile.bosses.SwampGuardianHead
 *  necesse.entity.mobs.hostile.bosses.VoidWizard
 *  necesse.entity.mobs.hostile.pirates.PirateCaptainMob
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.LootTablePresets
 *  necesse.inventory.lootTable.lootItem.ChanceLootItem
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.inventory.lootTable.lootItem.LootItemList
 *  necesse.inventory.lootTable.lootItem.RotationLootItem
 *  necesse.inventory.lootTable.presets.CaveChestLootTable
 *  necesse.inventory.lootTable.presets.DeepCaveChestLootTable
 *  necesse.inventory.lootTable.presets.DeepCaveRuinsLootTable
 */
package aphorea.registry;

import necesse.entity.levelEvent.nightSwarmEvent.NightSwarmLevelEvent;
import necesse.entity.mobs.hostile.DeepCaveSpiritMob;
import necesse.entity.mobs.hostile.FishianHealerMob;
import necesse.entity.mobs.hostile.FishianHookWarriorMob;
import necesse.entity.mobs.hostile.FishianShamanMob;
import necesse.entity.mobs.hostile.GoblinMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinHelmetMob;
import necesse.entity.mobs.hostile.VampireMob;
import necesse.entity.mobs.hostile.bosses.AncientVultureMob;
import necesse.entity.mobs.hostile.bosses.CryoQueenMob;
import necesse.entity.mobs.hostile.bosses.CrystalDragonHead;
import necesse.entity.mobs.hostile.bosses.EvilsProtectorMob;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsHead;
import necesse.entity.mobs.hostile.bosses.MoonlightDancerMob;
import necesse.entity.mobs.hostile.bosses.MotherSlimeMob;
import necesse.entity.mobs.hostile.bosses.PestWardenHead;
import necesse.entity.mobs.hostile.bosses.QueenSpiderMob;
import necesse.entity.mobs.hostile.bosses.ReaperMob;
import necesse.entity.mobs.hostile.bosses.SpiderEmpressMob;
import necesse.entity.mobs.hostile.bosses.SunlightChampionMob;
import necesse.entity.mobs.hostile.bosses.SwampGuardianHead;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;
import necesse.inventory.lootTable.lootItem.RotationLootItem;
import necesse.inventory.lootTable.presets.CaveChestLootTable;
import necesse.inventory.lootTable.presets.DeepCaveChestLootTable;
import necesse.inventory.lootTable.presets.DeepCaveRuinsLootTable;

public class AphLootTables {
    public static LootTable runeInventorHouse = new LootTable();
    public static LootTable infectedLootLake = new LootTable();
    public static LootTable infectedCaveForest = new LootTable();
    public static LootTable infectedCaveVariousTreasures = new LootTable();
    public static LootTable infectedCaveForestVariousTreasures = new LootTable();
    public static final RotationLootItem basicChestAllMainItems = RotationLootItem.presetRotation((LootItemInterface[])new LootItemInterface[]{new LootItem("zephyrcharm"), new LootItem("shinebelt"), new LootItem("heavyhammer"), new LootItem("noblehorseshoe"), new LootItem("ancientfeather"), new LootItem("miningcharm"), new LootItem("cactusshield"), new LootItem("airvessel"), new LootItem("prophecyslab"), new LootItem("swamptome"), new LootItem("slimecanister"), new LootItem("stinkflask"), new LootItem("vambrace"), new LootItem("overgrownfishingrod"), new LootItem("frozenwave"), new LootItem("calmingrose"), new LootItem("frozenheart"), new LootItem("sparegemstones"), new LootItem("magicbranch"), new LootItem("blowgun"), new LootItem("sling")});

    public static void modifyLootTables() {
        LootTablePresets.startChest.items.addAll(new LootItemList(new LootItemInterface[]{new LootItem("sling", 1), new LootItem("basicbackpack", 1)}));
        LootTablePresets.caveCryptCoffin.items.add(new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.1f, "bloodyperiapt"), new ChanceLootItem(0.05f, "onyxrune")}));
        LootTablePresets.snowCaveChest.items.add(new ChanceLootItem(0.05f, "frozenperiapt"));
        LootTablePresets.surfaceRuinsChest.items.addAll(new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.05f, "blowgun"), new ChanceLootItem(0.05f, "sling")}));
        LootTablePresets.basicCaveChest.items.addAll(new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.05f, "blowgun"), new ChanceLootItem(0.05f, "sling")}));
        LootTablePresets.hunterChest.items.addAll(new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.05f, "blowgun"), new ChanceLootItem(0.05f, "sling")}));
        LootTablePresets.dungeonChest.items.addAll(new LootItemList(new LootItemInterface[]{new ChanceLootItem(0.1f, "runeofthunder"), new ChanceLootItem(0.05f, "heartring")}));
        LootTablePresets.fishianBarrel.items.add(new ChanceLootItem(0.25f, "tidalrune"));
        DeepCaveChestLootTable.extraItems.items.add(new ChanceLootItem(0.02f, "abyssalrune"));
        DeepCaveRuinsLootTable.extraItems.items.add(new ChanceLootItem(0.005f, "abyssalrune"));
        DeepCaveSpiritMob.lootTable.items.add(new ChanceLootItem(0.05f, "runeofshadows"));
        FishianHookWarriorMob.lootTable.items.add(new ChanceLootItem(0.01f, "tidalrune"));
        FishianHealerMob.lootTable.items.add(new ChanceLootItem(0.01f, "tidalrune"));
        FishianShamanMob.lootTable.items.add(new ChanceLootItem(0.01f, "tidalrune"));
        TrenchcoatGoblinHelmetMob.lootTable = new LootTable(new LootItemInterface[]{GoblinMob.lootTable, new ChanceLootItem(0.4f, "frenzyrune")});
        VampireMob.lootTable.items.add(new ChanceLootItem(0.01f, "onyxrune"));
        EvilsProtectorMob.privateLootTable.items.add(new LootItem("runeofevilsprotector"));
        QueenSpiderMob.privateLootTable.items.add(new LootItem("runeofqueenspider"));
        VoidWizard.privateLootTable.items.add(new LootItem("runeofvoidwizard"));
        SwampGuardianHead.privateLootTable.items.add(new LootItem("runeofswampguardian"));
        AncientVultureMob.privateLootTable.items.add(new LootItem("runeofancientvulture"));
        PirateCaptainMob.privateLootTable.items.add(new LootItem("runeofpiratecaptain"));
        ReaperMob.privateLootTable.items.add(new LootItem("runeofreaper"));
        CryoQueenMob.privateLootTable.items.add(new LootItem("runeofcryoqueen"));
        PestWardenHead.privateLootTable.items.add(new LootItem("runeofpestwarden"));
        FlyingSpiritsHead.privateLootTable.items.add(new LootItem("runeofsageandgrit"));
        FallenWizardMob.privateLootTable.items.add(new LootItem("runeoffallenwizard"));
        MotherSlimeMob.privateLootTable.items.add(new LootItem("runeofmotherslime"));
        NightSwarmLevelEvent.privateLootTable.items.add(new LootItem("runeofnightswarm"));
        SpiderEmpressMob.privateLootTable.items.add(new LootItem("runeofspiderempress"));
        SunlightChampionMob.privateLootTable.items.add(new LootItem("runeofsunlightchampion"));
        MoonlightDancerMob.privateLootTable.items.add(new LootItem("runeofmoonlightdancer"));
        CrystalDragonHead.lootTable.items.add(new LootItem("runeofcrystaldragon"));
    }

    static {
        AphLootTables.runeInventorHouse.items.addAll(new LootItemList(new LootItemInterface[]{new LootItem("initialrune", 1), new LootItem("runestutorialbook", 1), new LootItem("rusticrunesinjector", 1), new LootItem("ironbar", 2)}));
        AphLootTables.infectedLootLake.items.addAll(new LootItemList(new LootItemInterface[]{new LootItem("thespammer"), LootItem.between((String)"spambullet", (int)90, (int)110), LootItem.between((String)"spinel", (int)4, (int)6), LootItem.between((String)"lifespinel", (int)1, (int)2), basicChestAllMainItems, CaveChestLootTable.potions, CaveChestLootTable.extraItems}));
        AphLootTables.infectedCaveForest.items.addAll(new LootItemList(new LootItemInterface[]{LootItem.between((String)"infectedalloy", (int)3, (int)4), LootItem.between((String)"spinel", (int)2, (int)3), LootItem.between((String)"lifespinel", (int)0, (int)1), CaveChestLootTable.potions, CaveChestLootTable.extraItems}));
        AphLootTables.infectedCaveVariousTreasures.items.addAll(new LootItemList(new LootItemInterface[]{RotationLootItem.globalLootRotation((LootItemInterface[])new LootItemInterface[]{new LootItem("lightsaber"), new LootItem("shotgunsaber"), new LootItem("ninjascarf"), new LootItem("adrenalinecharm"), new LootItem("cursedmedallion")})}));
        AphLootTables.infectedCaveForestVariousTreasures.items.addAll(new LootItemList(new LootItemInterface[]{RotationLootItem.globalLootRotation((LootItemInterface[])new LootItemInterface[]{new LootItem("brokenkora")})}));
    }
}

