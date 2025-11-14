/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Rectangle;
import necesse.level.gameObject.container.GrainMillExtraObject;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class GrainMillObject3
extends GrainMillExtraObject {
    protected int counterIDTopLeft;
    protected int counterIDTopRight;
    protected int counterIDBotRight;

    protected GrainMillObject3() {
    }

    @Override
    protected void setCounterIDs(int id1, int id2, int id3, int id4) {
        this.counterIDTopLeft = id1;
        this.counterIDTopRight = id2;
        this.counterIDBotRight = id4;
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 5, y * 32, 27, 22);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 5, y * 32 + 12, 27, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32, y * 32 + 12, 27, 20);
        }
        return new Rectangle(x * 32, y * 32, 27, 22);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 1, 2, 2, rotation, false, this.counterIDTopLeft, this.counterIDTopRight, this.getID(), this.counterIDBotRight);
    }
}

