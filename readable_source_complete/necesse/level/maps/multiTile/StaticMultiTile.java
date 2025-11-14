/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.multiTile;

import java.awt.Point;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.presets.PresetRotation;

public class StaticMultiTile
extends MultiTile {
    protected boolean allowRotation = true;

    public StaticMultiTile(int x, int y, int width, int height, int rotation, boolean isMaster, int ... ids) {
        super(x, y, width, height, rotation, isMaster, ids);
    }

    public StaticMultiTile(int x, int y, int width, int height, boolean isMaster, int ... ids) {
        this(x, y, width, height, 0, isMaster, ids);
        this.allowRotation = false;
    }

    @Override
    public Point getMirrorXPosOffset() {
        int centerXOffset = this.getCenterXOffset();
        boolean isEven = this.width % 2 == 0;
        int offset = -centerXOffset * 2 + (isEven ? (int)Math.signum(centerXOffset) : 0);
        return new Point(offset, 0);
    }

    @Override
    public Point getMirrorYPosOffset() {
        int centerYOffset = this.getCenterYOffset();
        boolean isEven = this.height % 2 == 0;
        int offset = -centerYOffset * 2 + (isEven ? (int)Math.signum(centerYOffset) : 0);
        return new Point(0, offset);
    }

    @Override
    public int getXMirrorRotation() {
        return this.rotation;
    }

    @Override
    public int getYMirrorRotation() {
        return this.rotation;
    }

    @Override
    public Point getPresetRotationOffset(PresetRotation presetRotation) {
        if (this.allowRotation) {
            return super.getPresetRotationOffset(presetRotation);
        }
        return null;
    }
}

