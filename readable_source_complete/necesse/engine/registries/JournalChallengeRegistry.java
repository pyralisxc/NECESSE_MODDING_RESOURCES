/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import necesse.engine.journal.BecomePossessedByAForestSpectorJournalChallenge;
import necesse.engine.journal.CraftItemJournalChallenge;
import necesse.engine.journal.CutSwampCobwebJournalChallenge;
import necesse.engine.journal.CutSwampThornsJournalChallenge;
import necesse.engine.journal.DefeatMobJournalChallenge;
import necesse.engine.journal.DefeatPiratesOnBoatJournalChallenge;
import necesse.engine.journal.DestroyCoinStacksJournalChallenge;
import necesse.engine.journal.DestroyVasesInDesertCaveJournalChallenge;
import necesse.engine.journal.EnchantAndEquipJournalChallenge;
import necesse.engine.journal.EquipSettlerWithQuartzJournalChallenge;
import necesse.engine.journal.EquippedArmorSetJournalChallenge;
import necesse.engine.journal.FoodConsumedJournalChallenge;
import necesse.engine.journal.FreeMageJournalChallenge;
import necesse.engine.journal.FreeStylistJournalChallenge;
import necesse.engine.journal.ImpaleIceJavelinsJournalChallenge;
import necesse.engine.journal.ItemObtainedJournalChallenge;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.JournalChallengeListenerManager;
import necesse.engine.journal.KillZombiesInForestSurfaceJournalChallenge;
import necesse.engine.journal.LevelVisitedJournalChallenge;
import necesse.engine.journal.MineOreWithExplosivesChallenge;
import necesse.engine.journal.MultiJournalChallenge;
import necesse.engine.journal.ObjectPlacedJournalChallenge;
import necesse.engine.journal.ObjectsDestroyedJournalChallenge;
import necesse.engine.journal.ObtainDeepDesertTrinketJournalChallenge;
import necesse.engine.journal.PartyInSwampCavesJournalChallenge;
import necesse.engine.journal.PickupItemsJournalChallenge;
import necesse.engine.journal.SeveralPotionBuffsInDeepSnowCavesJournalChallenge;
import necesse.engine.journal.SimpleJournalChallenge;
import necesse.engine.journal.listeners.AdventurePartyChangedJournalChallengeListener;
import necesse.engine.journal.listeners.BuffGainedJournalChallengeListener;
import necesse.engine.journal.listeners.CraftedRecipeJournalChallengeListener;
import necesse.engine.journal.listeners.EquipmentChangedJournalChallengeListener;
import necesse.engine.journal.listeners.FoodConsumedJournalChallengeListener;
import necesse.engine.journal.listeners.ItemObtainedJournalChallengeListener;
import necesse.engine.journal.listeners.ItemPickedUpJournalChallengeListener;
import necesse.engine.journal.listeners.LevelChangedJournalChallengeListener;
import necesse.engine.journal.listeners.MobKilledJournalChallengeListener;
import necesse.engine.journal.listeners.ObjectDestroyedJournalChallengeListener;
import necesse.engine.journal.listeners.ObjectPlacedJournalChallengeListener;
import necesse.engine.journal.listeners.SettlerEquipmentChangedJournalChallengeListener;
import necesse.engine.journal.listeners.SettlerRecruitedJournalChallengeListener;
import necesse.engine.journal.listeners.StatsCombinedJournalChallengeListener;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.GameRegistry;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class JournalChallengeRegistry
extends GameRegistry<JournalChallenge> {
    public static final JournalChallengeRegistry instance = new JournalChallengeRegistry();
    public static LootTable FOREST_SURFACE_REWARD = new LootTable(new LootItemList(new LootItem("frogmask"), new LootItem("frogcostumeshirt"), new LootItem("frogcostumeboots")).setCustomListName("itemcollections", "frogcostume"));
    public static LootTable FOREST_CAVE_REWARD = new LootTable(new LootItem("constructionhammer"));
    public static LootTable FOREST_DEEP_CAVE_REWARD = new LootTable(new LootItemList(new LootItem("seedgun"), new LootItem("seedpouch")).setCustomListName("itemcollections", "seedgunandpouch"));
    public static LootTable PLAINS_SURFACE_REWARD = new LootTable(new LootItem("telescopicladder"));
    public static LootTable PLAINS_CAVE_REWARD = new LootTable(new LootItem("hoverboots"));
    public static LootTable PLAINS_DEEP_CAVE_REWARD = new LootTable(new LootItem("challengersbanner"));
    public static LootTable SNOW_SURFACE_REWARD = new LootTable(new LootItem("bannerofpeace"));
    public static LootTable SNOW_CAVE_REWARD = new LootTable(new LootItemList(new LootItem("horsemask"), new LootItem("horsecostumeshirt"), new LootItem("horsecostumeboots")).setCustomListName("itemcollections", "horsecostume"));
    public static LootTable SNOW_DEEP_CAVE_REWARD = new LootTable(new LootItem("minersprosthetic"));
    public static LootTable DUNGEON_REWARD = new LootTable(new LootItemList(new LootItem("chickenmask"), new LootItem("chickencostumeshirt"), new LootItem("chickencostumeboots")).setCustomListName("itemcollections", "chickencostume"));
    public static LootTable SWAMP_SURFACE_REWARD = new LootTable(new LootItem("itemattractor"));
    public static LootTable SWAMP_CAVE_REWARD = new LootTable(new LootItem("infiniterope"));
    public static LootTable SWAMP_DEEP_CAVE_REWARD = new LootTable(new LootItem("infinitewaterbucket"));
    public static LootTable DESERT_SURFACE_REWARD = new LootTable(new LootItem("toolextender"));
    public static LootTable DESERT_CAVE_REWARD = new LootTable(new LootItemList(new LootItem("alienmask"), new LootItem("aliencostumeshirt"), new LootItem("aliencostumeboots")).setCustomListName("itemcollections", "aliencostume"));
    public static LootTable DESERT_DEEP_CAVE_REWARD = new LootTable(new LootItem("callofthesea"));
    public static LootTable TEMPLE_REWARD = new LootTable(new LootItem("bannerofwar"));
    public static LootTable PIRATE_VILLAGE_REWARD = new LootTable(new LootItem("teleportationstone"));
    public static int FOREST_SURFACE_CHALLENGES_ID;
    public static int FOREST_CAVES_CHALLENGES_ID;
    public static int FOREST_DEEP_CAVES_CHALLENGES_ID;
    public static int PLAINS_SURFACE_CHALLENGES_ID;
    public static int PLAINS_CAVES_CHALLENGES_ID;
    public static int PLAINS_DEEP_CAVES_CHALLENGES_ID;
    public static int SNOW_SURFACE_CHALLENGES_ID;
    public static int SNOW_CAVES_CHALLENGES_ID;
    public static int SNOW_DEEP_CAVES_CHALLENGES_ID;
    public static int DUNGEON_CHALLENGES_ID;
    public static int SWAMP_SURFACE_CHALLENGES_ID;
    public static int SWAMP_CAVES_CHALLENGES_ID;
    public static int SWAMP_DEEP_CAVES_CHALLENGES_ID;
    public static int DESERT_SURFACE_CHALLENGES_ID;
    public static int DESERT_CAVES_CHALLENGES_ID;
    public static int DESERT_DEEP_CAVES_CHALLENGES_ID;
    public static int TEMPLE_CHALLENGES_ID;
    public static int PIRATE_VILLAGE_CHALLENGES_ID;
    public static int KILL_ZOMBIES_ID;
    public static int CAPTURE_COW_ID;
    public static int USE_MYSTERIOUS_PORTAL_ID;
    public static int BEAT_A_BEAR_ID;
    public static int MINE_ORE_WITH_EXPLOSIVES_ID;
    public static int DEFEAT_CAVELING_ID;
    public static int CRAFT_TUNGSTEN_WEAPON_ID;
    public static int CRAFT_LIFE_ELIXIR_ID;
    public static int PLANT_LEMON_TREE_ID;
    public static int CRAFT_RASPBERRY_JUICE_ID;
    public static int SMACK_LEAF_PILES_ID;
    public static int CAPTURE_BEE_ID;
    public static int BREAK_RUNIC_BOULDERS_ID;
    public static int EQUIP_RUNIC_SET_ID;
    public static int FELL_THE_CHIEFTAIN_ID;
    public static int CRAFT_DRYAD_WEAPON_ID;
    public static int POSSESSED_BY_FOREST_SPECTOR_ID;
    public static int WIN_TIC_TAC_TOE_VS_CRONE_ID;
    public static int CATCH_ICEFISH_ID;
    public static int PICKUP_SNOWBALLS_ID;
    public static int FIND_WET_ICICLE_ID;
    public static int EQUIP_FROST_SET_ID;
    public static int IMPALE_FIVE_ICE_JAVELINS_ID;
    public static int DESTROY_ROYAL_EGG_ID;
    public static int WALK_ON_DEEP_ICE_ID;
    public static int OBTAIN_LANDSCAPING_STATION_ID;
    public static int SEVERAL_POTION_BUFFS_ID;
    public static int FREE_CAPTURED_MAGE_ID;
    public static int UPGRADE_ALCHEMY_TABLE;
    public static int FIND_VOID_WIZARD_CHAMBER_ID;
    public static int CRAFT_ROASTED_FROG_LEG_ID;
    public static int PICK_UP_SWAMP_LARVAE_ID;
    public static int GATHER_MUSHROOMS_ID;
    public static int CUT_SWAMP_THORNS_ID;
    public static int CRAFT_IVY_TOOL_ID;
    public static int PARTY_IN_SWAMP_CAVES_ID;
    public static int ENCHANT_AND_EQUIP_ARMOR_SET_ID;
    public static int CRAFT_DECAYING_LEAF_ID;
    public static int CUT_SWAMP_COBWEB_ID;
    public static int FIND_INEFFICIENT_FEATHER_ID;
    public static int EAT_COCONUT_ID;
    public static int CRAFT_PALM_FURNITURE_ID;
    public static int SMASH_VASES_ID;
    public static int EQUIP_SETTLER_QUARTZ_ID;
    public static int FIND_CAVELING_OASIS_ID;
    public static int LOOT_DEEP_DESERT_TRINKET_ID;
    public static int ONESHOT_SKELETON_ID;
    public static int FIND_TEMPLE_BIOME_ID;
    public static int DEFEAT_PIRATES_WHILE_ON_BOAT_ID;
    public static int FREE_CAPTURED_STYLIST_ID;
    public static int DESTROY_COIN_STACKS_ID;
    public static int FIND_SECRET_PAINTING_ID;
    public static int DEFEAT_OLD_NEMESIS_ID;
    public static int CRAFT_FALLEN_ALTAR_ID;
    public static HashSet<String> ONESHOT_SKELETON_VALID_STRINGIDS;
    protected static JournalChallengeListenerManager listenerManager;

    private JournalChallengeRegistry() {
        super("Journal Challenges", 32762);
    }

    @Override
    public void registerCore() {
        KILL_ZOMBIES_ID = JournalChallengeRegistry.registerChallenge("killzombies", new KillZombiesInForestSurfaceJournalChallenge());
        CAPTURE_COW_ID = JournalChallengeRegistry.registerChallenge("capturecow", new SimpleJournalChallenge());
        USE_MYSTERIOUS_PORTAL_ID = JournalChallengeRegistry.registerChallenge("usemysteriousportal", new SimpleJournalChallenge());
        FOREST_SURFACE_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("forestsurface", new MultiJournalChallenge(KILL_ZOMBIES_ID, CAPTURE_COW_ID, USE_MYSTERIOUS_PORTAL_ID).setReward(FOREST_SURFACE_REWARD));
        BEAT_A_BEAR_ID = JournalChallengeRegistry.registerChallenge("beatabear", new DefeatMobJournalChallenge("grizzlybear"));
        MINE_ORE_WITH_EXPLOSIVES_ID = JournalChallengeRegistry.registerChallenge("mineorewithexplosive", new MineOreWithExplosivesChallenge());
        DEFEAT_CAVELING_ID = JournalChallengeRegistry.registerChallenge("defeatcaveling", new DefeatMobJournalChallenge("stonecaveling"));
        FOREST_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("forestcave", new MultiJournalChallenge(BEAT_A_BEAR_ID, MINE_ORE_WITH_EXPLOSIVES_ID, DEFEAT_CAVELING_ID).setReward(FOREST_CAVE_REWARD));
        CRAFT_TUNGSTEN_WEAPON_ID = JournalChallengeRegistry.registerChallenge("crafttungstenweapon", new CraftItemJournalChallenge("tungstensword", "tungstenspear", "tungstenbow", "tungstengreatbow", "tungstenboomerang"));
        CRAFT_LIFE_ELIXIR_ID = JournalChallengeRegistry.registerChallenge("craftlifeelixir", new CraftItemJournalChallenge("lifeelixir"));
        PLANT_LEMON_TREE_ID = JournalChallengeRegistry.registerChallenge("plantlemontree", new ObjectPlacedJournalChallenge("lemonsapling"));
        FOREST_DEEP_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("forestdeepcave", new MultiJournalChallenge(CRAFT_TUNGSTEN_WEAPON_ID, CRAFT_LIFE_ELIXIR_ID, PLANT_LEMON_TREE_ID).setReward(FOREST_DEEP_CAVE_REWARD));
        CRAFT_RASPBERRY_JUICE_ID = JournalChallengeRegistry.registerChallenge("craftraspberryjuice", new CraftItemJournalChallenge("raspberryjuice"));
        SMACK_LEAF_PILES_ID = JournalChallengeRegistry.registerChallenge("smackleafpiles", new ObjectsDestroyedJournalChallenge(20, "leafpile"));
        CAPTURE_BEE_ID = JournalChallengeRegistry.registerChallenge("capturebee", new SimpleJournalChallenge());
        PLAINS_SURFACE_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("plainssurface", new MultiJournalChallenge(CRAFT_RASPBERRY_JUICE_ID, SMACK_LEAF_PILES_ID, CAPTURE_BEE_ID).setReward(PLAINS_SURFACE_REWARD));
        BREAK_RUNIC_BOULDERS_ID = JournalChallengeRegistry.registerChallenge("breakrunicboulder", new ObjectsDestroyedJournalChallenge(3, "runicboulder"));
        EQUIP_RUNIC_SET_ID = JournalChallengeRegistry.registerChallenge("equiprunicset", new EquippedArmorSetJournalChallenge("runicboots", "runicchestplate", "runichat", "runichood", "runichelmet", "runiccrown"));
        FELL_THE_CHIEFTAIN_ID = JournalChallengeRegistry.registerChallenge("fellthechieftain", new DefeatMobJournalChallenge("chieftain"));
        PLAINS_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("plainscave", new MultiJournalChallenge(BREAK_RUNIC_BOULDERS_ID, EQUIP_RUNIC_SET_ID, FELL_THE_CHIEFTAIN_ID).setReward(PLAINS_CAVE_REWARD));
        CRAFT_DRYAD_WEAPON_ID = JournalChallengeRegistry.registerChallenge("craftdryadweapon", new CraftItemJournalChallenge("dryadgreathammer", "dryadbow", "dryadbarrage", "dryadbranch"));
        POSSESSED_BY_FOREST_SPECTOR_ID = JournalChallengeRegistry.registerChallenge("possessedbyforestspector", new BecomePossessedByAForestSpectorJournalChallenge());
        WIN_TIC_TAC_TOE_VS_CRONE_ID = JournalChallengeRegistry.registerChallenge("wintictactoevscrone", new SimpleJournalChallenge());
        PLAINS_DEEP_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("plainsdeepcave", new MultiJournalChallenge(CRAFT_DRYAD_WEAPON_ID, POSSESSED_BY_FOREST_SPECTOR_ID, WIN_TIC_TAC_TOE_VS_CRONE_ID).setReward(PLAINS_DEEP_CAVE_REWARD));
        CATCH_ICEFISH_ID = JournalChallengeRegistry.registerChallenge("catchicefish", new SimpleJournalChallenge());
        PICKUP_SNOWBALLS_ID = JournalChallengeRegistry.registerChallenge("pickupsnowballs", new PickupItemsJournalChallenge(25, true, "snowball"));
        FIND_WET_ICICLE_ID = JournalChallengeRegistry.registerChallenge("findweticicle", new ItemObtainedJournalChallenge("weticicle"));
        SNOW_SURFACE_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("snowsurface", new MultiJournalChallenge(CATCH_ICEFISH_ID, PICKUP_SNOWBALLS_ID, FIND_WET_ICICLE_ID).setReward(SNOW_SURFACE_REWARD));
        EQUIP_FROST_SET_ID = JournalChallengeRegistry.registerChallenge("equipfrostset", new EquippedArmorSetJournalChallenge("frostboots", "frostchestplate", "frosthat", "frosthood", "frosthelmet"));
        IMPALE_FIVE_ICE_JAVELINS_ID = JournalChallengeRegistry.registerChallenge("impalejavelins", new ImpaleIceJavelinsJournalChallenge());
        DESTROY_ROYAL_EGG_ID = JournalChallengeRegistry.registerChallenge("destroyroyalegg", new SimpleJournalChallenge());
        SNOW_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("snowcave", new MultiJournalChallenge(EQUIP_FROST_SET_ID, IMPALE_FIVE_ICE_JAVELINS_ID, DESTROY_ROYAL_EGG_ID).setReward(SNOW_CAVE_REWARD));
        WALK_ON_DEEP_ICE_ID = JournalChallengeRegistry.registerChallenge("walkondeepice", new SimpleJournalChallenge());
        OBTAIN_LANDSCAPING_STATION_ID = JournalChallengeRegistry.registerChallenge("craftlandscaping", new ItemObtainedJournalChallenge("landscapingstation").setCustomName(new LocalMessage("journal", "obtainlandscaping")));
        SEVERAL_POTION_BUFFS_ID = JournalChallengeRegistry.registerChallenge("severalpotionbuffs", new SeveralPotionBuffsInDeepSnowCavesJournalChallenge());
        SNOW_DEEP_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("snowdeepcave", new MultiJournalChallenge(WALK_ON_DEEP_ICE_ID, OBTAIN_LANDSCAPING_STATION_ID, SEVERAL_POTION_BUFFS_ID).setReward(SNOW_DEEP_CAVE_REWARD));
        FREE_CAPTURED_MAGE_ID = JournalChallengeRegistry.registerChallenge("freecapturedmage", new FreeMageJournalChallenge());
        UPGRADE_ALCHEMY_TABLE = JournalChallengeRegistry.registerChallenge("upgradealchemytable", new SimpleJournalChallenge());
        FIND_VOID_WIZARD_CHAMBER_ID = JournalChallengeRegistry.registerChallenge("findvoidwizardchamber", new LevelVisitedJournalChallenge("dungeonarena"));
        DUNGEON_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("dungeon", new MultiJournalChallenge(FREE_CAPTURED_MAGE_ID, UPGRADE_ALCHEMY_TABLE, FIND_VOID_WIZARD_CHAMBER_ID).setReward(DUNGEON_REWARD));
        CRAFT_ROASTED_FROG_LEG_ID = JournalChallengeRegistry.registerChallenge("craftroastedfrogleg", new CraftItemJournalChallenge("roastedfrogleg"));
        PICK_UP_SWAMP_LARVAE_ID = JournalChallengeRegistry.registerChallenge("pickupswamplarvae", new PickupItemsJournalChallenge(10, true, "swamplarva"));
        GATHER_MUSHROOMS_ID = JournalChallengeRegistry.registerChallenge("gathermushrooms", new PickupItemsJournalChallenge(30, true, "mushroom"));
        SWAMP_SURFACE_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("swampsurface", new MultiJournalChallenge(CRAFT_ROASTED_FROG_LEG_ID, PICK_UP_SWAMP_LARVAE_ID, GATHER_MUSHROOMS_ID).setReward(SWAMP_SURFACE_REWARD));
        CUT_SWAMP_THORNS_ID = JournalChallengeRegistry.registerChallenge("cutswampthorns", new CutSwampThornsJournalChallenge());
        CRAFT_IVY_TOOL_ID = JournalChallengeRegistry.registerChallenge("craftivytool", new CraftItemJournalChallenge("ivypickaxe", "ivyaxe", "ivyshovel"));
        PARTY_IN_SWAMP_CAVES_ID = JournalChallengeRegistry.registerChallenge("partyinswampcaves", new PartyInSwampCavesJournalChallenge());
        SWAMP_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("swampcave", new MultiJournalChallenge(CUT_SWAMP_THORNS_ID, CRAFT_IVY_TOOL_ID, PARTY_IN_SWAMP_CAVES_ID).setReward(SWAMP_CAVE_REWARD));
        ENCHANT_AND_EQUIP_ARMOR_SET_ID = JournalChallengeRegistry.registerChallenge("enchantandequipset", new EnchantAndEquipJournalChallenge());
        CRAFT_DECAYING_LEAF_ID = JournalChallengeRegistry.registerChallenge("craftdecayingleaf", new CraftItemJournalChallenge("decayingleaf"));
        CUT_SWAMP_COBWEB_ID = JournalChallengeRegistry.registerChallenge("cutswampcobweb", new CutSwampCobwebJournalChallenge());
        SWAMP_DEEP_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("swampdeepcave", new MultiJournalChallenge(ENCHANT_AND_EQUIP_ARMOR_SET_ID, CRAFT_DECAYING_LEAF_ID, CUT_SWAMP_COBWEB_ID).setReward(SWAMP_DEEP_CAVE_REWARD));
        FIND_INEFFICIENT_FEATHER_ID = JournalChallengeRegistry.registerChallenge("findinefficientfeather", new ItemObtainedJournalChallenge("inefficientfeather"));
        EAT_COCONUT_ID = JournalChallengeRegistry.registerChallenge("eatcoconut", new FoodConsumedJournalChallenge("coconut"));
        CRAFT_PALM_FURNITURE_ID = JournalChallengeRegistry.registerChallenge("craftpalmfurniture", new CraftItemJournalChallenge("palmchest", "palmdinnertable", "palmdesk", "palmmodulartable", "palmchair", "palmbench", "palmbookshelf", "palmcabinet", "palmbed", "palmdoublebed", "palmdresser", "palmclock", "palmcandelabra", "palmdisplay", "palmbathtub", "palmtoilet"));
        DESERT_SURFACE_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("desertsurface", new MultiJournalChallenge(FIND_INEFFICIENT_FEATHER_ID, EAT_COCONUT_ID, CRAFT_PALM_FURNITURE_ID).setReward(DESERT_SURFACE_REWARD));
        SMASH_VASES_ID = JournalChallengeRegistry.registerChallenge("smashvases", new DestroyVasesInDesertCaveJournalChallenge());
        EQUIP_SETTLER_QUARTZ_ID = JournalChallengeRegistry.registerChallenge("equipsettlerquartz", new EquipSettlerWithQuartzJournalChallenge());
        FIND_CAVELING_OASIS_ID = JournalChallengeRegistry.registerChallenge("findcavelingoasis", new SimpleJournalChallenge());
        DESERT_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("desertcave", new MultiJournalChallenge(SMASH_VASES_ID, EQUIP_SETTLER_QUARTZ_ID, FIND_CAVELING_OASIS_ID).setReward(DESERT_CAVE_REWARD));
        LOOT_DEEP_DESERT_TRINKET_ID = JournalChallengeRegistry.registerChallenge("lootdeepdeserttrinket", new ObtainDeepDesertTrinketJournalChallenge());
        ONESHOT_SKELETON_ID = JournalChallengeRegistry.registerChallenge("oneshotskeleton", new SimpleJournalChallenge());
        FIND_TEMPLE_BIOME_ID = JournalChallengeRegistry.registerChallenge("findtemplebiome", new LevelVisitedJournalChallenge("temple", "templearena"));
        DESERT_DEEP_CAVES_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("desertdeepcave", new MultiJournalChallenge(LOOT_DEEP_DESERT_TRINKET_ID, ONESHOT_SKELETON_ID, FIND_TEMPLE_BIOME_ID).setReward(DESERT_DEEP_CAVE_REWARD));
        FIND_SECRET_PAINTING_ID = JournalChallengeRegistry.registerChallenge("findsecretpainting", new ItemObtainedJournalChallenge("paintingcooljonas", "paintingelder"));
        DEFEAT_OLD_NEMESIS_ID = JournalChallengeRegistry.registerChallenge("defeatoldnemesis", new DefeatMobJournalChallenge("fallenwizard"));
        CRAFT_FALLEN_ALTAR_ID = JournalChallengeRegistry.registerChallenge("craftfallenaltar", new CraftItemJournalChallenge("fallenaltar"));
        TEMPLE_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("temple", new MultiJournalChallenge(FIND_SECRET_PAINTING_ID, DEFEAT_OLD_NEMESIS_ID, CRAFT_FALLEN_ALTAR_ID).setReward(TEMPLE_REWARD));
        DEFEAT_PIRATES_WHILE_ON_BOAT_ID = JournalChallengeRegistry.registerChallenge("defeatpirateswhileonboat", new DefeatPiratesOnBoatJournalChallenge());
        FREE_CAPTURED_STYLIST_ID = JournalChallengeRegistry.registerChallenge("freecapturedstylist", new FreeStylistJournalChallenge());
        DESTROY_COIN_STACKS_ID = JournalChallengeRegistry.registerChallenge("destroycoinstacks", new DestroyCoinStacksJournalChallenge());
        PIRATE_VILLAGE_CHALLENGES_ID = JournalChallengeRegistry.registerChallenge("forestpiratevillage", new MultiJournalChallenge(DEFEAT_PIRATES_WHILE_ON_BOAT_ID, FREE_CAPTURED_STYLIST_ID, DESTROY_COIN_STACKS_ID).setReward(PIRATE_VILLAGE_REWARD));
    }

    @Override
    protected void onRegister(JournalChallenge object, int id, String stringID, boolean isReplace) {
        listenerManager.addChallenge(object);
    }

    @Override
    protected void onRegistryClose() {
        for (JournalChallenge element : this.getElements()) {
            element.onChallengeRegistryClosed();
        }
    }

    public static int registerChallenge(String stringID, JournalChallenge journalChallenge) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register challenges");
        }
        return instance.register(stringID, journalChallenge);
    }

    public static Iterable<JournalChallenge> getChallenges() {
        return instance.getElements();
    }

    public static boolean doesChallengeExists(String stringID) {
        try {
            return instance.getElementIDRaw(stringID) >= 0;
        }
        catch (NoSuchElementException e) {
            return false;
        }
    }

    public static int getTotalChallengeCount() {
        return instance.size();
    }

    public static JournalChallenge getChallenge(String id) {
        return (JournalChallenge)instance.getElement(id);
    }

    public static JournalChallenge getChallenge(int id) {
        return (JournalChallenge)instance.getElement(id);
    }

    public static int getChallengeID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static String getChallengeStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static <T> void handleListeners(ServerClient client, Class<T> listenerClass, Consumer<T> handler) {
        for (T challenge : listenerManager.getChallenges(listenerClass)) {
            if (!((JournalChallenge)challenge).isJournalEntryDiscovered(client)) continue;
            handler.accept(challenge);
        }
    }

    static {
        ONESHOT_SKELETON_VALID_STRINGIDS = new HashSet<String>(Arrays.asList("ancientskeleton", "ancientskeletonthrower", "ancientskeletonthrower", "skeletonthrower", "skeletonmage", "skeletonminer", "swampskeleton", "ancientarmoredskeleton", "ancientskeletonmage"));
        listenerManager = new JournalChallengeListenerManager(MobKilledJournalChallengeListener.class, StatsCombinedJournalChallengeListener.class, CraftedRecipeJournalChallengeListener.class, ItemObtainedJournalChallengeListener.class, EquipmentChangedJournalChallengeListener.class, ObjectDestroyedJournalChallengeListener.class, ObjectPlacedJournalChallengeListener.class, LevelChangedJournalChallengeListener.class, SettlerRecruitedJournalChallengeListener.class, ItemPickedUpJournalChallengeListener.class, FoodConsumedJournalChallengeListener.class, BuffGainedJournalChallengeListener.class, SettlerEquipmentChangedJournalChallengeListener.class, AdventurePartyChangedJournalChallengeListener.class);
    }
}

