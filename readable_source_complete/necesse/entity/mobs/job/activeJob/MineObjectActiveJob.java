/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.function.Predicate;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.ObjectDamageResult;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.GameLinkedListJobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.PickupItemEntityActiveJob;
import necesse.entity.mobs.job.activeJob.TileActiveJob;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.JobMoveToTile;

public abstract class MineObjectActiveJob
extends TileActiveJob {
    public Predicate<LevelObject> isValidObject;
    public GameObjectReservable reservable;
    public String attackItemStringID;
    public int objectDamage;
    public int animTime;
    public int attackCooldown;
    public long lastAttackTime;

    public MineObjectActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, int tileX, int tileY, Predicate<LevelObject> isValidObject, GameObjectReservable reservable, String attackItemStringID, int objectDamage, int animTime, int attackCooldown) {
        super(worker, priority, tileX, tileY);
        this.isValidObject = isValidObject;
        this.reservable = reservable;
        this.attackItemStringID = attackItemStringID;
        this.objectDamage = objectDamage;
        this.animTime = animTime;
        this.attackCooldown = attackCooldown;
    }

    @Override
    public JobMoveToTile getMoveToTile(JobMoveToTile lastTile) {
        return new JobMoveToTile(this.tileX, this.tileY, true);
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        if (this.reservable != null) {
            this.reservable.reserve(this.worker.getMobWorker());
        }
    }

    @Override
    public boolean isValid(boolean isCurrent) {
        if (this.reservable != null && !this.reservable.isAvailable(this.worker.getMobWorker())) {
            return false;
        }
        if (this.isValidObject != null) {
            LevelObject levelObject = this.getLevel().getLevelObject(this.tileX, this.tileY);
            return this.isValidObject.test(levelObject);
        }
        return true;
    }

    public void addItemPickupJobs(JobTypeHandler.TypePriority priority, ObjectDamageResult result, GameLinkedListJobSequence sequence) {
        PickupItemEntityActiveJob.addItemPickupJobs(this.worker, priority, result.itemsDropped, sequence);
    }

    public abstract void onObjectDestroyed(ObjectDamageResult var1);

    @Override
    public ActiveJobResult perform() {
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        Mob mob = this.worker.getMobWorker();
        if (this.lastAttackTime + (long)this.attackCooldown > mob.getWorldEntity().getTime()) {
            return ActiveJobResult.PERFORMING;
        }
        if (this.attackItemStringID != null) {
            this.worker.showAttackAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, ItemRegistry.getItem(this.attackItemStringID), this.animTime);
        } else {
            this.worker.showPlaceAnimation(this.tileX * 32 + 16, this.tileY * 32 + 16, null, this.animTime);
        }
        ObjectDamageResult result = this.getLevel().entityManager.doObjectDamage(0, this.tileX, this.tileY, this.objectDamage, -1.0f, mob, null, true, this.tileX * 32 + 16, this.tileY * 32 + 16);
        if (result.destroyed) {
            this.onObjectDestroyed(result);
            return ActiveJobResult.FINISHED;
        }
        if (result.addedDamage > 0) {
            return ActiveJobResult.PERFORMING;
        }
        return ActiveJobResult.FAILED;
    }
}

