/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.awt.geom.Line2D;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketFishingStatus;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.FishingMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.SingleJobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobHitResult;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.baitItem.BaitItem;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.LevelTile;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.liquidManager.ClosestHeightResult;

public class FishingPositionLevelJob
extends LevelJob {
    public FishingPositionLevelJob(int tileX, int tileY) {
        super(tileX, tileY);
    }

    public FishingPositionLevelJob(LoadData save) {
        super(save);
    }

    public LevelTile getTile() {
        return this.getLevel().getLevelTile(this.tileX, this.tileY);
    }

    public int getLiquidHeight() {
        return this.getLevel().liquidManager.getHeight(this.tileX, this.tileY);
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isValid() {
        int height = this.getLevel().liquidManager.getHeight(this.tileX, this.tileY);
        return -5 < height && height < -1;
    }

    @Override
    public int getSameJobPriority() {
        return -this.getLiquidHeight();
    }

    protected static Point findClosestHeightTileSimple(Entity entity, int tileX, int tileY, int desiredHeight) {
        int currentHeight = entity.getLevel().liquidManager.getHeight(tileX, tileY);
        Point currentTile = new Point(tileX, tileY);
        if (currentHeight == desiredHeight) {
            return currentTile;
        }
        boolean goDown = desiredHeight < currentHeight;
        int mobTileX = entity.getTileX();
        int mobTileY = entity.getTileY();
        int[][] dirs = new int[][]{{-1, 0, 1}, {0, 1, -1}, {1, 0, -1}};
        while (true) {
            int[] checkX = dirs[Integer.compare(mobTileX - currentTile.x, 0) + 1];
            int[] checkY = dirs[Integer.compare(mobTileY - currentTile.y, 0) + 1];
            Point nextTile = null;
            for (int x : checkX) {
                int nextX = currentTile.x + x;
                if (!entity.getLevel().isTileXWithinBounds(nextX)) continue;
                for (int y : checkY) {
                    if (x == 0 && y == 0) continue;
                    int nextY = currentTile.y + y;
                    if (!entity.getLevel().isTileYWithinBounds(nextY) || entity.getLevel().isSolidTile(nextX, nextY)) continue;
                    int height = entity.getLevel().liquidManager.getHeight(nextX, nextY);
                    if (height == desiredHeight) {
                        return new Point(nextX, nextY);
                    }
                    if (goDown && height < currentHeight) {
                        currentHeight = height;
                        nextTile = new Point(nextX, nextY);
                        continue;
                    }
                    if (height <= currentHeight) continue;
                    currentHeight = height;
                    nextTile = new Point(nextX, nextY);
                }
            }
            if (nextTile == null) {
                return null;
            }
            currentTile = nextTile;
        }
    }

    public static <T extends FishingPositionLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FishingMob fishingMob, FoundJob<T> foundJob) {
        LocalMessage activityDescription = new LocalMessage("activities", "fishing");
        return new SingleJobSequence(((FishingPositionLevelJob)foundJob.job).getActiveJob(worker, foundJob.priority, fishingMob), activityDescription);
    }

    public ActiveJob getActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, FishingMob fishingMob) {
        return new FishingActiveJob(worker, priority, this.tileX, this.tileY, fishingMob);
    }

    private class FishingActiveJob
    extends TileActiveJob {
        protected FishingMob fishingMob;
        protected FishingEvent fishingEvent;
        protected int maxWaitTicker;

        public FishingActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, int tileX, int tileY, FishingMob fishingMob) {
            super(worker, priority, tileX, tileY);
            this.fishingMob = fishingMob;
        }

        @Override
        public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
            CollisionFilter collisionFilter = ((Mob)((Object)this.fishingMob)).modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), null);
            if (lastTile == null || this.getLevel().isSolidTile(lastTile.tileX, lastTile.tileY) || this.getLevel().collides(new Line2D.Float(this.tileX * 32 + 16, this.tileY * 32 + 16, lastTile.tileX * 32 + 16, lastTile.tileY * 32 + 16), 16.0f, 4.0f, collisionFilter)) {
                ClosestHeightResult result = this.getLevel().liquidManager.findClosestHeightTile(this.tileX, this.tileY, 1, checkTile -> {
                    if (this.getLevel().isSolidTile(checkTile.x, checkTile.y)) {
                        return false;
                    }
                    return !this.getLevel().collides(new Line2D.Float(this.tileX * 32 + 16, this.tileY * 32 + 16, checkTile.x * 32 + 16, checkTile.y * 32 + 16), 16.0f, 4.0f, collisionFilter);
                });
                return new JobMoveToTile(result.best.x, result.best.y, false);
            }
            return lastTile;
        }

        @Override
        public void tick(boolean isCurrent, boolean isMovingTo) {
        }

        @Override
        public boolean isAt(JobMoveToTile moveToTile) {
            Mob mob = this.worker.getMobWorker();
            double dist = new Point(mob.getX(), mob.getY()).distance(moveToTile.tileX * 32 + 16, moveToTile.tileY * 32 + 16);
            return dist < 32.0;
        }

        @Override
        public boolean isValid(boolean isCurrent) {
            if (FishingPositionLevelJob.this.isRemoved()) {
                return false;
            }
            return FishingPositionLevelJob.this.isValid();
        }

        @Override
        public ActiveJobHitResult onHit(MobWasHitEvent event, boolean isMovingTo) {
            if (this.fishingEvent != null && !this.fishingEvent.isReeled) {
                this.fishingEvent.reel();
                if (FishingPositionLevelJob.this.isServer()) {
                    this.getLevel().getServer().network.sendToClientsWithEntity(PacketFishingStatus.getReelPacket(this.fishingEvent), this.fishingEvent);
                }
            }
            return ActiveJobHitResult.CLEAR_SEQUENCE;
        }

        @Override
        public void onCancelled(boolean becauseOfInvalid, boolean isCurrent, boolean isMovingTo) {
            if (this.fishingEvent != null && !this.fishingEvent.isReeled) {
                this.fishingEvent.reel();
                if (FishingPositionLevelJob.this.isServer()) {
                    this.getLevel().getServer().network.sendToClientsWithEntity(PacketFishingStatus.getReelPacket(this.fishingEvent), this.fishingEvent);
                }
            }
        }

        @Override
        public ActiveJobResult perform() {
            if (this.fishingEvent == null) {
                FishingRodItem fishingRod = (FishingRodItem)ItemRegistry.getItem("ironfishingrod");
                BaitItem baitItem = (BaitItem)ItemRegistry.getItem("wormbait");
                this.fishingEvent = new FishingEvent(this.fishingMob, this.tileX * 32 + 16, this.tileY * 32 + 16, fishingRod, baitItem);
                this.getLevel().entityManager.events.add(this.fishingEvent);
                this.worker.showAttackAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, fishingRod, fishingRod.getAttackAnimTime(new InventoryItem(fishingRod), null));
                this.maxWaitTicker = 600;
            } else if (!this.fishingEvent.isReeled) {
                --this.maxWaitTicker;
                if (this.maxWaitTicker <= 0 || this.fishingEvent.getTicksToNextCatch() == -10 && GameRandom.globalRandom.getChance(0.7f)) {
                    this.fishingEvent.reel();
                    if (FishingPositionLevelJob.this.isServer()) {
                        this.getLevel().getServer().network.sendToClientsWithEntity(PacketFishingStatus.getReelPacket(this.fishingEvent), this.fishingEvent);
                    }
                }
            }
            if (this.fishingEvent.isOver()) {
                this.fishingEvent = null;
                return ActiveJobResult.FINISHED;
            }
            return ActiveJobResult.PERFORMING;
        }
    }
}

