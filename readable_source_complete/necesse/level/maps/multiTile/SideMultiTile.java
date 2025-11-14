/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.multiTile;

import java.awt.Point;
import necesse.level.maps.multiTile.MultiTile;

public class SideMultiTile
extends MultiTile {
    public SideMultiTile(int x, int y, int width, int height, int rotation, boolean isMaster, int ... ids) {
        super(x, y, width, height, rotation, isMaster, ids);
    }

    @Override
    public Point getMirrorXPosOffset() {
        Point centerTileOffset = this.getCenterTileOffset();
        if (this.rotation == 0 || this.rotation == 2) {
            return centerTileOffset;
        }
        return new Point(-centerTileOffset.x, -centerTileOffset.y);
    }

    @Override
    public Point getMirrorYPosOffset() {
        Point centerTileOffset = this.getCenterTileOffset();
        if (this.rotation == 1 || this.rotation == 3) {
            return centerTileOffset;
        }
        return new Point(-centerTileOffset.x, -centerTileOffset.y);
    }

    @Override
    public int getXMirrorRotation() {
        if (this.rotation == 0) {
            return 2;
        }
        if (this.rotation == 2) {
            return 0;
        }
        return this.rotation;
    }

    @Override
    public int getYMirrorRotation() {
        if (this.rotation == 1) {
            return 3;
        }
        if (this.rotation == 3) {
            return 1;
        }
        return this.rotation;
    }
}

