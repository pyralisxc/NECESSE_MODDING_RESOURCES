/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import java.awt.Point;
import java.util.Objects;
import necesse.level.maps.Level;

public class RegionPosition {
    public final Level level;
    public final int regionX;
    public final int regionY;

    public RegionPosition(Level level, int regionX, int regionY) {
        this.level = level;
        this.regionX = regionX;
        this.regionY = regionY;
    }

    public RegionPosition(Level level, Point point) {
        this(level, point.x, point.y);
    }

    public Point point() {
        return new Point(this.regionX, this.regionY);
    }

    public boolean isSame(RegionPosition other) {
        return this.level.getIdentifier().equals(other.level.getIdentifier()) && this.regionX == other.regionX && this.regionY == other.regionY;
    }

    public boolean isLoaded() {
        return this.level.regionManager.isRegionLoaded(this.regionX, this.regionY);
    }

    public boolean equals(Object obj) {
        if (obj instanceof RegionPosition) {
            return this.isSame((RegionPosition)obj);
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return Objects.hash(this.level.getIdentifierHashCode(), this.regionX, this.regionY);
    }
}

