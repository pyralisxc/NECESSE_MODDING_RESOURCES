/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.HashSet;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;

public class CaveGeneration {
    private final Level level;
    public GameRandom random;
    public final int rockTile;
    public final int rockObject;
    private boolean[] cellMap;
    private final HashSet<Point> illegalCrateTiles = new HashSet();

    public CaveGeneration(Level level, String rockTile, String rockObject) {
        this.level = level;
        this.random = new GameRandom(level.getSeed() + (long)level.getIslandDimension());
        this.rockTile = TileRegistry.getTileID(rockTile);
        this.rockObject = ObjectRegistry.getObjectID(rockObject);
    }

    public boolean[] generateLevel() {
        return this.generateLevel(0.42f, 4, 3, 4);
    }

    public boolean[] generateLevel(float rockChance, int cellDeathLimit, int cellBirthLimit, int cellIterations) {
        GameTile rockTile = TileRegistry.getTile(this.rockTile);
        for (int x = 0; x < this.level.tileWidth; ++x) {
            for (int y = 0; y < this.level.tileHeight; ++y) {
                rockTile.placeTile(this.level, x, y, false);
            }
        }
        this.cellMap = GenerationTools.generateRandomCellMap(this.random, this.level.tileWidth, this.level.tileHeight, rockChance);
        this.doCellularAutomaton(cellDeathLimit, cellBirthLimit, cellIterations);
        this.updateCellMap();
        return this.cellMap;
    }

    public void doCellularAutomaton(int cellDeathLimit, int cellBirthLimit, int cellIterations) {
        this.cellMap = GenerationTools.doCellularAutomaton(this.cellMap, this.level.tileWidth, this.level.tileHeight, cellDeathLimit, cellBirthLimit, true, cellIterations);
    }

    public void updateCellMap() {
        GameObject rockObject = ObjectRegistry.getObject(this.rockObject);
        for (int x = 0; x < this.level.tileWidth; ++x) {
            for (int y = 0; y < this.level.tileHeight; ++y) {
                if (!this.cellMap[x + y * this.level.tileWidth]) continue;
                rockObject.placeObject(this.level, x, y, 0, false);
            }
        }
    }

    public void addIllegalCrateTile(int x, int y) {
        this.illegalCrateTiles.add(new Point(x, y));
    }

    public void generateRandomSingleRocks(int object, float chance) {
        for (int x = 0; x < this.level.tileWidth; ++x) {
            for (int y = 0; y < this.level.tileHeight; ++y) {
                GameObject rock;
                GameTile tile = this.level.getTile(x, y);
                if (!tile.isLiquid && tile.getID() != this.rockTile || !this.random.getChance(tile.isLiquid ? chance * 3.0f : chance) || (rock = ObjectRegistry.getObject(object)).canPlace(this.level, x, y, 0, false) != null) continue;
                rock.placeObject(this.level, x, y, 0, false);
            }
        }
    }

    public void generateRandomCrates(float chance, int ... crateObjects) {
        for (int x = 0; x < this.level.tileWidth; ++x) {
            for (int y = 0; y < this.level.tileHeight; ++y) {
                int crateID;
                GameObject crate;
                if (!((this.level.getObjectID(x - 1, y) != 0 && !this.isCrate(this.level.getObjectID(x - 1, y), crateObjects) || this.level.getObjectID(x + 1, y) != 0 && !this.isCrate(this.level.getObjectID(x + 1, y), crateObjects) || this.level.getObjectID(x, y - 1) != 0 && !this.isCrate(this.level.getObjectID(x, y - 1), crateObjects) || this.level.getObjectID(x, y + 1) != 0 && !this.isCrate(this.level.getObjectID(x, y + 1), crateObjects)) && this.random.getChance(chance) && !this.illegalCrateTiles.contains(new Point(x, y)) && (crate = ObjectRegistry.getObject(crateID = crateObjects[this.random.nextInt(crateObjects.length)])).canPlace(this.level, x, y, 0, false) == null)) continue;
                crate.placeObject(this.level, x, y, 0, false);
            }
        }
    }

    private boolean isCrate(int object, int ... crateObjects) {
        for (int crateObject : crateObjects) {
            if (object != crateObject) continue;
            return true;
        }
        return false;
    }

    public int generateGuaranteedOreVeins(int places, int minVeinSize, int maxVeinSize, int oreObject) {
        GameObject obj = ObjectRegistry.getObject(oreObject);
        return GenerationTools.generateGuaranteedRandomVeins(this.level, this.random, places, minVeinSize, maxVeinSize, (level, tileX, tileY) -> level.getObjectID(tileX, tileY) == this.rockObject, (level, tileX, tileY) -> obj.placeObject(level, tileX, tileY, 0, false));
    }

    public void generateOreVeins(float veinsPerChunk, int minVeinSize, int maxVeinSize, int oreObject) {
        GenerationTools.generateRandomVeins(this.level, this.random, veinsPerChunk, minVeinSize, maxVeinSize, -1, -1, 0.0f, oreObject, this.rockObject, 1.0f, true, false);
    }

    public void generateTileVeins(float veinsPerChunk, int minVeinSize, int maxVeinSize, int tile, int objectChange) {
        GenerationTools.generateRandomVeins(this.level, this.random, veinsPerChunk, minVeinSize, maxVeinSize, tile, -1, 1.0f, objectChange, -1, 1.0f, true, false);
    }

    public void generateSmoothOreVeins(float veinsPerChunk, int minVeinSize, int maxVeinSize, int oreObject) {
        GameObject obj = ObjectRegistry.getObject(oreObject);
        GenerationTools.generateRandomPoints(this.level, this.random, veinsPerChunk, pos -> new LinesGeneration(pos.x, pos.y).addRandomArms(this.random, 2, (float)minVeinSize / 2.0f, (float)maxVeinSize / 2.0f, (float)minVeinSize / 2.0f, (float)maxVeinSize / 2.0f).doCellularAutomaton(this.random).forEachTile(this.level, (level, tileX, tileY) -> {
            if (level.getObjectID(tileX, tileY) == this.rockObject) {
                obj.placeObject(level, tileX, tileY, 0, false);
            }
        }));
    }
}

