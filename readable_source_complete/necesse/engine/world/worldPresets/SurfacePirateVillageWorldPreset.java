/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.VillageModularGeneration;
import necesse.level.maps.levelData.OneWorldPirateVillageData;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageBossPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillagePathPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom1HPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom1VPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom2Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageRoom3Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWalkway1HPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWalkway1VPreset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWall1Preset;
import necesse.level.maps.presets.modularPresets.vilagePresets.pirateVillagePresets.PirateVillageWall2Preset;
import necesse.level.maps.regionSystem.Region;

public class SurfacePirateVillageWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = SurfacePirateVillageWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.FOREST, 0.001f);
        for (int i = 0; i < total; ++i) {
            final int size = random.getIntBetween(60, 80);
            final Dimension dimension = new Dimension(size, size);
            int padding = 35;
            Dimension paddingDimension = new Dimension(size + padding * 2, size + padding * 2);
            final Point tile = SurfacePirateVillageWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.FOREST, 200, dimension, new String[]{"villagespadding", "villages"}, new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return SurfacePirateVillageWorldPreset.this.runGridCheck(tileX, tileY, size, size, 20, new WorldPreset.ValidTilePredicate(){

                        @Override
                        public boolean isValidPosition(int tileX, int tileY) {
                            return !generatorStack.isSurfaceOcean(tileX, tileY) && generatorStack.getLazyBiomeID(tileX, tileY) == BiomeRegistry.FOREST.getID();
                        }
                    });
                }
            });
            if (tile == null) continue;
            int pirateRegionsTilePadding = 30;
            RegionTileWorldPresetGenerator tileGenerator = new RegionTileWorldPresetGenerator();
            int pirateRegionsStartTileX = tile.x - pirateRegionsTilePadding;
            int pirateRegionsStartTileY = tile.y - pirateRegionsTilePadding;
            int pirateRegionsEndTileX = tile.x + dimension.width + pirateRegionsTilePadding * 2 - 1;
            int pirateRegionsEndTileY = tile.y + dimension.height + pirateRegionsTilePadding * 2 - 1;
            tileGenerator.onRegionsGeneratedByTile(pirateRegionsStartTileX, pirateRegionsStartTileY, pirateRegionsEndTileX, pirateRegionsEndTileY, new RegionTileWorldPresetGenerator.RegionGeneratedFunction(){

                @Override
                public void onRegionGenerated(GameRandom random, Level level, Region region, PerformanceTimerManager timer) {
                    region.isPirateVillageRegion = true;
                }
            });
            tileGenerator.addToRegion(this, presetsRegion, p -> p.setDebugName("Pirate Region Setter"));
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, dimension, "villages", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    AtomicInteger chestRotation = new AtomicInteger();
                    AtomicInteger displayStandRotation = new AtomicInteger();
                    OneWorldPirateVillageData.getPirateVillageData(level, true).addPirateTileRectangle(new Rectangle(tile.x, tile.y, dimension.width, dimension.height));
                    VillageModularGeneration mg = new VillageModularGeneration(level, dimension.width / 3, dimension.height / 3, 3, 3, 1){

                        @Override
                        public Point getStartCell() {
                            return new Point(this.cellsWidth / 2, this.cellsHeight / 2);
                        }
                    };
                    PirateVillageBossPreset startPreset = new PirateVillageBossPreset(mg.random);
                    mg.setStartPreset(startPreset);
                    int xOffset = tile.x;
                    int yOffset = tile.y;
                    mg.addPreset(new PirateVillagePathPreset(true, true, true, true), 6);
                    mg.addPreset(new PirateVillagePathPreset(true, false, true, false), 3);
                    mg.addPreset(new PirateVillagePathPreset(false, true, false, true), 3);
                    mg.addPreset(new PirateVillageRoom1HPreset(mg.random, chestRotation), 3);
                    mg.addPreset(new PirateVillageRoom1VPreset(mg.random, chestRotation), 3);
                    mg.addPreset(new PirateVillageWalkway1HPreset(mg.random, chestRotation), 2);
                    mg.addPreset(new PirateVillageWalkway1VPreset(mg.random, chestRotation), 2);
                    mg.addPreset(new PirateVillageRoom2Preset(mg.random, chestRotation), 4);
                    mg.addPreset(new PirateVillageRoom3Preset(mg.random, displayStandRotation), 10, 2);
                    mg.initGeneration(xOffset, yOffset);
                    mg.tickGeneration(xOffset, yOffset, size * 3);
                    mg.addFillPreset(new PirateVillageWall1Preset(mg.random), 5);
                    mg.addFillPreset(new PirateVillageWall2Preset(mg.random), 5);
                    int fillTicks = size / 10;
                    mg.tickFillGeneration(xOffset, yOffset, fillTicks);
                    String[] mobList = new String[]{"piraterecruit"};
                    mg.addRandomMobs(xOffset, yOffset, mobList, size * 4, size / 3);
                    mg.endGeneration();
                }
            });
            presetsRegion.addOccupiedSpaceBoard("villagespadding", new Rectangle(tile.x - padding, tile.y - padding, paddingDimension.width, paddingDimension.height));
        }
    }
}

