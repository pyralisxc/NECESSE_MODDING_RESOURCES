/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Shape;
import necesse.engine.util.GameMath;
import necesse.level.maps.Level;
import necesse.level.maps.LevelShapeBoundsSpliterator;
import necesse.level.maps.TilePosition;

public class LevelTilesSpliterator
extends LevelShapeBoundsSpliterator<TilePosition> {
    public LevelTilesSpliterator(Level level, Shape shape, int extraRange) {
        super(level, shape, extraRange);
    }

    @Override
    protected int getPosX(int x) {
        return GameMath.getTileCoordinate(x);
    }

    @Override
    protected int getPosY(int y) {
        return GameMath.getTileCoordinate(y);
    }

    @Override
    protected int getMinX() {
        if (this.level.tileWidth > 0) {
            return 0;
        }
        return Integer.MIN_VALUE;
    }

    @Override
    protected int getMinY() {
        if (this.level.tileHeight > 0) {
            return 0;
        }
        return Integer.MIN_VALUE;
    }

    @Override
    protected int getMaxX() {
        if (this.level.tileWidth > 0) {
            return this.level.tileWidth - 1;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getMaxY() {
        if (this.level.tileHeight > 0) {
            return this.level.tileHeight - 1;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    protected TilePosition getPos(int x, int y) {
        return new TilePosition(this.level, x, y);
    }
}

