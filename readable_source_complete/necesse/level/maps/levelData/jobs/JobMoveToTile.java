/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.util.Objects;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.mobMovement.MobMovement;

public class JobMoveToTile {
    public final MobMovement custom;
    public final int tileX;
    public final int tileY;
    public final boolean acceptAdjacentTiles;
    public final int maxPathIterations;

    public JobMoveToTile(int tileX, int tileY, boolean acceptAdjacentTiles, int maxPathIterations) {
        this.custom = null;
        this.tileX = tileX;
        this.tileY = tileY;
        this.acceptAdjacentTiles = acceptAdjacentTiles;
        this.maxPathIterations = maxPathIterations;
    }

    public JobMoveToTile(int tileX, int tileY, boolean acceptAdjacentTiles) {
        this(tileX, tileY, acceptAdjacentTiles, HumanMob.defaultJobPathIterations);
    }

    public JobMoveToTile(MobMovement custom) {
        this.custom = custom;
        this.tileX = -1;
        this.tileY = -1;
        this.acceptAdjacentTiles = false;
        this.maxPathIterations = 0;
    }

    public boolean equals(JobMoveToTile other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!Objects.equals(this.custom, other.custom)) {
            return false;
        }
        if (this.custom != null) {
            return true;
        }
        return this.tileX == other.tileX && this.tileY == other.tileY && this.acceptAdjacentTiles == other.acceptAdjacentTiles;
    }

    public boolean equals(Object obj) {
        if (obj instanceof JobMoveToTile) {
            return this.equals((JobMoveToTile)obj);
        }
        return super.equals(obj);
    }
}

