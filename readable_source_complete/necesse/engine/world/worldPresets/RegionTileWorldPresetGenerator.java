/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.lang.invoke.LambdaMetafactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionPointsSet;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class RegionTileWorldPresetGenerator {
    private final HashMap<Long, RegionPlace> placeFunctions = new HashMap();

    public void onRegionGenerated(int regionX, int regionY, RegionGeneratedFunction function) {
        long key = GameMath.getUniqueLongKey(regionX, regionY);
        this.placeFunctions.compute((Long)Long.valueOf((long)key), (BiFunction<Long, RegionPlace, RegionPlace>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, lambda$onRegionGenerated$0(java.lang.Long necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator$RegionPlace ), (Ljava/lang/Long;Lnecesse/engine/world/worldPresets/RegionTileWorldPresetGenerator$RegionPlace;)Lnecesse/engine/world/worldPresets/RegionTileWorldPresetGenerator$RegionPlace;)()).regionGeneratedFunctions.add(function);
    }

    public void onRegionsGenerated(int startRegionX, int startRegionY, int endRegionX, int endRegionY, RegionGeneratedFunction function) {
        for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
            for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                this.onRegionGenerated(regionX, regionY, function);
            }
        }
    }

    public void onRegionsGeneratedByTile(int startTileX, int startTileY, int endTileX, int endTileY, RegionGeneratedFunction function) {
        int startRegionX = GameMath.getRegionCoordByTile(startTileX);
        int startRegionY = GameMath.getRegionCoordByTile(startTileY);
        int endRegionX = GameMath.getRegionCoordByTile(endTileX);
        int endRegionY = GameMath.getRegionCoordByTile(endTileY);
        this.onRegionsGenerated(startRegionX, startRegionY, endRegionX, endRegionY, function);
    }

    public void addTile(int tileX, int tileY, TilePlaceFunction placeFunction) {
        int regionX = GameMath.getRegionCoordByTile(tileX);
        int regionY = GameMath.getRegionCoordByTile(tileY);
        long key = GameMath.getUniqueLongKey(regionX, regionY);
        this.placeFunctions.compute((Long)Long.valueOf((long)key), (BiFunction<Long, RegionPlace, RegionPlace>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, lambda$addTile$1(java.lang.Long necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator$RegionPlace ), (Ljava/lang/Long;Lnecesse/engine/world/worldPresets/RegionTileWorldPresetGenerator$RegionPlace;)Lnecesse/engine/world/worldPresets/RegionTileWorldPresetGenerator$RegionPlace;)()).tilePlaces.add(new TilePlace(tileX, tileY, placeFunction));
    }

    public void addToRegion(WorldPreset preset, LevelPresetsRegion presetsRegion, Consumer<LevelPresetsRegion.PlaceableWorldPreset> modifier) {
        for (Map.Entry<Long, RegionPlace> entry : this.placeFunctions.entrySet()) {
            long key = entry.getKey();
            final int regionX = GameMath.getXFromUniqueLongKey(key);
            final int regionY = GameMath.getYFromUniqueLongKey(key);
            final RegionPlace value = entry.getValue();
            RegionPointsSet regionsSet = new RegionPointsSet();
            regionsSet.addRegion(regionX, regionY);
            LevelPresetsRegion.PlaceableWorldPreset placeableWorldPreset = presetsRegion.addPresetToRegions(preset, regionsSet, new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    Region region;
                    if (!value.regionGeneratedFunctions.isEmpty() && (region = level.regionManager.getRegion(regionX, regionY, true)) != null) {
                        for (RegionGeneratedFunction regionGeneratedFunction : value.regionGeneratedFunctions) {
                            regionGeneratedFunction.onRegionGenerated(random, level, region, timer);
                        }
                    }
                    for (TilePlace placeFunctions : value.tilePlaces) {
                        placeFunctions.placeFunction.place(random, level, placeFunctions.tileX, placeFunctions.tileY, timer);
                    }
                }
            });
            if (modifier == null) continue;
            modifier.accept(placeableWorldPreset);
        }
    }

    public void addToRegion(WorldPreset preset, LevelPresetsRegion presetsRegion) {
        this.addToRegion(preset, presetsRegion, null);
    }

    public void forEachRegion(ForEachFunction handler) {
        for (Map.Entry<Long, RegionPlace> entry : this.placeFunctions.entrySet()) {
            long key = entry.getKey();
            final int regionX = GameMath.getXFromUniqueLongKey(key);
            final int regionY = GameMath.getYFromUniqueLongKey(key);
            final RegionPlace value = entry.getValue();
            handler.handle(regionX, regionY, new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    Region region;
                    if (!value.regionGeneratedFunctions.isEmpty() && (region = level.regionManager.getRegion(regionX, regionY, true)) != null) {
                        for (RegionGeneratedFunction regionGeneratedFunction : value.regionGeneratedFunctions) {
                            regionGeneratedFunction.onRegionGenerated(random, level, region, timer);
                        }
                    }
                    for (TilePlace placeFunctions : value.tilePlaces) {
                        placeFunctions.placeFunction.place(random, level, placeFunctions.tileX, placeFunctions.tileY, timer);
                    }
                }
            });
        }
    }

    private static /* synthetic */ RegionPlace lambda$addTile$1(Long k, RegionPlace v) {
        if (v == null) {
            v = new RegionPlace();
        }
        return v;
    }

    private static /* synthetic */ RegionPlace lambda$onRegionGenerated$0(Long k, RegionPlace v) {
        if (v == null) {
            v = new RegionPlace();
        }
        return v;
    }

    private static class RegionPlace {
        public LinkedList<RegionGeneratedFunction> regionGeneratedFunctions = new LinkedList();
        public LinkedList<TilePlace> tilePlaces = new LinkedList();

        private RegionPlace() {
        }
    }

    @FunctionalInterface
    public static interface RegionGeneratedFunction {
        public void onRegionGenerated(GameRandom var1, Level var2, Region var3, PerformanceTimerManager var4);
    }

    private static class TilePlace {
        public final int tileX;
        public final int tileY;
        public final TilePlaceFunction placeFunction;

        public TilePlace(int tileX, int tileY, TilePlaceFunction placeFunction) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.placeFunction = placeFunction;
        }
    }

    @FunctionalInterface
    public static interface TilePlaceFunction {
        public void place(GameRandom var1, Level var2, int var3, int var4, PerformanceTimerManager var5);
    }

    @FunctionalInterface
    public static interface ForEachFunction {
        public void handle(int var1, int var2, LevelPresetsRegion.WorldPresetPlaceFunction var3);
    }
}

