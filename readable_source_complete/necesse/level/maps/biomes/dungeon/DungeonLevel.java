/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.dungeon;

import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.ModularGeneration;
import necesse.level.maps.generationModules.PlacedPreset;
import necesse.level.maps.presets.BotanistLaboratoryPreset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridor1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridor2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridorDouble1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonCorridorDouble2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonEnd1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonEnd2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonIntersectionDouble1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonIntersectionDouble2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonIntersectionPreset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom2Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom3Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonRoom4Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonStartPreset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonTSection1Preset;
import necesse.level.maps.presets.modularPresets.dungeonPresets.DungeonTSection2Preset;

public class DungeonLevel
extends Level {
    public static final int DUNGEON_SIZE = 200;
    public static final int ROOM_SIZE = 15;

    public DungeonLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DungeonLevel(LevelIdentifier identifier, WorldEntity worldEntity) {
        super(identifier, 200, 200, worldEntity);
        this.baseBiome = BiomeRegistry.DUNGEON;
        this.isCave = true;
        this.generateLevel();
    }

    @Override
    public void onLoadingComplete() {
        super.onLoadingComplete();
        this.baseBiome = BiomeRegistry.DUNGEON;
    }

    @Override
    public GameMessage getSetSpawnError(int x, int y, ServerClient client) {
        return new LocalMessage("misc", "spawndungeon");
    }

    public void generateLevel() {
        int cellsWidth = ModularGeneration.getCellsSize(this.tileWidth, 15);
        int cellsHeight = ModularGeneration.getCellsSize(this.tileHeight, 15);
        ModularGeneration mg = new ModularGeneration(this, cellsWidth, cellsHeight, 15, 3, 2);
        AtomicInteger chestRotation = new AtomicInteger();
        mg.setStartPreset(new DungeonStartPreset(mg.random));
        mg.addPreset(new DungeonCorridor1Preset(mg.random), 1);
        mg.addPreset(new DungeonCorridor2Preset(mg.random), 1);
        mg.addPreset(new DungeonCorridorDouble1Preset(mg.random), 1);
        mg.addPreset(new DungeonCorridorDouble2Preset(mg.random), 1);
        mg.addPreset(new DungeonEnd1Preset(mg.random, chestRotation), 1);
        mg.addPreset(new DungeonEnd2Preset(mg.random, chestRotation), 1);
        mg.addPreset(new DungeonIntersectionPreset(mg.random), 1);
        mg.addPreset(new DungeonRoom1Preset(mg.random, chestRotation), 2);
        mg.addPreset(new DungeonRoom2Preset(mg.random), 1);
        mg.addPreset(new DungeonRoom3Preset(mg.random), 1);
        mg.addPreset(new DungeonRoom4Preset(mg.random, chestRotation), 2);
        mg.addPreset(new DungeonTSection1Preset(mg.random), 1);
        mg.addPreset(new DungeonTSection2Preset(mg.random), 1);
        mg.addPreset(new DungeonIntersectionDouble1Preset(mg.random), 1);
        mg.addPreset(new DungeonIntersectionDouble2Preset(mg.random), 1);
        int wall = ObjectRegistry.getObjectID("dungeonwall");
        int tile = TileRegistry.getTileID("dungeonfloor");
        for (int x = 0; x < this.tileWidth; ++x) {
            for (int y = 0; y < this.tileHeight; ++y) {
                this.setTile(x, y, tile);
                this.setObject(x, y, wall);
            }
        }
        this.liquidManager.calculateShores();
        mg.initGeneration(0, 0);
        mg.tickGeneration(0, 0, 20);
        mg.addPreset(new BotanistLaboratoryPreset(mg.random), 100, 1);
        mg.tickGeneration(0, 0, -1);
        mg.endGeneration();
        mg.fixHeuristic();
        PlacedPreset replace = null;
        int desiredHeuristic = 6;
        for (PlacedPreset p : mg.getPlacedPresets()) {
            if (p.heuristic == 0 || p.preset.sectionWidth != 1 || p.preset.sectionHeight != 1 || replace != null && (p.heuristic <= replace.heuristic || p.heuristic > desiredHeuristic) && (p.heuristic >= replace.heuristic || p.heuristic < desiredHeuristic)) continue;
            replace = p;
            if (replace.heuristic != desiredHeuristic) continue;
            break;
        }
        if (replace != null) {
            mg.replacePreset(replace.cell, new DungeonStartPreset(mg.random), false, false, 0, 0);
            int centerX = mg.getCellRealX(replace.cell.x) + 7;
            int centerY = mg.getCellRealY(replace.cell.y) + 7;
            this.setObject(centerX, centerY, ObjectRegistry.getObjectID("dungeonentrance"));
        } else {
            System.out.println("Could not find position for dungeon boss preset");
        }
    }
}

