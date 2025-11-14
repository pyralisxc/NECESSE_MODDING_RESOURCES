/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.ai.path.SubRegionPathResult;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SubRegion;

public class BasicPathDoorOption
extends PathDoorOption {
    public final boolean canBreakDown;
    public final boolean canOpen;
    public final boolean canClose;

    public BasicPathDoorOption(String debugName, Level level, boolean canBreakDown, boolean canOpen, boolean canClose) {
        super(debugName, level);
        this.canBreakDown = canBreakDown;
        this.canOpen = canOpen;
        this.canClose = canClose;
    }

    public BasicPathDoorOption(String debugName, Level level, boolean canOpen, boolean canClose) {
        this(debugName, level, false, canOpen, canClose);
    }

    @Override
    public SubRegionPathResult canPathThrough(SubRegion subregion) {
        if (this.canBreakDown) {
            return SubRegionPathResult.VALID;
        }
        if (subregion.getType().isDoor) {
            if (this.canOpen) {
                return SubRegionPathResult.VALID;
            }
            return SubRegionPathResult.CHECK_EACH_TILE;
        }
        if (subregion.getType().isSolid) {
            return SubRegionPathResult.INVALID;
        }
        return SubRegionPathResult.VALID;
    }

    @Override
    public boolean canPathThroughCheckTile(SubRegion subregion, int tileX, int tileY) {
        GameObject object = this.level.getObject(tileX, tileY);
        if (object.isDoor) {
            return ((DoorObject)object).isOpen(this.level, tileX, tileY, this.level.getObjectRotation(tileX, tileY)) || this.canOpen(tileX, tileY) && !((DoorObject)object).isForceClosed(this.level, tileX, tileY);
        }
        return false;
    }

    @Override
    public boolean canBreakDown(int tileX, int tileY) {
        return this.canBreakDown;
    }

    @Override
    public boolean canOpen(int tileX, int tileY) {
        return this.canOpen;
    }

    @Override
    public boolean canClose(int tileX, int tileY) {
        return this.canClose;
    }

    @Override
    public boolean doorChangeInvalidatesCache(DoorObject lastDoor, DoorObject newDoor, int tileX, int tileY) {
        return (!this.canOpen(tileX, tileY) || newDoor.isForceClosed(this.level, tileX, tileY)) && !this.canBreakDown(tileX, tileY);
    }
}

