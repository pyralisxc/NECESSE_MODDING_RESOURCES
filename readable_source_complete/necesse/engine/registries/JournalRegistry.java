/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import java.util.Collections;
import necesse.engine.GameLoadingScreen;
import necesse.engine.journal.JournalEntry;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.util.HashMapLinkedList;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.biomes.Biome;

public class JournalRegistry
extends GameRegistry<JournalEntry> {
    public static final JournalRegistry instance = new JournalRegistry();
    private static HashMapLinkedList<Integer, JournalEntry> biomeIDToEntries = new HashMapLinkedList();

    private JournalRegistry() {
        super("Biome", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "biomes"));
        JournalEntry forestSurface = JournalRegistry.registerJournalEntry("forestsurface", new JournalEntry(BiomeRegistry.FOREST, LevelIdentifier.SURFACE_IDENTIFIER));
        forestSurface.addBiomeLootEntry("oaklog", "sprucelog", "apple", "blueberry", "firemone", "gobfish", "halffish", "furfish", "carp", "herring", "mackerel", "salmon", "trout");
        forestSurface.addMobEntries("cow", "bull", "sheep", "ram", "rabbit", "duck", "zombie", "zombiearcher", "stabbybush", "evilsprotector");
        forestSurface.addTreasureEntry(LootTablePresets.surfaceRuinsChest, LootTablePresets.rollingPinDisplayStand);
        forestSurface.addEntryChallenges(JournalChallengeRegistry.FOREST_SURFACE_CHALLENGES_ID);
        JournalEntry forestCave = JournalRegistry.registerJournalEntry("forestcave", new JournalEntry(BiomeRegistry.FOREST, LevelIdentifier.CAVE_IDENTIFIER));
        forestCave.addBiomeLootEntry("stone", "clay", "copperore", "ironore", "goldore", "sapphire", "salmon", "trout", "carp", "rockfish", "terrorfish");
        forestCave.addMobEntries("zombie", "zombiearcher", "crawlingzombie", "goblin", "vampire", "cavemole", "giantcavespider", "trenchcoatgoblinstacked", "grizzlybear", "beetcavecroppler", "stonecaveling");
        forestCave.addTreasureEntry(LootTablePresets.basicCaveChest, LootTablePresets.basicCaveRuinsChest);
        forestCave.addEntryChallenges(JournalChallengeRegistry.FOREST_CAVES_CHALLENGES_ID);
        JournalEntry forestDeepCave = JournalRegistry.registerJournalEntry("forestdeepcave", new JournalEntry(BiomeRegistry.FOREST, LevelIdentifier.DEEP_CAVE_IDENTIFIER));
        forestDeepCave.addBiomeLootEntry("deepstone", "copperore", "ironore", "goldore", "obsidian", "tungstenore", "lifequartz", "ruby");
        forestDeepCave.addMobEntries("skeleton", "skeletonthrower", "skeletonminer", "deepcavespirit", "beetcavecroppler", "deepstonecaveling", "reaper");
        forestDeepCave.addTreasureEntry(LootTablePresets.deepCaveChest, LootTablePresets.basicDeepCaveRuinsChest);
        forestDeepCave.addEntryChallenges(JournalChallengeRegistry.FOREST_DEEP_CAVES_CHALLENGES_ID);
        JournalEntry pirateVillage = JournalRegistry.registerJournalEntry("forestpiratevillage", new JournalEntry(BiomeRegistry.PIRATE_VILLAGE, LevelIdentifier.SURFACE_IDENTIFIER));
        pirateVillage.addBiomeLootEntry("oaklog", "sprucelog", "apple", "blueberry", "firemone");
        pirateVillage.addMobEntries("pirateparrot", "piraterecruit", "piratecaptain");
        pirateVillage.addTreasureEntry(LootTablePresets.pirateChest, LootTablePresets.pirateDisplayStand);
        pirateVillage.addEntryChallenges(JournalChallengeRegistry.PIRATE_VILLAGE_CHALLENGES_ID);
        JournalEntry plainsSurface = JournalRegistry.registerJournalEntry("plainssurface", new JournalEntry(BiomeRegistry.PLAINS, LevelIdentifier.SURFACE_IDENTIFIER));
        plainsSurface.addBiomeLootEntry("birchlog", "maplelog", "raspberry", "sunflower", "gobfish", "halffish", "furfish", "carp", "herring", "mackerel", "salmon", "trout");
        plainsSurface.addMobEntries("cow", "bull", "sheep", "ram", "rabbit", "duck", "zombie", "zombiearcher", "stabbybush", "evilsprotector");
        plainsSurface.addTreasureEntry(LootTablePresets.surfaceRuinsChest, LootTablePresets.rollingPinDisplayStand);
        plainsSurface.addEntryChallenges(JournalChallengeRegistry.PLAINS_SURFACE_CHALLENGES_ID);
        JournalEntry plainsCave = JournalRegistry.registerJournalEntry("plainscave", new JournalEntry(BiomeRegistry.PLAINS, LevelIdentifier.CAVE_IDENTIFIER));
        plainsCave.addBiomeLootEntry("granite", "runestone", "copperore", "ironore", "goldore", "salmon", "trout", "carp", "rockfish", "terrorfish");
        plainsCave.addMobEntries("runeboundbrute", "runeboundshaman", "runeboundtrapper", "bonewalker", "goblin", "trenchcoatgoblinstacked", "grizzlybear", "beetcavecroppler", "granitecaveling", "chieftain");
        plainsCave.addTreasureEntry(LootTablePresets.plainsCaveChest, LootTablePresets.plainsCaveRuinsChest);
        plainsCave.addEntryChallenges(JournalChallengeRegistry.PLAINS_CAVES_CHALLENGES_ID);
        JournalEntry plainsDeepCave = JournalRegistry.registerJournalEntry("plainsdeepcave", new JournalEntry(BiomeRegistry.PLAINS, LevelIdentifier.DEEP_CAVE_IDENTIFIER));
        plainsDeepCave.addBiomeLootEntry("basalt", "amber", "dryadlog", "birchlog", "maplelog", "copperore", "ironore", "goldore", "tungstenore", "lifequartz", "topaz", "salmon", "trout", "carp", "rockfish", "terrorfish");
        plainsDeepCave.addMobEntries("forestspector", "dryadsentinel", "spiritghoul", "beetcavecroppler", "dryadcaveling", "thecursedcrone");
        plainsDeepCave.addTreasureEntry(LootTablePresets.deepPlainsCaveChest, LootTablePresets.plainsDeepCaveRuinsChest);
        plainsDeepCave.addEntryChallenges(JournalChallengeRegistry.PLAINS_DEEP_CAVES_CHALLENGES_ID);
        JournalEntry snowSurface = JournalRegistry.registerJournalEntry("snowsurface", new JournalEntry(BiomeRegistry.SNOW, LevelIdentifier.SURFACE_IDENTIFIER));
        snowSurface.addBiomeLootEntry("pinelog", "blackberry", "iceblossom", "gobfish", "halffish", "icefish", "carp", "cod", "salmon", "trout");
        snowSurface.addMobEntries("sheep", "ram", "penguin", "snowhare", "duck", "polarbear", "zombie", "zombiearcher", "trapperzombie");
        snowSurface.addTreasureEntry(LootTablePresets.surfaceRuinsChest, LootTablePresets.rollingPinDisplayStand);
        snowSurface.addEntryChallenges(JournalChallengeRegistry.SNOW_SURFACE_CHALLENGES_ID);
        JournalEntry snowCave = JournalRegistry.registerJournalEntry("snowcave", new JournalEntry(BiomeRegistry.SNOW, LevelIdentifier.CAVE_IDENTIFIER));
        snowCave.addBiomeLootEntry("snowstone", "copperore", "ironore", "goldore", "frostshard", "salmon", "trout", "carp", "rockfish", "terrorfish");
        snowCave.addMobEntries("zombie", "zombiearcher", "trapperzombie", "crawlingzombie", "cavemole", "frozendwarf", "frostsentry", "goblin", "vampire", "blackcavespider", "beetcavecroppler", "snowstonecaveling", "queenspider");
        snowCave.addTreasureEntry(LootTablePresets.snowCaveChest, LootTablePresets.snowCaveRuinsChest);
        snowCave.addEntryChallenges(JournalChallengeRegistry.SNOW_CAVES_CHALLENGES_ID);
        JournalEntry snowDeepCave = JournalRegistry.registerJournalEntry("snowdeepcave", new JournalEntry(BiomeRegistry.SNOW, LevelIdentifier.DEEP_CAVE_IDENTIFIER));
        snowDeepCave.addBiomeLootEntry("deepsnowstone", "copperore", "ironore", "goldore", "tungstenore", "lifequartz", "glacialore", "salmon", "trout", "carp", "rockfish", "terrorfish");
        snowDeepCave.addMobEntries("skeleton", "skeletonthrower", "snowwolf", "cryoflake", "ninja", "beetcavecroppler", "deepsnowstonecaveling", "cryoqueen");
        snowDeepCave.addTreasureEntry(LootTablePresets.deepSnowCaveChest, LootTablePresets.snowDeepCaveRuinsChest, LootTablePresets.stringsVinyls2LootTable);
        snowDeepCave.addEntryChallenges(JournalChallengeRegistry.SNOW_DEEP_CAVES_CHALLENGES_ID);
        JournalEntry dungeon = JournalRegistry.registerJournalEntry("dungeon", new JournalEntry(BiomeRegistry.DUNGEON));
        dungeon.addMobEntries("enchantedzombie", "enchantedzombiearcher", "enchantedcrawlingzombie", "voidapprentice", "beetcavecroppler", "voidwizard");
        dungeon.addTreasureEntry(LootTablePresets.dungeonChest);
        dungeon.addTreasureEntry("bashybush");
        dungeon.addEntryChallenges(JournalChallengeRegistry.DUNGEON_CHALLENGES_ID);
        JournalEntry swampSurface = JournalRegistry.registerJournalEntry("swampsurface", new JournalEntry(BiomeRegistry.SWAMP, LevelIdentifier.SURFACE_IDENTIFIER));
        swampSurface.addBiomeLootEntry("willowlog", "cattail", "mushroom", "gobfish", "halffish", "swampfish", "carp", "mackerel", "salmon", "tuna");
        swampSurface.addMobEntries("cow", "bull", "sheep", "ram", "swampslug", "frog", "duck", "zombie", "zombiearcher", "swampzombie", "swampslime");
        swampSurface.addTreasureEntry(LootTablePresets.surfaceRuinsChest, LootTablePresets.rollingPinDisplayStand);
        swampSurface.addEntryChallenges(JournalChallengeRegistry.SWAMP_SURFACE_CHALLENGES_ID);
        JournalEntry swampCave = JournalRegistry.registerJournalEntry("swampcave", new JournalEntry(BiomeRegistry.SWAMP, LevelIdentifier.CAVE_IDENTIFIER));
        swampCave.addBiomeLootEntry("swampstone", "copperore", "ironore", "goldore", "ivyore", "salmon", "carp", "rockfish", "terrorfish");
        swampCave.addMobEntries("frog", "zombie", "zombiearcher", "crawlingzombie", "swampzombie", "swampslime", "swampshooter", "goblin", "vampire", "cavemole", "swampcavespider", "evilwitch", "beetcavecroppler", "swampstonecaveling", "swampguardian");
        swampCave.addTreasureEntry(LootTablePresets.swampCaveChest, LootTablePresets.swampCaveRuinsChest, LootTablePresets.evilWitchChest);
        swampCave.addEntryChallenges(JournalChallengeRegistry.SWAMP_CAVES_CHALLENGES_ID);
        JournalEntry swampDeepCave = JournalRegistry.registerJournalEntry("swampdeepcave", new JournalEntry(BiomeRegistry.SWAMP, LevelIdentifier.DEEP_CAVE_IDENTIFIER));
        swampDeepCave.addBiomeLootEntry("deepswampstone", "copperore", "ironore", "goldore", "tungstenore", "lifequartz", "myceliumore", "emerald");
        swampDeepCave.addMobEntries("ancientskeleton", "ancientskeletonthrower", "swampskeleton", "swampdweller", "giantswampslime", "smallswampcavespider", "staticjellyfish", "fishianhookwarrior", "fishianhealer", "mosquito", "beetcavecroppler", "deepswampstonecaveling", "pestwarden");
        swampDeepCave.addTreasureEntry(LootTablePresets.deepSwampCaveChest, LootTablePresets.swampDeepCaveRuinsChest, LootTablePresets.fishianBarrel);
        swampDeepCave.addEntryChallenges(JournalChallengeRegistry.SWAMP_DEEP_CAVES_CHALLENGES_ID);
        JournalEntry desertSurface = JournalRegistry.registerJournalEntry("desertsurface", new JournalEntry(BiomeRegistry.DESERT, LevelIdentifier.SURFACE_IDENTIFIER));
        desertSurface.addBiomeLootEntry("palmlog", "coconut", "gobfish", "halffish", "carp", "mackerel", "salmon", "tuna");
        desertSurface.addMobEntries("wildostrich", "duck", "zombie", "zombiearcher", "mummy");
        desertSurface.addTreasureEntry(LootTablePresets.surfaceRuinsChest, LootTablePresets.rollingPinDisplayStand);
        desertSurface.addEntryChallenges(JournalChallengeRegistry.DESERT_SURFACE_CHALLENGES_ID);
        JournalEntry desertCave = JournalRegistry.registerJournalEntry("desertcave", new JournalEntry(BiomeRegistry.DESERT, LevelIdentifier.CAVE_IDENTIFIER));
        desertCave.addBiomeLootEntry("sandstone", "copperore", "ironore", "goldore", "quartz", "amethyst", "salmon", "carp", "rockfish", "terrorfish");
        desertCave.addMobEntries("mummy", "mummymage", "sandspirit", "jackal", "beetcavecroppler", "sandstonecaveling", "ancientvulture");
        desertCave.addTreasureEntry(LootTablePresets.desertCaveChest, LootTablePresets.desertCaveRuinsChest);
        desertCave.addEntryChallenges(JournalChallengeRegistry.DESERT_CAVES_CHALLENGES_ID);
        JournalEntry desertDeepCave = JournalRegistry.registerJournalEntry("desertdeepcave", new JournalEntry(BiomeRegistry.DESERT, LevelIdentifier.DEEP_CAVE_IDENTIFIER));
        desertDeepCave.addBiomeLootEntry("deepsandstone", "copperore", "ironore", "goldore", "ancientfossilore", "lifequartz", "myceliumore");
        desertDeepCave.addMobEntries("ancientskeleton", "ancientskeletonthrower", "desertcrawler", "sandworm", "beetcavecroppler", "deepsandstonecaveling", "sageandgrit");
        desertDeepCave.addTreasureEntry(LootTablePresets.deepDesertCaveChest, LootTablePresets.desertDeepCaveRuinsChest);
        desertDeepCave.addEntryChallenges(JournalChallengeRegistry.DESERT_DEEP_CAVES_CHALLENGES_ID);
        JournalEntry temple = JournalRegistry.registerJournalEntry("temple", new JournalEntry(BiomeRegistry.TEMPLE));
        temple.addMobEntries("ancientskeleton", "ancientarmoredskeleton", "ancientskeletonthrower", "ancientskeletonmage", "beetcavecroppler", "fallenwizard");
        temple.addTreasureEntry(LootTablePresets.templeChest);
        temple.addEntryChallenges(JournalChallengeRegistry.TEMPLE_CHALLENGES_ID);
        JournalEntry forestDeepCaveIncursion = JournalRegistry.registerJournalEntry("forestdeepcaveincursion", new JournalEntry(BiomeRegistry.FOREST_DEEP_CAVE_INCURSION, IncursionBiomeRegistry.FOREST_DEEP_CAVE_INCURSION));
        forestDeepCaveIncursion.addBiomeLootEntry("deepstone", "tungstenore", "upgradeshard", "alchemyshard");
        forestDeepCaveIncursion.addMobEntries("skeleton", "skeletonthrower", "deepcavespirit", "beetcavecroppler", "reaper");
        JournalEntry snowDeepCaveIncursion = JournalRegistry.registerJournalEntry("snowdeepcaveincursion", new JournalEntry(BiomeRegistry.SNOW_DEEP_CAVE_INCURSION, IncursionBiomeRegistry.SNOW_DEEP_CAVE_INCURSION));
        snowDeepCaveIncursion.addBiomeLootEntry("deepsnowstone", "glacialore", "upgradeshard", "alchemyshard", "salmon", "trout", "carp", "rockfish", "terrorfish");
        snowDeepCaveIncursion.addMobEntries("skeleton", "skeletonthrower", "snowwolf", "cryoflake", "ninja", "beetcavecroppler", "cryoqueen");
        JournalEntry swampDeepCaveIncursion = JournalRegistry.registerJournalEntry("swampdeepcaveincursion", new JournalEntry(BiomeRegistry.SWAMP_DEEP_CAVE_INCURSION, IncursionBiomeRegistry.SWAMP_DEEP_CAVE_INCURSION));
        swampDeepCaveIncursion.addBiomeLootEntry("deepswampstone", "myceliumore", "upgradeshard", "alchemyshard");
        swampDeepCaveIncursion.addMobEntries("ancientskeleton", "ancientskeletonthrower", "swampskeleton", "swampdweller", "giantswampslime", "smallswampcavespider", "beetcavecroppler", "pestwarden");
        JournalEntry desertDeepCaveIncursion = JournalRegistry.registerJournalEntry("desertdeepcaveincursion", new JournalEntry(BiomeRegistry.DESERT_DEEP_CAVE_INCURSION, IncursionBiomeRegistry.DESERT_DEEP_CAVE_INCURSION));
        desertDeepCaveIncursion.addBiomeLootEntry("deepsandstone", "ancientfossilore", "upgradeshard", "alchemyshard");
        desertDeepCaveIncursion.addMobEntries("ancientskeleton", "ancientskeletonthrower", "desertcrawler", "sandworm", "beetcavecroppler", "sageandgrit");
        JournalEntry slimeCaveIncursion = JournalRegistry.registerJournalEntry("slimecaveincursion", new JournalEntry(BiomeRegistry.SLIME_CAVE, IncursionBiomeRegistry.SLIME_CAVE));
        slimeCaveIncursion.addBiomeLootEntry("slimestone", "slimeum", "upgradeshard", "alchemyshard");
        slimeCaveIncursion.addMobEntries("warriorslime", "leggedslimethrower", "mageslime", "ghostslime", "slimeworm", "beetcavecroppler", "motherslime");
        JournalEntry graveyardIncursion = JournalRegistry.registerJournalEntry("graveyardincursion", new JournalEntry(BiomeRegistry.GRAVEYARD, IncursionBiomeRegistry.GRAVEYARD));
        graveyardIncursion.addBiomeLootEntry("nightsteelore", "upgradeshard", "alchemyshard");
        graveyardIncursion.addMobEntries("cryptvampire", "cryptbat", "phantom", "beetcavecroppler", "nightswarm");
        JournalEntry spiderCastleIncursion = JournalRegistry.registerJournalEntry("spidercastleincursion", new JournalEntry(BiomeRegistry.SPIDER_CASTLE, IncursionBiomeRegistry.SPIDER_CASTLE));
        spiderCastleIncursion.addBiomeLootEntry("spiderstone", "spideriteore", "upgradeshard", "alchemyshard");
        spiderCastleIncursion.addMobEntries("spiderkin", "spiderkinwarrior", "spiderkinarcher", "spiderkinmage", "bloatedspider", "webspinner", "beetcavecroppler", "spiderempress");
        JournalEntry sunArenaIncursion = JournalRegistry.registerJournalEntry("sunarenaincursion", new JournalEntry(BiomeRegistry.SUN_ARENA, IncursionBiomeRegistry.SUN_ARENA));
        sunArenaIncursion.addMobEntries("sunlightchampion");
        JournalEntry moonArenaIncursion = JournalRegistry.registerJournalEntry("moonarenaincursion", new JournalEntry(BiomeRegistry.MOON_ARENA, IncursionBiomeRegistry.MOON_ARENA));
        moonArenaIncursion.addMobEntries("moonlightdancer");
        JournalEntry crystalHollowIncursion = JournalRegistry.registerJournalEntry("crystalhollowincursion", new JournalEntry(BiomeRegistry.CRYSTAL_HOLLOW, IncursionBiomeRegistry.CRYSTAL_HOLLOW));
        crystalHollowIncursion.addBiomeLootEntry("omnicrystal", "amethyst", "sapphire", "emerald", "ruby", "pearlescentshard", "upgradeshard", "alchemyshard", "salmon", "carp", "rockfish", "terrorfish");
        crystalHollowIncursion.addMobEntries("crystalgolem", "crystalarmadillo", "crystaldragon");
    }

    @Override
    protected void onRegister(JournalEntry object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        biomeIDToEntries = new HashMapLinkedList();
        for (JournalEntry element : this.getElements()) {
            biomeIDToEntries.add(element.biome.getID(), element);
        }
    }

    public static JournalEntry registerJournalEntry(String stringID, JournalEntry journalEntry) {
        instance.register(stringID, journalEntry);
        return journalEntry;
    }

    public static Iterable<JournalEntry> getJournalEntries() {
        return instance.getElements();
    }

    public static int getTotalEntryCount() {
        return instance.size();
    }

    public static JournalEntry getJournalEntry(String stringID) {
        return (JournalEntry)instance.getElement(stringID);
    }

    public static JournalEntry getJournalEntry(int id) {
        return (JournalEntry)instance.getElement(id);
    }

    public static int getJournalEntryID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static String getJournalEntryStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static Iterable<JournalEntry> getEntriesForBiome(int biomeID) {
        return (Iterable)biomeIDToEntries.get(biomeID);
    }

    public static String[] getHostileMobStringIDsForBiomeLevel(Biome biome, LevelIdentifier levelIdentifier) {
        Iterable<JournalEntry> entriesForBiome = JournalRegistry.getEntriesForBiome(biome.getID());
        if (entriesForBiome == null) {
            return null;
        }
        ArrayList result = new ArrayList();
        for (JournalEntry journalEntry : entriesForBiome) {
            String[] mobIDs = (String[])journalEntry.mobsData.stream().filter(item -> !item.mob.isBoss()).filter(item -> item.mob.isHostile).map(item -> item.mob.getStringID()).toArray(String[]::new);
            if (levelIdentifier.isDeepCave()) {
                Collections.addAll(result, mobIDs);
                continue;
            }
            if (levelIdentifier.isSurface() && journalEntry.levelIdentifier.isSurface()) {
                return mobIDs;
            }
            if (levelIdentifier.isDeepCave() || journalEntry.levelIdentifier.isDeepCave()) continue;
            Collections.addAll(result, mobIDs);
        }
        return result.toArray(new String[0]);
    }
}

