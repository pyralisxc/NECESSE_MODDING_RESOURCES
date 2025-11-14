/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.multiTile.MultiTile;

public abstract class TileActiveJob
extends ActiveJob {
    public final int tileX;
    public final int tileY;

    public TileActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, int tileX, int tileY) {
        super(worker, priority);
        this.tileX = tileX;
        this.tileY = tileY;
    }

    @Override
    public boolean isAt(JobMoveToTile moveToTile) {
        MultiTile multiTile;
        Mob mob = this.worker.getMobWorker();
        if (moveToTile.acceptAdjacentTiles && (multiTile = this.getLevel().getObject(moveToTile.tileX, moveToTile.tileY).getMultiTile(this.getLevel(), 0, moveToTile.tileX, moveToTile.tileY)).getAdjacentTileRectangle(moveToTile.tileX, moveToTile.tileY).contains(mob.getTileX(), mob.getTileY()) && !mob.hasCurrentMovement()) {
            return true;
        }
        double dist = new Point(mob.getX(), mob.getY()).distance(moveToTile.tileX * 32 + 16, moveToTile.tileY * 32 + 16);
        if (dist <= (double)this.getCompleteRange()) {
            return this.hasLOS();
        }
        return false;
    }

    public int getCompleteRange() {
        return 48;
    }

    public boolean hasLOS() {
        Mob mob = this.worker.getMobWorker();
        Line2D.Float line = new Line2D.Float(mob.x, mob.y, this.tileX * 32 + 16, this.tileY * 32 + 16);
        return !mob.getLevel().collides(line, new CollisionFilter().mobCollision().addFilter(pos -> {
            Rectangle rect = pos.object().getMultiTile().getTileRectangle(pos.tileX, pos.tileY);
            return !rect.contains(pos.tileX, pos.tileY);
        }));
    }

    public String toString() {
        return super.toString() + "{" + this.tileX + ", " + this.tileY + "}";
    }

    protected Inventory getTileInventory() {
        ObjectEntity objectEntity = this.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        if (objectEntity instanceof OEInventory) {
            return ((OEInventory)((Object)objectEntity)).getInventory();
        }
        return null;
    }
}

