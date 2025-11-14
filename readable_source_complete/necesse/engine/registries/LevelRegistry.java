/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.AscendedVoidLevel;
import necesse.level.maps.CaveLevel;
import necesse.level.maps.DebugLevel;
import necesse.level.maps.DeepCaveLevel;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.SurfaceLevel;
import necesse.level.maps.TemporaryDummyLevel;
import necesse.level.maps.biomes.BasicCaveLevel;
import necesse.level.maps.biomes.BasicDeepCaveLevel;
import necesse.level.maps.biomes.BasicSurfaceLevel;
import necesse.level.maps.biomes.desert.DesertCaveLevel;
import necesse.level.maps.biomes.desert.DesertDeepCaveLevel;
import necesse.level.maps.biomes.desert.DesertSurfaceLevel;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;
import necesse.level.maps.biomes.dungeon.DungeonLevel;
import necesse.level.maps.biomes.dungeon.DungeonSurfaceLevel;
import necesse.level.maps.biomes.forest.ForestCaveLevel;
import necesse.level.maps.biomes.forest.ForestDeepCaveLevel;
import necesse.level.maps.biomes.forest.ForestSurfaceLevel;
import necesse.level.maps.biomes.plains.PlainsCaveLevel;
import necesse.level.maps.biomes.plains.PlainsDeepCaveLevel;
import necesse.level.maps.biomes.plains.PlainsSurfaceLevel;
import necesse.level.maps.biomes.snow.SnowCaveLevel;
import necesse.level.maps.biomes.snow.SnowDeepCaveLevel;
import necesse.level.maps.biomes.snow.SnowSurfaceLevel;
import necesse.level.maps.biomes.swamp.SwampCaveLevel;
import necesse.level.maps.biomes.swamp.SwampDeepCaveLevel;
import necesse.level.maps.biomes.swamp.SwampSurfaceLevel;
import necesse.level.maps.biomes.temple.TempleArenaLevel;
import necesse.level.maps.biomes.temple.TempleLevel;
import necesse.level.maps.biomes.trial.TrialRoomLevel;
import necesse.level.maps.incursion.CrystalHollowIncursionLevel;
import necesse.level.maps.incursion.DesertDeepCaveIncursionLevel;
import necesse.level.maps.incursion.ForestDeepCaveIncursionLevel;
import necesse.level.maps.incursion.GraveyardIncursionLevel;
import necesse.level.maps.incursion.MoonArenaIncursionLevel;
import necesse.level.maps.incursion.SettlementRuinsIncursionLevel;
import necesse.level.maps.incursion.SlimeCaveIncursionLevel;
import necesse.level.maps.incursion.SnowDeepCaveIncursionLevel;
import necesse.level.maps.incursion.SpiderCastleIncursionLevel;
import necesse.level.maps.incursion.SunArenaIncursionLevel;
import necesse.level.maps.incursion.SwampDeepCaveIncursionLevel;

public class LevelRegistry
extends ClassedGameRegistry<Level, LevelRegistryElement> {
    public static final LevelRegistry instance = new LevelRegistry();

    public LevelRegistry() {
        super("Level", 32762);
    }

    @Override
    public void registerCore() {
        LevelRegistry.registerLevel("level", Level.class);
        LevelRegistry.registerLevel("dummy", TemporaryDummyLevel.class);
        LevelRegistry.registerLevel("debug", DebugLevel.class);
        LevelRegistry.registerLevel("surface", SurfaceLevel.class);
        LevelRegistry.registerLevel("cave", CaveLevel.class);
        LevelRegistry.registerLevel("deepcave", DeepCaveLevel.class);
        LevelRegistry.registerLevel("basicsurface", BasicSurfaceLevel.class);
        LevelRegistry.registerLevel("basiccave", BasicCaveLevel.class);
        LevelRegistry.registerLevel("basicdeepcave", BasicDeepCaveLevel.class);
        LevelRegistry.registerLevel("forestsurface", ForestSurfaceLevel.class);
        LevelRegistry.registerLevel("forestcave", ForestCaveLevel.class);
        LevelRegistry.registerLevel("forestdeepcave", ForestDeepCaveLevel.class);
        LevelRegistry.registerLevel("plainssurface", PlainsSurfaceLevel.class);
        LevelRegistry.registerLevel("plainscave", PlainsCaveLevel.class);
        LevelRegistry.registerLevel("plainsdeepcave", PlainsDeepCaveLevel.class);
        LevelRegistry.registerLevel("snowsurface", SnowSurfaceLevel.class);
        LevelRegistry.registerLevel("snowcave", SnowCaveLevel.class);
        LevelRegistry.registerLevel("snowdeepcave", SnowDeepCaveLevel.class);
        LevelRegistry.registerLevel("swampsurface", SwampSurfaceLevel.class);
        LevelRegistry.registerLevel("swampcave", SwampCaveLevel.class);
        LevelRegistry.registerLevel("swampdeepcave", SwampDeepCaveLevel.class);
        LevelRegistry.registerLevel("desertsurface", DesertSurfaceLevel.class);
        LevelRegistry.registerLevel("desertcave", DesertCaveLevel.class);
        LevelRegistry.registerLevel("desertdeepcave", DesertDeepCaveLevel.class);
        LevelRegistry.registerLevel("trialroom", TrialRoomLevel.class);
        LevelRegistry.registerLevel("dungeonsurface", DungeonSurfaceLevel.class);
        LevelRegistry.registerLevel("dungeon", DungeonLevel.class);
        LevelRegistry.registerLevel("dungeonarena", DungeonArenaLevel.class);
        LevelRegistry.registerLevel("temple", TempleLevel.class);
        LevelRegistry.registerLevel("templearena", TempleArenaLevel.class);
        LevelRegistry.registerLevel("incursion", IncursionLevel.class);
        LevelRegistry.registerLevel("forestdeepcaveincursion", ForestDeepCaveIncursionLevel.class);
        LevelRegistry.registerLevel("snowdeepcaveincursion", SnowDeepCaveIncursionLevel.class);
        LevelRegistry.registerLevel("swampdeepcaveincursion", SwampDeepCaveIncursionLevel.class);
        LevelRegistry.registerLevel("desertdeepcaveincursion", DesertDeepCaveIncursionLevel.class);
        LevelRegistry.registerLevel("slimecaveincursion", SlimeCaveIncursionLevel.class);
        LevelRegistry.registerLevel("graveyardincursion", GraveyardIncursionLevel.class);
        LevelRegistry.registerLevel("spidercastleincursion", SpiderCastleIncursionLevel.class);
        LevelRegistry.registerLevel("sunarenaincursion", SunArenaIncursionLevel.class);
        LevelRegistry.registerLevel("moonarenaincursion", MoonArenaIncursionLevel.class);
        LevelRegistry.registerLevel("crystalhollowincursion", CrystalHollowIncursionLevel.class);
        LevelRegistry.registerLevel("settlementruinsincursion", SettlementRuinsIncursionLevel.class);
        LevelRegistry.registerLevel("ascendedvoid", AscendedVoidLevel.class);
    }

    @Override
    protected void onRegistryClose() {
    }

    public static int registerLevel(String stringID, Class<? extends Level> levelClass) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register levels");
        }
        try {
            return instance.register(stringID, new LevelRegistryElement(levelClass));
        }
        catch (NoSuchMethodException e) {
            System.err.println("Could not register level " + levelClass.getSimpleName() + ": Missing constructor with parameters: LevelIdentifier, int (width), int (height), WorldEntity");
            return -1;
        }
    }

    public static Level getNewLevel(int id, LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        try {
            return (Level)((LevelRegistryElement)instance.getElement(id)).newInstance(identifier, width, height, worldEntity);
        }
        catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getLevelID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static String getLevelStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static int getLevelID(Class<? extends Level> clazz) {
        return instance.getElementID(clazz);
    }

    public static Stream<String> streamLevelStringIDs() {
        return instance.streamElements().map(IDDataContainer::getStringID);
    }

    protected static class LevelRegistryElement
    extends ClassIDDataContainer<Level> {
        public LevelRegistryElement(Class<? extends Level> levelClass) throws NoSuchMethodException {
            super(levelClass, LevelIdentifier.class, Integer.TYPE, Integer.TYPE, WorldEntity.class);
        }
    }
}

