/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.runeboundTribePreset;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.presets.modularPresets.ModularPreset;
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

public class RuneboundTribePreset
extends ModularPreset {
    public GameRandom random;
    protected ArrayList<String> runeboundMobStrings = new ArrayList<String>(){
        {
            this.add("runeboundbrute");
            this.add("runeboundshaman");
            this.add("runeboundtrapper");
        }
    };

    public RuneboundTribePreset(int sectionWidth, int sectionHeight, int openingSize, int openingDepth, GameRandom random) {
        super(sectionWidth, sectionHeight, 12, openingSize, openingDepth);
        this.random = random;
        this.overlap = true;
    }

    public RuneboundTribePreset(int sectionWidth, int sectionHeight, GameRandom random) {
        this(sectionWidth, sectionHeight, 0, 0, random);
    }

    public RuneboundTribePreset(int sectionWidth, int sectionHeight) {
        this(sectionWidth, sectionHeight, null);
    }

    @Override
    protected RuneboundTribePreset newModularObject(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        return new RuneboundTribePreset(sectionWidth, sectionHeight, openingSize, openingDepth, this.random);
    }

    public void addRuneboundMobSpawn(int tileX, int tileY) {
        this.addCustomApply(tileX, tileY, 0, (level, levelX, levelY, dir, blackboard) -> {
            Mob runeboundMob = MobRegistry.getMob(this.random.getOneOf(this.runeboundMobStrings), level);
            runeboundMob.canDespawn = false;
            Point spawnLocation = Waystone.findTeleportLocation(level, levelX, levelY, runeboundMob);
            runeboundMob.onSpawned(spawnLocation.x, spawnLocation.y);
            level.entityManager.addMob(runeboundMob, spawnLocation.x, spawnLocation.y);
            return (level1, presetX, presetY) -> runeboundMob.remove();
        });
    }

    public static void generateRuneboundTribeOnLevel(Level level, GameRandom random, PresetGeneration presets) {
        block0: for (int i = 0; i < 6; ++i) {
            ModularGeneration mg = new ModularGeneration(level, random.getIntBetween(2, 3), random.getIntBetween(2, 3), 12, 0, 0){

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
                RuneboundTribeRunicBoulder1Preset runicSquare = new RuneboundTribeRunicBoulder1Preset(mg.random);
                mg.setStartPreset(runicSquare);
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
                presets.addOccupiedSpace(xOffset, yOffset, tileWidth, tileHeight);
                continue block0;
            }
        }
    }
}

