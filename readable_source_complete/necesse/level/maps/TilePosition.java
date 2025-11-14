/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Point;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;
import necesse.level.maps.biomes.Biome;

public class TilePosition {
    public final Level level;
    public final int tileX;
    public final int tileY;
    protected LevelObject object;
    protected LevelTile tile;

    public TilePosition(Level level, int tileX, int tileY) {
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public TilePosition(Level level, Point tile) {
        this(level, tile.x, tile.y);
    }

    public int objectID() {
        return this.level.getObjectID(this.tileX, this.tileY);
    }

    public byte objectRotation() {
        return this.level.getObjectRotation(this.tileX, this.tileY);
    }

    public LevelObject object() {
        if (this.object == null) {
            this.object = new LevelObject(this.level, this.tileX, this.tileY);
        }
        return this.object;
    }

    public int tileID() {
        return this.level.getTileID(this.tileX, this.tileY);
    }

    public LevelTile tile() {
        if (this.tile == null) {
            this.tile = new LevelTile(this.level, this.tileX, this.tileY);
        }
        return this.tile;
    }

    public boolean isLiquidTile() {
        return this.level.isLiquidTile(this.tileX, this.tileY);
    }

    public boolean isShore() {
        return this.level.isShore(this.tileX, this.tileY);
    }

    public boolean isSolidTile() {
        return this.level.isSolidTile(this.tileX, this.tileY);
    }

    public int getBiomeID() {
        return this.level.getBiomeID(this.tileX, this.tileY);
    }

    public Biome getBiome() {
        return this.level.getBiome(this.tileX, this.tileY);
    }
}

