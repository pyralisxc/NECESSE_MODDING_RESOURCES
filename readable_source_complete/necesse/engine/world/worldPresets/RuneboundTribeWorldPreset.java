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
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeCaveHouse1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeCaveHouse2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeCaveHouse3Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeCaveHouse4Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeCaveHouse5Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeCaveHouse6Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeHouse1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeHouse2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeHouse3Preset;
import necesse.level.maps.presets.modularPresets.runeboundTribePreset.RuneboundTribeRunicBoulder1Preset;

public class RuneboundTribeWorldPreset
extends WorldPreset {
    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER);
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = RuneboundTribeWorldPreset.getTotalBiomePoints(random, presetsRegion, BiomeRegistry.PLAINS, 0.017f);
        for (int i = 0; i < total; ++i) {
            final int cellsRes = 12;
            final int cellsWidth = random.getIntBetween(2, 3);
            final int cellsHeight = random.getIntBetween(2, 3);
            Dimension dimension = new Dimension(cellsWidth * cellsRes, cellsHeight * cellsRes);
            final Point tile = RuneboundTribeWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, BiomeRegistry.PLAINS, 30, dimension, new String[]{"minibiomes", "loot"}, null);
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
                    mg.setStartPreset(new RuneboundTribeRunicBoulder1Preset(mg.random));
                    mg.initGeneration(xOffset, yOffset);
                    mg.addPreset(new RuneboundTribeCaveHouse1Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeCaveHouse2Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeCaveHouse3Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeCaveHouse4Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeCaveHouse5Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeCaveHouse6Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeHouse1Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeHouse2Preset(mg.random), 75);
                    mg.addPreset(new RuneboundTribeHouse3Preset(mg.random), 75);
                    mg.tickGeneration(xOffset, yOffset, Integer.MAX_VALUE);
                    mg.endGeneration();
                }
            });
        }
    }
}

