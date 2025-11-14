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
import necesse.level.gameObject.MinecartTrackObject;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.presets.ChieftainsArenaPreset;

public class ChieftainBossWorldPreset
extends BiomeCenterWorldPreset {
    public Dimension size = new Dimension(40, 40);

    public ChieftainBossWorldPreset() {
        super(BiomeRegistry.PLAINS);
        this.randomAttempts = 50;
        this.sectionMaxRegionCount = 400;
        this.sectionMinRegionCount = 36;
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER);
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
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        LinesGeneration lg = new LinesGeneration(centerTileX, centerTileY);
        int armAngle = random.nextInt(360);
        int arms = 4;
        int anglePerArm = 360 / arms;
        BiPredicate<LinesGeneration, Integer> isValidArm = new BiPredicate<LinesGeneration, Integer>(){

            @Override
            public boolean test(LinesGeneration arm, Integer padding) {
                return ChieftainBossWorldPreset.this.isTileWithinBounds(arm.x2, arm.y2, presetsRegion, padding) && generatorStack.getLazyBiomeID(arm.x2, arm.y2) == ChieftainBossWorldPreset.this.biome.getID();
            }
        };
        for (int i = 0; i < arms; ++i) {
            LinesGeneration lastArm = lg.addMultiArm(random, armAngle += anglePerArm, 15, random.getIntBetween(150, 200), 5.0f, 10.0f, 5.0f, 6.0f, armLG -> !isValidArm.test((LinesGeneration)armLG, 15));
            if (isValidArm.test(lastArm, 10)) continue;
            lg.removeLastLine();
        }
        if (!lg.getRoot().isEmpty()) {
            PointHashSet tiles = Performance.record(performanceTimer, "getDiamondPoints", lg::getDiamondPoints);
            RegionTileWorldPresetGenerator tileGenerator = new RegionTileWorldPresetGenerator();
            final int gravelID = TileRegistry.getTileID("graveltile");
            final int graniteTileID = TileRegistry.getTileID("graniterocktile");
            final int crateObjectID = ObjectRegistry.getObjectID("crate");
            Performance.record(performanceTimer, "addTileGenerator", () -> {
                Comparator<Point> comparator = Comparator.comparingInt(e -> e.x);
                comparator = comparator.thenComparingInt(e -> e.y);
                tiles.stream().sorted(comparator).forEach(tile -> tileGenerator.addTile(tile.x, tile.y, new RegionTileWorldPresetGenerator.TilePlaceFunction(){

                    @Override
                    public void place(GameRandom random, Level level, int tileX, int tileY, PerformanceTimerManager timer) {
                        Performance.record(timer, "setTile", () -> {
                            if (random.getChance(0.25f)) {
                                level.setTile(tileX, tileY, gravelID);
                            } else {
                                level.setTile(tileX, tileY, graniteTileID);
                            }
                            if (!(level.getObject(tileX, tileY) instanceof MinecartTrackObject)) {
                                level.setObject(tileX, tileY, 0);
                                if (random.getChance(0.04f)) {
                                    level.setObject(tileX, tileY, crateObjectID);
                                }
                            }
                        });
                    }
                }));
            });
            tileGenerator.addToRegion(this, presetsRegion, placeable -> placeable.setDebugName("Chieftain branches"));
        }
        final int startTileX = centerTileX - this.size.width / 2;
        final int startTileY = centerTileY - this.size.height / 2;
        presetsRegion.addPreset((WorldPreset)this, startTileX, startTileY, this.size, new String[]{"minibiomes", "loot"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

            @Override
            public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                ChieftainsArenaPreset preset = new ChieftainsArenaPreset();
                int applyTileX = startTileX + ChieftainBossWorldPreset.this.size.width / 2 - preset.width / 2;
                int applyTileY = startTileY + ChieftainBossWorldPreset.this.size.height / 2 - preset.height / 2;
                WorldPreset.ensureRegionsAreGenerated(level, applyTileX, applyTileY, ChieftainBossWorldPreset.this.size.width, ChieftainBossWorldPreset.this.size.height);
                preset.applyToLevel(level, applyTileX, applyTileY);
            }
        });
    }
}

