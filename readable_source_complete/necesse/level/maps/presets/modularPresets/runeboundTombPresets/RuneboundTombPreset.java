/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTombPresets;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.presets.modularPresets.ModularPreset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombCrossPreset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombDeadEnd1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombDeadEnd2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallway1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallway2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallwayT1Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombHallwayT2Preset;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombLongHallwayPreset;

public class RuneboundTombPreset
extends ModularPreset {
    public GameRandom random;
    protected ArrayList<String> runeboundMobStrings = new ArrayList<String>(){
        {
            this.add("runeboundbrute");
            this.add("runeboundshaman");
            this.add("runeboundtrapper");
        }
    };

    public RuneboundTombPreset(int sectionWidth, int sectionHeight, int openingSize, int openingDepth, GameRandom random) {
        super(sectionWidth, sectionHeight, 10, openingSize, openingDepth);
        this.random = random;
        this.overlap = true;
    }

    public RuneboundTombPreset(int sectionWidth, int sectionHeight, GameRandom random) {
        this(sectionWidth, sectionHeight, 0, 0, random);
    }

    public RuneboundTombPreset(int sectionWidth, int sectionHeight) {
        this(sectionWidth, sectionHeight, null);
    }

    @Override
    protected RuneboundTombPreset newModularObject(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        return new RuneboundTombPreset(sectionWidth, sectionHeight, openingSize, openingDepth, this.random);
    }

    public static void generateRuneboundTombOnLevel(Level level, GameRandom random, PresetGeneration presets) {
        block0: for (int i = 0; i < 3; ++i) {
            ModularGeneration mg = new ModularGeneration(level, random.getIntBetween(3, 4), random.getIntBetween(3, 4), 10, 0, 0){

                @Override
                public Point getStartCell() {
                    return new Point((this.cellsWidth - this.startPreset.sectionWidth) / 2, (this.cellsHeight - this.startPreset.sectionHeight) / 2);
                }
            };
            for (int j = 0; j < 50; ++j) {
                int yOffset;
                int tileWidth = mg.cellsWidth * mg.cellRes;
                int tileHeight = mg.cellsHeight * mg.cellRes;
                int xOffset = random.getIntBetween(5, level.tileWidth - tileWidth - 5);
                if (!presets.isSpaceOccupied(xOffset, yOffset = random.getIntBetween(5, level.tileHeight - tileHeight - 5), tileWidth, tileHeight)) continue;
                RuneboundTombCrossPreset runicSquare = new RuneboundTombCrossPreset(mg.random);
                mg.setStartPreset(runicSquare);
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
                presets.addOccupiedSpace(xOffset, yOffset, tileWidth, tileHeight);
                continue block0;
            }
        }
    }
}

