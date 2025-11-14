/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.util.AreaSpliterator;
import necesse.level.maps.Level;

public abstract class LevelShapeBoundsSpliterator<T>
extends AreaSpliterator<T> {
    public final Level level;

    public LevelShapeBoundsSpliterator(Level level, Shape shape, int extraRange) {
        this.level = level;
        Rectangle bounds = shape.getBounds();
        int startX = Math.max(this.getMinX(), this.getPosX(bounds.x) - extraRange);
        int startY = Math.max(this.getMinY(), this.getPosY(bounds.y) - extraRange);
        int endX = Math.min(this.getMaxX(), this.getPosX(bounds.x + bounds.width) + extraRange);
        int endY = Math.min(this.getMaxY(), this.getPosY(bounds.y + bounds.height) + extraRange);
        this.reset(startX, startY, endX + 1, endY + 1);
    }

    protected abstract int getPosX(int var1);

    protected abstract int getPosY(int var1);

    protected int getMinX() {
        return 0;
    }

    protected int getMinY() {
        return 0;
    }

    protected abstract int getMaxX();

    protected abstract int getMaxY();
}

