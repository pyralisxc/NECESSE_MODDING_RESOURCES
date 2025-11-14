/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Comparator;
import java.util.function.BiPredicate;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.BiomeCenterWorldPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.presets.TheCursedCroneArenaPreset;

public class CursedCroneBossWorldPreset
extends BiomeCenterWorldPreset {
    public Dimension size = new Dimension(40, 40);

    public CursedCroneBossWorldPreset() {
        super(BiomeRegistry.PLAINS);
        this.randomAttempts = 50;
        this.sectionMaxRegionCount = 400;
        this.sectionMinRegionCount = 36;
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
    }

    @Override
    public boolean isValidSectionRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return true;
    }

    @Override
    public boolean isValidFinalRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        int startTileX = centerTileX - this.size.width / 2;
        int startTileY = centerTileY - this.size.height / 2;
        return this.runCornerCheck(startTileX, startTileY, this.size.width, this.size.height, (tileX, tileY) -> generatorStack.getLazyBiomeID(tileX, tileY) == this.biome.getID());
    }

    @Override
    public void onFoundRegion(int regionX, int regionY, GameRandom random, final LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int[] riverAngles;
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        LinesGeneration lg = new LinesGeneration(centerTileX, centerTileY);
        BiPredicate<LinesGeneration, Integer> isValidArm = new BiPredicate<LinesGeneration, Integer>(){

            @Override
            public boolean test(LinesGeneration arm, Integer padding) {
                return CursedCroneBossWorldPreset.this.isTileWithinBounds(arm.x2, arm.y2, presetsRegion, padding) && generatorStack.getLazyBiomeID(arm.x2, arm.y2) == CursedCroneBossWorldPreset.this.biome.getID();
            }
        };
        for (int armAngle : riverAngles = new int[]{45, random.getChance(0.5f) ? -1 : 45, 135, random.getChance(0.5f) ? -1 : 135, 225, random.getChance(0.5f) ? -1 : 225, 270, random.getChance(0.5f) ? -1 : 270, 315, random.getChance(0.5f) ? -1 : 315}) {
            LinesGeneration lastArm;
            if (armAngle == -1 || isValidArm.test(lastArm = lg.addMultiArm(random, armAngle, 15, random.getIntBetween(150, 200), 5.0f, 10.0f, 5.0f, 6.0f, armLG -> !isValidArm.test((LinesGeneration)armLG, 15)), 10)) continue;
            lg.removeLastLine();
        }
        if (!lg.getRoot().isEmpty()) {
            PointHashSet tiles = Performance.record(performanceTimer, "getDiamondPoints", lg::getDiamondPoints);
            RegionTileWorldPresetGenerator tileGenerator = new RegionTileWorldPresetGenerator();
            final int waterGrassID = ObjectRegistry.getObjectID("watergrass");
            final int waterLanternID = ObjectRegistry.getObjectID("waterlantern");
            Performance.record(performanceTimer, "addTileGenerator", () -> {
                Comparator<Point> comparator = Comparator.comparingInt(e -> e.x);
                comparator = comparator.thenComparingInt(e -> e.y);
                tiles.stream().sorted(comparator).forEach(tile -> tileGenerator.addTile(tile.x, tile.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                    @Override
                    public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                        Performance.record(timer, "setTile", () -> {
                            level.setTile(tileX, tileY, TileRegistry.spiritWaterID);
                            if (random.getChance(0.1f)) {
                                level.setObject(tileX, tileY, waterGrassID);
                            } else if (random.getChance(0.02f)) {
                                level.setObject(tileX, tileY, waterLanternID);
                            }
                        });
                    }
                }));
            });
            tileGenerator.forEachRegion(new RegionTileWorldPresetGenerator.ForEachFunction(){

                @Override
                public void handle(int regionX, int regionY, LevelPresetsRegion.WorldPresetPlaceFunction placeFunction) {
                    int tileX = GameMath.getTileCoordByRegion(regionX);
                    int tileY = GameMath.getTileCoordByRegion(regionY);
                    presetsRegion.addPreset((WorldPreset)CursedCroneBossWorldPreset.this, tileX, tileY, new Dimension(16, 16), new String[]{"minibiomes", "loot"}, placeFunction);
                }
            });
        }
        final int startTileX = centerTileX - this.size.width / 2;
        final int startTileY = centerTileY - this.size.height / 2;
        presetsRegion.addPreset((WorldPreset)this, startTileX, startTileY, this.size, new String[]{"minibiomes", "loot"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

            @Override
            public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                TheCursedCroneArenaPreset preset = new TheCursedCroneArenaPreset();
                int applyTileX = startTileX + CursedCroneBossWorldPreset.this.size.width / 2 - preset.width / 2;
                int applyTileY = startTileY + CursedCroneBossWorldPreset.this.size.height / 2 - preset.height / 2;
                WorldPreset.ensureRegionsAreGenerated(level, applyTileX, applyTileY, CursedCroneBossWorldPreset.this.size.width, CursedCroneBossWorldPreset.this.size.height);
                preset.applyToLevel(level, applyTileX, applyTileY);
            }
        });
    }
}

