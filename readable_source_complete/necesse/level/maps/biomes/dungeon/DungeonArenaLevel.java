/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.dungeon;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.dungeon.DungeonLevel;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.furniturePresets.BedDresserPreset;
import necesse.level.maps.presets.furniturePresets.BenchPreset;
import necesse.level.maps.presets.furniturePresets.BookshelfClockPreset;
import necesse.level.maps.presets.furniturePresets.BookshelvesPreset;
import necesse.level.maps.presets.furniturePresets.CabinetsPreset;
import necesse.level.maps.presets.furniturePresets.DeskBookshelfPreset;
import necesse.level.maps.presets.furniturePresets.DinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.DisplayStandClockPreset;
import necesse.level.maps.presets.furniturePresets.ModularDinnerTablePreset;
import necesse.level.maps.presets.furniturePresets.ModularTablesPreset;
import necesse.level.maps.presets.set.FurnitureSet;

public class DungeonArenaLevel
extends DungeonLevel {
    public static final int DUNGEON_BOSS_SIZE = 40;
    public static final int DUNGEON_BOSS_EDGE = 40;
    private static final int TOTAL_SIZE = 120;

    public DungeonArenaLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public DungeonArenaLevel(LevelIdentifier identifier, WorldEntity worldEntity) {
        super(identifier, 120, 120, worldEntity);
        this.baseBiome = BiomeRegistry.DUNGEON;
        this.isCave = true;
        this.isProtected = true;
        this.generateLevel();
    }

    @Override
    public void generateLevel() {
        int y;
        int x;
        GameRandom random = new GameRandom(this.getSeed());
        int wall = ObjectRegistry.getObjectID("dungeonwall");
        int floor = TileRegistry.getTileID("dungeonfloor");
        int lamp = ObjectRegistry.getObjectID("dungeoncandelabra");
        int centerX = this.tileWidth / 2;
        int centerY = this.tileHeight / 2;
        for (x = 0; x < this.tileWidth; ++x) {
            for (y = 0; y < this.tileHeight; ++y) {
                double dist = new Point2D.Float(centerX, centerY).distance(x, y);
                if (dist <= 20.5) {
                    this.setObject(x, y, 0);
                    continue;
                }
                this.setObject(x, y, wall);
            }
        }
        for (x = 0; x < this.tileWidth; ++x) {
            for (y = 0; y < this.tileHeight; ++y) {
                this.setTile(x, y, floor);
            }
        }
        TicketSystemList<Preset> furniture = new TicketSystemList<Preset>();
        furniture.addObject(100, (Object)new BedDresserPreset(FurnitureSet.dungeon, 2));
        furniture.addObject(100, (Object)new BenchPreset(FurnitureSet.dungeon, 2));
        furniture.addObject(100, (Object)new BookshelfClockPreset(FurnitureSet.dungeon, 2));
        furniture.addObject(100, (Object)new BookshelvesPreset(FurnitureSet.dungeon, 2, 3));
        furniture.addObject(100, (Object)new CabinetsPreset(FurnitureSet.dungeon, 2, 3));
        furniture.addObject(100, (Object)new DeskBookshelfPreset(FurnitureSet.dungeon, 2));
        furniture.addObject(100, (Object)new DinnerTablePreset(FurnitureSet.dungeon, 2));
        furniture.addObject(100, (Object)new DisplayStandClockPreset(FurnitureSet.dungeon, 2, null, null, new Object[0]));
        furniture.addObject(100, (Object)new ModularDinnerTablePreset(FurnitureSet.dungeon, 2, 1));
        furniture.addObject(50, (Object)new ModularTablesPreset(FurnitureSet.dungeon, 2, 2, true));
        for (int x2 = 0; x2 < this.tileWidth; ++x2) {
            for (int y2 = 0; y2 < this.tileHeight; ++y2) {
                if (this.getObjectID(x2, y2) != 0 || !random.getChance(0.8f)) continue;
                GenerationTools.generateFurniture(this, random, x2, y2, furniture, pos -> pos.objectID() == 0);
            }
        }
        Point ladderPos = DungeonArenaLevel.getLadderPosition();
        this.setObject(ladderPos.x, ladderPos.y, ObjectRegistry.getObjectID("dungeonexit"));
        this.setObject(ladderPos.x - 5, ladderPos.y, lamp);
        this.setObject(ladderPos.x + 5, ladderPos.y, lamp);
        for (int i = 0; i < 5; ++i) {
            int yOffset = 6 + i * 6;
            this.setObject(ladderPos.x - 4, 75 - yOffset, lamp);
            this.setObject(ladderPos.x + 4, 75 - yOffset, lamp);
            this.setObject(ladderPos.x - 11, 75 - yOffset + 2, lamp);
            this.setObject(ladderPos.x + 11, 75 - yOffset + 2, lamp);
        }
        Mob mob = MobRegistry.getMob("voidwizard", (Level)this);
        Point2D.Float bossPos = DungeonArenaLevel.getBossPosition();
        this.entityManager.addMob(mob, bossPos.x, bossPos.y);
    }

    public static Point getLadderPosition() {
        return new Point(60, 75);
    }

    public static Point2D.Float getBossPosition() {
        return new Point2D.Float(1936.0f, 1456.0f);
    }
}

