/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.stream.Stream;
import necesse.engine.GameTileRange;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobFinder;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.WorkInventory;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public interface EntityJobWorker {
    public static final GameTileRange defaultJobRange = new GameTileRange(50, new Point[0]);

    default public Mob getMobWorker() {
        return (Mob)((Object)this);
    }

    default public Level getLevel() {
        return this.getMobWorker().getLevel();
    }

    default public Point getJobSearchTile() {
        Mob mob = this.getMobWorker();
        return new Point(mob.getTileX(), mob.getTileY());
    }

    default public Rectangle getJobSearchBounds() {
        return defaultJobRange.getRangeBounds(this.getJobSearchTile());
    }

    default public Stream<LevelJob> streamJobsWithinRange() {
        return this.getLevel().jobsLayer.streamJobsInRegionsShape(this.getJobSearchBounds(), 0);
    }

    default public JobSequence findJob() {
        long currentTime;
        JobTypeHandler handler = this.getJobTypeHandler();
        if (handler.isOnGlobalCooldown(currentTime = this.getMobWorker().getTime())) {
            return null;
        }
        FoundJob<?> first = new JobFinder(this).findJob();
        if (handler.resetPrioritizeNextJobIfFound) {
            handler.prioritizeNextJobID = -1;
        }
        if (first != null) {
            first.handler.startCooldown(currentTime);
            this.useWorkBreakBuffer(first.handler.nextWorkBreakBufferUsage());
            handler.lastPerformedJobID = ((LevelJob)first.job).prioritizeForSameJobAgain() ? ((LevelJob)first.job).getID() : -1;
            return first.getSequence();
        }
        int cooldown = GameRandom.globalRandom.getIntBetween(5000, 10000);
        handler.startGlobalCooldown(currentTime, cooldown);
        handler.lastPerformedJobID = -1;
        handler.prioritizeNextJobID = -1;
        return null;
    }

    default public void onHitCausedFailed(boolean clearedSequence) {
        int cooldown = GameRandom.globalRandom.getIntBetween(5000, 10000);
        if (!clearedSequence) {
            cooldown /= 2;
        }
        this.getJobTypeHandler().startGlobalCooldown(this.getMobWorker().getTime(), cooldown);
    }

    default public void onTargetFoundCausedFailed(Mob target) {
        int cooldown = GameRandom.globalRandom.getIntBetween(5000, 10000);
        this.getJobTypeHandler().startGlobalCooldown(this.getMobWorker().getTime(), cooldown);
    }

    public boolean estimateCanMoveTo(int var1, int var2, boolean var3);

    default public ZoneTester getJobRestrictZone() {
        Point searchTile = this.getJobSearchTile();
        return (tileX, tileY) -> defaultJobRange.isWithinRange(searchTile, tileX, tileY);
    }

    public JobTypeHandler getJobTypeHandler();

    public WorkInventory getWorkInventory();

    default public void submitFullInventoryNotification() {
        if (this instanceof SettlerMob) {
            SettlerMob settlerMob = (SettlerMob)((Object)this);
            SettlementNotificationSeverity severity = SettlementNotificationSeverity.WARNING;
            NetworkSettlementData settlement = settlerMob.getSettlerSettlementNetworkData();
            if (settlement != null) {
                settlement.notifications.submitNotification("fullinventory", (SettlerMob)((Object)this), severity);
            }
        }
    }

    default public void removeFullInventoryNotification() {
        SettlerMob settlerMob;
        NetworkSettlementData settlement;
        if (this instanceof SettlerMob && (settlement = (settlerMob = (SettlerMob)((Object)this)).getSettlerSettlementNetworkData()) != null) {
            settlement.notifications.removeNotification("fullinventory", (SettlerMob)((Object)this));
        }
    }

    default public boolean isFullInventoryNotificationStillValid() {
        return this.getWorkInventory().isFull();
    }

    public void showPickupAnimation(int var1, int var2, Item var3, int var4);

    public void showPlaceAnimation(int var1, int var2, Item var3, int var4);

    public void showWorkAnimation(int var1, int var2, Item var3, int var4);

    public void showAttackAnimation(int var1, int var2, Item var3, int var4);

    public void showHoldAnimation(Item var1, int var2);

    public void clearHoldAnimation();

    public boolean hasActiveJob();

    public boolean isInWorkAnimation();

    public boolean isJobCancelled();

    public void resetJobCancelled();

    public int getWorkBreakBuffer();

    public boolean isOnWorkBreak();

    public void useWorkBreakBuffer(int var1);

    public void regenWorkBreakBuffer(int var1);

    public boolean isOnStrike();

    public void attemptStartStrike(boolean var1, boolean var2);

    default public void setPrioritizeNextJob(int jobID, boolean resetPrioritizeNextJobIfFound) {
        JobTypeHandler handler = this.getJobTypeHandler();
        handler.prioritizeNextJobID = jobID;
        handler.resetPrioritizeNextJobIfFound = resetPrioritizeNextJobIfFound;
    }

    default public void setPrioritizeNextJob(Class<? extends LevelJob> job, boolean resetPrioritizeNextJobIfFound) {
        this.setPrioritizeNextJob(LevelJobRegistry.getJobID(job), resetPrioritizeNextJobIfFound);
    }
}

