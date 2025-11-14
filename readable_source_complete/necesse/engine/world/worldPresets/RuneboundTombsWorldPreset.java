/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombCrossPreset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombDeadEnd1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombDeadEnd2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallway1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallway2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallwayT1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallwayT2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombLongHallwayPreset;

public class RuneboundTombsWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.DEEP_CAVE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = RuneboundTombsWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.PLAINS, 0.01f);
        for (int i = 0; i < total; ++i) {
            final int cellsRes = 10;
            final int cellsWidth = random.getIntBetween(3, 4);
            final int cellsHeight = random.getIntBetween(3, 4);
            Dimension dimension = new Dimension(cellsWidth * cellsRes, cellsHeight * cellsRes);
            final Point tile = RuneboundTombsWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.PLAINS, 30, dimension, new String[]{"minibiomes", "loot"}, null);
            if (tile == null) continue;
            presetsRegion.addPreset((WorldPreset)this, tile.x, tile.y, dimension, new String[]{"minibiomes", "loot"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    ModularGeneration mg = new ModularGeneration(level, cellsWidth, cellsHeight, cellsRes, 0, 0){

                        @Override
                        public Point getStartCell() {
                            return new Point((this.cellsWidth - this.startPreset.sectionWidth) / 2, (this.cellsHeight - this.startPreset.sectionHeight) / 2);
                        }
                    };
                    int xOffset = tile.x;
                    int yOffset = tile.y;
                    mg.setStartPreset(new RuneboundTombCrossPreset(mg.random));
                    mg.initGeneration(xOffset, yOffset);
                    mg.addPreset(new RuneboundTombCrossPreset(mg.random), 75);
                    mg.addPreset(new RuneboundTombHallway1Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTombHallway2Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTombLongHallwayPreset(mg.random), 75);
                    mg.addPreset(new RuneboundTombHallwayT1Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTombHallwayT2Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTombDeadEnd1Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTombDeadEnd2Preset(mg.random), 75);
                    mg.tickGeneration(xOffset, yOffset, Integer.MAX_VALUE);
                    mg.endGeneration();
                }
            });
        }
    }
}

