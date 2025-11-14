/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.GameRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldPresets.AbandonedMineWorldPreset;
import necesse.engine.world.worldPresets.AgedChampionWorldPreset;
import necesse.engine.world.worldPresets.AncientVultureArenaWorldPreset;
import necesse.engine.world.worldPresets.BearCaveWorldPreset;
import necesse.engine.world.worldPresets.CavePresetsWorldPreset;
import necesse.engine.world.worldPresets.CaveRuinsWorldPreset;
import necesse.engine.world.worldPresets.ChestRoomWorldPreset;
import necesse.engine.world.worldPresets.ChieftainBossWorldPreset;
import necesse.engine.world.worldPresets.CursedCroneBossWorldPreset;
import necesse.engine.world.worldPresets.CustomCrystalsWorldPreset;
import necesse.engine.world.worldPresets.DeepCavePresetsWorldPreset;
import necesse.engine.world.worldPresets.DeepSwampSpiderNestsWorldPreset;
import necesse.engine.world.worldPresets.DesertCavelingOasisWorldPreset;
import necesse.engine.world.worldPresets.DesertTempleWorldPreset;
import necesse.engine.world.worldPresets.DungeonEntranceWorldPreset;
import necesse.engine.world.worldPresets.FishianMiniBiomeWorldPreset;
import necesse.engine.world.worldPresets.ForestLootAreaWorldPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.MinecartsWorldPreset;
import necesse.engine.world.worldPresets.RuneboundTombsWorldPreset;
import necesse.engine.world.worldPresets.RuneboundTribeWorldPreset;
import necesse.engine.world.worldPresets.SnowCaveIceOpeningsWorldPreset;
import necesse.engine.world.worldPresets.SnowLootAreaWorldPreset;
import necesse.engine.world.worldPresets.SnowMusicPlayerWorldPreset;
import necesse.engine.world.worldPresets.SnowSpiderNestsWorldPreset;
import necesse.engine.world.worldPresets.SpiderNestsWorldPreset;
import necesse.engine.world.worldPresets.SurfacePirateVillageWorldPreset;
import necesse.engine.world.worldPresets.SurfacePresetsWorldPreset;
import necesse.engine.world.worldPresets.SurfaceRuinsWorldPreset;
import necesse.engine.world.worldPresets.SurfaceVillageWorldPreset;
import necesse.engine.world.worldPresets.SwampCaveSporesWorldPreset;
import necesse.engine.world.worldPresets.SwampMudAreasWorldPreset;
import necesse.engine.world.worldPresets.SwampWitchCabinWorldPreset;
import necesse.engine.world.worldPresets.SwampWitchCastleWorldPreset;
import necesse.engine.world.worldPresets.VampireCryptWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;

public class WorldPresetRegistry
extends GameRegistry<WorldPreset> {
    public static final WorldPresetRegistry instance = new WorldPresetRegistry();
    private static ArrayList<WorldPreset> prioritySortedPresets = new ArrayList();

    private WorldPresetRegistry() {
        super("WorldPreset", 32762);
    }

    @Override
    public void registerCore() {
        WorldPresetRegistry.registerPreset("forestspidernests", new SpiderNestsWorldPreset(BiomeRegistry.FOREST, LevelIdentifier.CAVE_IDENTIFIER, 0.025f, "giantcavespider"));
        WorldPresetRegistry.registerPreset("forestsapphirecrystals", new CustomCrystalsWorldPreset(BiomeRegistry.FOREST, LevelIdentifier.CAVE_IDENTIFIER, 0.012f, "sapphiregravel", "sapphireclustersmall", "sapphirecluster"));
        WorldPresetRegistry.registerPreset("forestvampirecrypts", new VampireCryptWorldPreset(BiomeRegistry.FOREST, LevelIdentifier.CAVE_IDENTIFIER, 0.0375f));
        WorldPresetRegistry.registerPreset("forestbearcave", new BearCaveWorldPreset(BiomeRegistry.FOREST, LevelIdentifier.CAVE_IDENTIFIER, 0.005f));
        WorldPresetRegistry.registerPreset("snowcaveiceopenings", new SnowCaveIceOpeningsWorldPreset());
        WorldPresetRegistry.registerPreset("snowspidernests", new SnowSpiderNestsWorldPreset());
        WorldPresetRegistry.registerPreset("snowvampirecrypts", new VampireCryptWorldPreset(BiomeRegistry.SNOW, LevelIdentifier.CAVE_IDENTIFIER, 0.012f));
        WorldPresetRegistry.registerPreset("plainsbearcave", new BearCaveWorldPreset(BiomeRegistry.PLAINS, LevelIdentifier.CAVE_IDENTIFIER, 0.005f));
        WorldPresetRegistry.registerPreset("swampcavespores", new SwampCaveSporesWorldPreset());
        WorldPresetRegistry.registerPreset("swampmudareas", new SwampMudAreasWorldPreset());
        WorldPresetRegistry.registerPreset("swampspidernests", new SpiderNestsWorldPreset(BiomeRegistry.SWAMP, LevelIdentifier.CAVE_IDENTIFIER, 0.025f, "swampcavespider"));
        WorldPresetRegistry.registerPreset("desertamethystcrystals", new CustomCrystalsWorldPreset(BiomeRegistry.DESERT, LevelIdentifier.CAVE_IDENTIFIER, 0.012f, "amethystgravel", "amethystclustersmall", "amethystcluster"));
        WorldPresetRegistry.registerPreset("deepforestrubycrystals", new CustomCrystalsWorldPreset(BiomeRegistry.FOREST, LevelIdentifier.DEEP_CAVE_IDENTIFIER, 0.012f, "rubygravel", "rubyclustersmall", "rubycluster"));
        WorldPresetRegistry.registerPreset("deepplainstopazcrystals", new CustomCrystalsWorldPreset(BiomeRegistry.PLAINS, LevelIdentifier.DEEP_CAVE_IDENTIFIER, 0.012f, "topazgravel", "topazclustersmall", "topazcluster"));
        WorldPresetRegistry.registerPreset("deepswampfishianbiome", new FishianMiniBiomeWorldPreset());
        WorldPresetRegistry.registerPreset("deepswampemeralcrystals", new CustomCrystalsWorldPreset(BiomeRegistry.SWAMP, LevelIdentifier.DEEP_CAVE_IDENTIFIER, 0.012f, "emeraldgravel", "emeraldclustersmall", "emeraldcluster"));
        WorldPresetRegistry.registerPreset("deepswampspidernests", new DeepSwampSpiderNestsWorldPreset());
        WorldPresetRegistry.registerPreset("minecarttracks", new MinecartsWorldPreset());
        WorldPresetRegistry.registerPreset("dungeonentrance", new DungeonEntranceWorldPreset());
        WorldPresetRegistry.registerPreset("chieftainarena", new ChieftainBossWorldPreset());
        WorldPresetRegistry.registerPreset("cursedcronearena", new CursedCroneBossWorldPreset());
        WorldPresetRegistry.registerPreset("vulturearenas", new AncientVultureArenaWorldPreset());
        WorldPresetRegistry.registerPreset("deserttemple", new DesertTempleWorldPreset());
        WorldPresetRegistry.registerPreset("piratevillages", new SurfacePirateVillageWorldPreset());
        WorldPresetRegistry.registerPreset("surfacevillages", new SurfaceVillageWorldPreset());
        WorldPresetRegistry.registerPreset("surfaceruins", new SurfaceRuinsWorldPreset());
        WorldPresetRegistry.registerPreset("swampwitchcabins", new SwampWitchCabinWorldPreset());
        WorldPresetRegistry.registerPreset("plainsruneboundtribes", new RuneboundTribeWorldPreset());
        WorldPresetRegistry.registerPreset("deepforestabandonedmines", new AbandonedMineWorldPreset());
        WorldPresetRegistry.registerPreset("deepplainsruneboundtombs", new RuneboundTombsWorldPreset());
        WorldPresetRegistry.registerPreset("chestrooms", new ChestRoomWorldPreset());
        WorldPresetRegistry.registerPreset("caveruins", new CaveRuinsWorldPreset());
        WorldPresetRegistry.registerPreset("forestlootareas", new ForestLootAreaWorldPreset());
        WorldPresetRegistry.registerPreset("snowlootareas", new SnowLootAreaWorldPreset());
        WorldPresetRegistry.registerPreset("swampwitchcastle", new SwampWitchCastleWorldPreset());
        WorldPresetRegistry.registerPreset("desertcavelingoasis", new DesertCavelingOasisWorldPreset());
        WorldPresetRegistry.registerPreset("deepsnowmusicplayer", new SnowMusicPlayerWorldPreset());
        WorldPresetRegistry.registerPreset("deepswampagedchampion", new AgedChampionWorldPreset());
        WorldPresetRegistry.registerPreset("surfacepresets", new SurfacePresetsWorldPreset());
        WorldPresetRegistry.registerPreset("cavepresets", new CavePresetsWorldPreset());
        WorldPresetRegistry.registerPreset("deepcavepresets", new DeepCavePresetsWorldPreset());
    }

    @Override
    protected void onRegister(WorldPreset object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        prioritySortedPresets = new ArrayList(instance.size());
        for (WorldPreset element : instance.getElements()) {
            prioritySortedPresets.add(element);
        }
        prioritySortedPresets.sort(Comparator.comparingInt(preset -> -preset.getPriority()));
        for (WorldPreset element : instance.getElements()) {
            element.onRegistryClosed();
        }
    }

    public static <T extends WorldPreset> T registerPreset(String stringID, T preset) {
        instance.register(stringID, preset);
        return preset;
    }

    public static WorldPreset getPreset(String stringID) {
        return (WorldPreset)instance.getElement(stringID);
    }

    public static void initRegion(LevelPresetsRegion region, int customSeed, PerformanceTimerManager timer) {
        GameRandom random = customSeed != 0 ? new GameRandom(customSeed).nextSeeded(45) : region.worldRegion.worldEntity.getNewWorldRandom().nextSeeded(region.worldRegion.worldPresetRegionX).nextSeeded(region.worldRegion.worldPresetRegionY).nextSeeded(region.identifier.hashCode());
        for (WorldPreset preset : prioritySortedPresets) {
            GameRandom seededRandom = random.nextSeeded(preset.getStringID().hashCode());
            if (!preset.shouldAddToRegion(region)) continue;
            Performance.record(timer, preset.getStringID(), () -> preset.addToRegion(seededRandom, region, region.worldRegion.worldEntity.getGeneratorStack(), timer));
        }
        region.finalizeRegionSetup();
    }
}

