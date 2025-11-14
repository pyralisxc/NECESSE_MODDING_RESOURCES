/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.presets;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.presets.Preset;

public class RandomRuinsPreset
extends Preset {
    private String[] walls = new String[]{"stonewall", "woodwall"};
    private String[] tiles = new String[]{"woodfloor", "stonefloor"};
    private String[] chests = new String[]{"storagebox", "barrel"};
    public GameRandom random;
    private int currentWall;
    private int currentTile;

    public RandomRuinsPreset(GameRandom random) {
        super(9, 9);
        int i;
        this.random = random;
        this.currentWall = ObjectRegistry.getObjectID(this.walls[random.nextInt(this.walls.length)]);
        this.currentTile = TileRegistry.getTileID(this.tiles[random.nextInt(this.tiles.length)]);
        float wallChance = 0.5f;
        float tileChance = 0.65f;
        for (i = 2; i <= 6; ++i) {
            this.setObject(i, 0, this.currentWall, random, wallChance);
            this.setObject(i, 8, this.currentWall, random, wallChance);
        }
        for (i = 2; i <= 6; ++i) {
            this.setObject(0, i, this.currentWall, random, wallChance);
            this.setObject(8, i, this.currentWall, random, wallChance);
        }
        for (i = 0; i <= 1; ++i) {
            this.setObject(i + 1, 1, this.currentWall, random, wallChance);
            this.setObject(i + 6, 1, this.currentWall, random, wallChance);
            this.setObject(i + 1, 7, this.currentWall, random, wallChance);
            this.setObject(i + 6, 7, this.currentWall, random, wallChance);
        }
        this.setObject(1, 2, this.currentWall, random, wallChance);
        this.setObject(7, 2, this.currentWall, random, wallChance);
        this.setObject(1, 6, this.currentWall, random, wallChance);
        this.setObject(7, 6, this.currentWall, random, wallChance);
        for (int x = 1; x < 8; ++x) {
            for (int y = 1; y < 8; ++y) {
                if (!((y != 1 && y != 7 || x > 2 && x < 6) && (y != 2 && y != 6 || x > 1 && x < 7))) continue;
                this.setTile(x, y, this.currentTile, random, tileChance);
            }
        }
        this.setObject(4, 4, ObjectRegistry.getObjectID(this.chests[random.nextInt(this.chests.length)]), random.nextInt(4));
        this.addInventory(LootTablePresets.surfaceRuinsChest, random, 4, 4, new Object[0]);
        this.addCanApplyRectPredicate(-1, -1, this.width + 2, this.height + 2, 0, (level, levelStartX, levelStartY, levelEndX, levelEndY, dir) -> {
            Rectangle collision = new Rectangle(levelStartX * 32, levelStartY * 32, (levelEndX - levelStartX) * 32, (levelEndY - levelStartY) * 32);
            return !level.collides((Shape)collision, new CollisionFilter().allLiquidTiles());
        });
    }

    public RandomRuinsPreset setWalls(String ... walls) {
        this.walls = walls;
        int oldWall = this.currentWall;
        this.currentWall = ObjectRegistry.getObjectID(walls[this.random.nextInt(walls.length)]);
        this.replaceObject(oldWall, this.currentWall);
        return this;
    }

    public RandomRuinsPreset setTiles(String ... tiles) {
        this.tiles = tiles;
        int oldTile = this.currentTile;
        this.currentTile = TileRegistry.getTileID(tiles[this.random.nextInt(tiles.length)]);
        this.replaceTile(oldTile, this.currentTile);
        return this;
    }

    public RandomRuinsPreset setChests(String ... chests) {
        this.chests = chests;
        this.setObject(4, 4, ObjectRegistry.getObjectID(chests[this.random.nextInt(chests.length)]), this.random.nextInt(4));
        return this;
    }

    public void setObject(int x, int y, int object, GameRandom random, float chance) {
        if (random.getChance(chance)) {
            this.setObject(x, y, object);
        }
    }

    public void setTile(int x, int y, int tile, GameRandom random, float chance) {
        if (random.getChance(chance)) {
            this.setTile(x, y, tile);
            this.setObject(x, y, 0);
        }
    }
}

