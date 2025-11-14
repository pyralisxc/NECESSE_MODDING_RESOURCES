/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.awt.Point;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.entity.mobs.HungerMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.SlowlyUseItemActiveJob;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.food.FoodConsumableItem;
import necesse.level.gameObject.ChairObjectInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.JobMoveToTile;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class SlowlyConsumeItemActiveJob
extends SlowlyUseItemActiveJob {
    public HungerMob hungerMob;
    public boolean lookedForChairs;
    public Point chairTile;
    public ObjectUserActive chairUserActive;
    public static GameTileRange CHAIR_SEARCH_RANGE = new GameTileRange(40, true, new Point[0]);

    public SlowlyConsumeItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, AtomicReference<InventoryItem> item, HungerMob hungerMob, int idleTime) {
        super(worker, priority, item, idleTime);
        this.hungerMob = hungerMob;
        this.idleTimePostUsage = 2000;
    }

    public SlowlyConsumeItemActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, InventoryItem item, HungerMob hungerMob, int idleTime) {
        super(worker, priority, item, idleTime);
        this.hungerMob = hungerMob;
        this.idleTimePostUsage = 2000;
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        if (this.chairTile != null) {
            return new JobMoveToTile(this.chairTile.x, this.chairTile.y, true);
        }
        return null;
    }

    @Override
    public boolean isAt(JobMoveToTile moveToTile) {
        Mob mob = this.worker.getMobWorker();
        return TilePathfinding.isAtOrAdjacentObject(mob.getLevel(), moveToTile.tileX, moveToTile.tileY, mob.getTileX(), mob.getTileY());
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        long timeSinceIdleTick;
        InventoryItem item;
        super.tick(isCurrent, isMovingTo);
        if (isCurrent && isMovingTo && (item = (InventoryItem)this.item.get()) != null && (timeSinceIdleTick = this.getLevel().getTime() - this.lastTickIdleTime) >= (long)this.tickIdleCooldown) {
            this.worker.showHoldAnimation(item.item, this.tickIdleCooldown + 500);
            this.lastTickIdleTime = this.getLevel().getTime();
        }
    }

    @Override
    public ActiveJobResult perform() {
        ActiveJobResult out;
        Mob mob;
        if (this.chairTile != null) {
            if (this.chairUserActive == null) {
                mob = this.worker.getMobWorker();
                GameObject object = mob.getLevel().getObject(this.chairTile.x, this.chairTile.y);
                if (object instanceof ChairObjectInterface && object instanceof ObjectUsersObject) {
                    OEUsers oeUsers = ((ObjectUsersObject)((Object)object)).getOEUsersObject(mob.getLevel(), this.chairTile.x, this.chairTile.y);
                    if (oeUsers != null && oeUsers.getCanUseError(mob) == null) {
                        oeUsers.startUser(mob);
                        this.chairUserActive = ((ObjectUserMob)((Object)mob)).getUsingObject();
                        this.startPerformingTime = this.getLevel().getTime();
                    } else {
                        this.lookedForChairs = false;
                    }
                } else {
                    this.lookedForChairs = false;
                }
            } else {
                this.chairUserActive.keepUsing();
            }
        }
        if (!this.lookedForChairs) {
            mob = this.worker.getMobWorker();
            ZoneTester zoneTester = this.worker.getJobRestrictZone();
            this.lookedForChairs = true;
            PriorityMap<Point> validChairs = SlowlyConsumeItemActiveJob.findValidChairs(mob.getLevel(), mob.getTileX(), mob.getTileY(), mob, 8, (Point tile, OEUsers oeUser) -> zoneTester.containsTile(tile.x, tile.y) && mob.estimateCanMoveTo(tile.x, tile.y, true), true);
            if (!validChairs.isEmpty()) {
                this.chairTile = validChairs.getRandomBestObject(GameRandom.globalRandom, 1);
                return ActiveJobResult.MOVE_TO;
            }
        }
        if ((out = super.perform()) != ActiveJobResult.PERFORMING && out != ActiveJobResult.MOVE_TO && this.chairUserActive != null) {
            this.chairUserActive.stopUsing();
            this.chairUserActive = null;
        }
        return out;
    }

    @Override
    public void tickIdleTime(InventoryItem item) {
        if (item == null) {
            return;
        }
        this.worker.showHoldAnimation(item.item, this.tickIdleCooldown + 500);
        if (GameRandom.globalRandom.nextBoolean()) {
            Mob mob = this.worker.getMobWorker();
            this.worker.showAttackAnimation(this.worker.getMobWorker().getDir() == 3 ? mob.getX() - 10 : mob.getX() + 10, mob.getY(), item.item, 200);
        }
    }

    @Override
    public boolean useItem(InventoryItem item, ListIterator<InventoryItem> li) {
        if (item.item.isFoodItem()) {
            FoodConsumableItem foodItem = (FoodConsumableItem)item.item;
            Mob mob = this.worker.getMobWorker();
            this.worker.showAttackAnimation(this.worker.getMobWorker().getDir() == 3 ? mob.getX() - 10 : mob.getX() + 10, mob.getY(), foodItem, 200);
            this.worker.clearHoldAnimation();
            this.hungerMob.useFoodItem(foodItem, true);
            if (foodItem.isSingleUse(null)) {
                item.setAmount(item.getAmount() - 1);
                if (item.getAmount() <= 0) {
                    li.remove();
                    this.worker.getWorkInventory().markDirty();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onCancelled(boolean becauseOfInvalid, boolean isCurrent, boolean isMovingTo) {
        super.onCancelled(becauseOfInvalid, isCurrent, isMovingTo);
        if (this.chairUserActive != null) {
            this.chairUserActive.stopUsing();
            this.chairUserActive = null;
        }
        this.worker.clearHoldAnimation();
    }

    public static PriorityMap<Point> findValidChairs(Level level, int searchCenterTileX, int searchCenterTileY, Mob user, GameTileRange searchRange, int firstFindExtraDistance, BiPredicate<Point, OEUsers> filter, Function<LevelObject, Integer> priority) {
        PriorityMap<Point> out = new PriorityMap<Point>();
        int foundDistance = -1;
        for (int i = searchRange.minRange; i <= searchRange.maxRange && (foundDistance == -1 || i <= foundDistance + firstFindExtraDistance); ++i) {
            for (Point p : searchRange.getValuesAtRange(searchCenterTileX, searchCenterTileY, i)) {
                LevelObject lo = new LevelObject(level, p.x, p.y);
                OEUsers chair = SlowlyConsumeItemActiveJob.isChair(lo);
                if (chair == null || chair.getCanUseError(user) != null || filter != null && !filter.test(p, chair)) continue;
                int chairPriority = priority == null ? 0 : priority.apply(lo);
                out.add(chairPriority, p);
                if (foundDistance != -1) continue;
                foundDistance = i;
                if (firstFindExtraDistance >= 0) continue;
                return out;
            }
        }
        return out;
    }

    public static PriorityMap<Point> findValidChairs(Level level, int searchCenterTileX, int searchCenterTileY, Mob user, int firstFindExtraDistance, BiPredicate<Point, OEUsers> filter, Function<LevelObject, Integer> priority) {
        return SlowlyConsumeItemActiveJob.findValidChairs(level, searchCenterTileX, searchCenterTileY, user, CHAIR_SEARCH_RANGE, firstFindExtraDistance, filter, priority);
    }

    public static PriorityMap<Point> findValidChairs(Level level, int searchCenterTileX, int searchCenterTileY, Mob user, GameTileRange searchRange, int firstFindExtraDistance, BiPredicate<Point, OEUsers> filter, boolean prefersTables) {
        return SlowlyConsumeItemActiveJob.findValidChairs(level, searchCenterTileX, searchCenterTileY, user, searchRange, firstFindExtraDistance, filter, prefersTables ? lo -> ((ChairObjectInterface)((Object)lo.object)).facesTable(lo.level, lo.tileX, lo.tileY) ? 100 : 0 : null);
    }

    public static PriorityMap<Point> findValidChairs(Level level, int searchCenterTileX, int searchCenterTileY, Mob user, int firstFindExtraDistance, BiPredicate<Point, OEUsers> filter, boolean prefersTables) {
        return SlowlyConsumeItemActiveJob.findValidChairs(level, searchCenterTileX, searchCenterTileY, user, CHAIR_SEARCH_RANGE, firstFindExtraDistance, filter, prefersTables ? lo -> ((ChairObjectInterface)((Object)lo.object)).facesTable(lo.level, lo.tileX, lo.tileY) ? 100 : 0 : null);
    }

    public static OEUsers isChair(LevelObject lo) {
        if (lo.object instanceof ChairObjectInterface && lo.object instanceof ObjectUsersObject) {
            return ((ObjectUsersObject)((Object)lo.object)).getOEUsersObject(lo.level, lo.tileX, lo.tileY);
        }
        return null;
    }

    public static OEUsers isChair(Level level, int tileX, int tileY) {
        return SlowlyConsumeItemActiveJob.isChair(new LevelObject(level, tileX, tileY));
    }
}

