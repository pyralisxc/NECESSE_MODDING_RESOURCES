/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets.modularPresets.spiderCastlePreset;

import java.awt.Point;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.presets.modularPresets.ModularPreset;
import necesse.level.maps.presets.modularPresets.abandonedMinePreset.AbandonedMineHallwayPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleBigSpiderRoomPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleCrossRoomPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleDeadEnd1Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleDeadEnd2Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleDeadEnd3Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleDeadEnd4Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleDoubleDoorHallwayPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleHallwayCornerPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleHallwayStraight1Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleHallwayStraight2Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleHallwayStraight3Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleHallwayStraight4Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleHallwayUTurnPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleLRoomPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleLibraryPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleLongHallwayPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleSecretTunnelHallwayPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleSecretTunnelRoomPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleSmallCrossRoomPreset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleTRoom1Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleTRoom2Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleTRoom3Preset;
import necesse.level.maps.presets.modularPresets.spiderCastlePreset.SpiderCastleThroneRoomPreset;

public class SpiderCastlePreset
extends ModularPreset {
    public final int wall;
    public final int floor;
    public GameRandom random;

    public SpiderCastlePreset(int sectionWidth, int sectionHeight, int openingSize, int openingDepth, GameRandom random) {
        super(sectionWidth, sectionHeight, 7, openingSize, openingDepth);
        this.random = random;
        this.overlap = true;
        this.wall = ObjectRegistry.getObjectID("deepstonewall");
        this.floor = TileRegistry.deepStoneFloorID;
    }

    public SpiderCastlePreset(int sectionWidth, int sectionHeight, GameRandom random) {
        this(sectionWidth, sectionHeight, 5, 1, random);
    }

    public SpiderCastlePreset(int sectionWidth, int sectionHeight) {
        this(sectionWidth, sectionHeight, null);
    }

    @Override
    protected SpiderCastlePreset newModularObject(int sectionWidth, int sectionHeight, int sectionRes, int openingSize, int openingDepth) {
        return new SpiderCastlePreset(sectionWidth, sectionHeight, openingSize, openingDepth, this.random);
    }

    public static Point generateSpiderCasteOnLevel(Level level, GameRandom random) {
        ModularGeneration mg = new ModularGeneration(level, random.getIntBetween(10, 12), random.getIntBetween(8, 12), 7, 5, 1){

            @Override
            public Point getStartCell() {
                return new Point((this.cellsWidth - this.startPreset.sectionWidth) / 2, (this.cellsHeight - this.startPreset.sectionHeight) / 2);
            }
        };
        int xOffset = level.tileWidth / 2 - mg.cellRes * mg.cellsWidth / 2;
        int yOffset = level.tileHeight / 2 - mg.cellRes * mg.cellsHeight / 2;
        SpiderCastleThroneRoomPreset throneRoom = new SpiderCastleThroneRoomPreset(mg.random);
        mg.setStartPreset(throneRoom);
        Point spawnCell = mg.initGeneration(xOffset, yOffset);
        int spawnLevelTileX = ModularGeneration.getCellRealPos(spawnCell.x, mg.cellRes) + mg.cellRes * throneRoom.sectionWidth / 2;
        int spawnLevelTileY = ModularGeneration.getCellRealPos(spawnCell.y, mg.cellRes) + mg.cellRes * throneRoom.sectionHeight / 2;
        mg.addPreset(new SpiderCastleHallwayCornerPreset(mg.random), 75);
        mg.addPreset(new SpiderCastleBigSpiderRoomPreset(mg.random), 50);
        mg.addPreset(new SpiderCastleCrossRoomPreset(mg.random), 25);
        mg.addPreset(new SpiderCastleSmallCrossRoomPreset(mg.random), 35);
        mg.addPreset(new SpiderCastleLibraryPreset(mg.random), 50);
        mg.addPreset(new SpiderCastleDoubleDoorHallwayPreset(mg.random), 50);
        mg.addPreset(new SpiderCastleHallwayUTurnPreset(mg.random), 50);
        mg.addPreset(new SpiderCastleLRoomPreset(mg.random), 50);
        mg.addPreset(new SpiderCastleSecretTunnelRoomPreset(mg.random), 50);
        mg.addPreset(new SpiderCastleSecretTunnelHallwayPreset(mg.random), 75);
        mg.addPreset(new SpiderCastleLongHallwayPreset(mg.random), 50);
        mg.addPreset(new SpiderCastleHallwayStraight1Preset(mg.random), 100);
        mg.addPreset(new SpiderCastleHallwayStraight2Preset(mg.random), 100);
        mg.addPreset(new SpiderCastleHallwayStraight3Preset(mg.random), 100);
        mg.addPreset(new SpiderCastleHallwayStraight4Preset(mg.random), 100);
        mg.addPreset(new SpiderCastleTRoom1Preset(mg.random), 75);
        mg.addPreset(new SpiderCastleTRoom2Preset(mg.random), 75);
        mg.addPreset(new SpiderCastleTRoom3Preset(mg.random), 75);
        mg.addPreset(new SpiderCastleDeadEnd1Preset(mg.random), 25);
        mg.addPreset(new SpiderCastleDeadEnd2Preset(mg.random), 25);
        mg.addPreset(new SpiderCastleDeadEnd3Preset(mg.random), 25);
        mg.addPreset(new SpiderCastleDeadEnd4Preset(mg.random), 25);
        mg.tickGeneration(xOffset, yOffset, Integer.MAX_VALUE);
        for (PlacedPreset placedPreset : mg.getPlacedPresets()) {
            if (!(placedPreset.preset instanceof AbandonedMineHallwayPreset)) continue;
            SpiderCastlePreset.fixHallway(level, mg, xOffset, yOffset, placedPreset);
        }
        mg.endGeneration();
        IncursionBiome.addReturnPortal(level, (float)((spawnLevelTileX + xOffset) * 32) - 0.5f, (float)((spawnLevelTileY + yOffset) * 32) + 0.5f);
        return new Point(spawnLevelTileX + xOffset, spawnLevelTileY + yOffset);
    }

    private static void fixHallway(Level level, ModularGeneration mg, int xOffset, int yOffset, PlacedPreset preset) {
        for (int i = 0; i < 4; ++i) {
            Point nextCell = mg.getNextCell(preset.cell, i);
            PlacedPreset placedPreset = mg.getPlacedPreset(nextCell);
            if (placedPreset != null) continue;
            preset.preset.closeLevel(level, 0, 0, mg.getCellRealX(preset.cell.x) + xOffset, mg.getCellRealY(preset.cell.y) + yOffset, i, mg.cellRes);
        }
    }
}

