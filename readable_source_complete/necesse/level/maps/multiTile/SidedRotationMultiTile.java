/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.multiTile;

import java.awt.Point;
import necesse.level.maps.multiTile.MultiTile;

public class SidedRotationMultiTile
extends MultiTile {
    public SidedRotationMultiTile(int x, int y, int width, int height, int rotation, boolean isMaster, int ... ids) {
        super(x, y, width, height, rotation, isMaster, ids);
    }

    @Override
    public Point getMirrorXPosOffset() {
        Point centerTileOffset = this.getCenterTileOffset();
        if (this.rotation == 1 || this.rotation == 3) {
            return new Point(0, centerTileOffset.y);
        }
        return new Point(-centerTileOffset.x, 0);
    }

    @Override
    public Point getMirrorYPosOffset() {
        Point centerTileOffset = this.getCenterTileOffset();
        if (this.rotation == 0 || this.rotation == 2) {
            return new Point(centerTileOffset.x, 0);
        }
        return new Point(0, -centerTileOffset.y);
    }
}

