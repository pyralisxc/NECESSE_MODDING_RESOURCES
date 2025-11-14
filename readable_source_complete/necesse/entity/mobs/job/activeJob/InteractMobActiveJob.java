/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job.activeJob;

import java.util.function.Predicate;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.MobActiveJob;
import necesse.inventory.InventoryItem;

public abstract class InteractMobActiveJob<T extends Mob>
extends MobActiveJob<T> {
    public Predicate<T> isValid;
    public GameObjectReservable reservable;
    public InventoryItem interactItem;

    public InteractMobActiveJob(EntityJobWorker worker, JobTypeHandler.TypePriority priority, T target, Predicate<T> isValid, GameObjectReservable reservable, InventoryItem interactItem) {
        super(worker, priority, target);
        this.isValid = isValid;
        this.reservable = reservable;
        this.interactItem = interactItem;
    }

    @Override
    public boolean isJobValid(boolean isCurrent) {
        if (this.reservable != null && !this.reservable.isAvailable(this.worker.getMobWorker())) {
            return false;
        }
        if (this.isValid != null) {
            return this.isValid.test((Mob)this.target);
        }
        return true;
    }

    @Override
    public void tick(boolean isCurrent, boolean isMovingTo) {
        if (this.reservable != null) {
            this.reservable.reserve(this.worker.getMobWorker());
        }
    }

    public abstract ActiveJobResult onInteracted(T var1);

    @Override
    public ActiveJobResult performTarget() {
        if (this.worker.isInWorkAnimation()) {
            return ActiveJobResult.PERFORMING;
        }
        if (this.interactItem != null) {
            this.worker.showAttackAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), this.interactItem.item, 500);
        } else {
            this.worker.showPlaceAnimation(((Mob)this.target).getX(), ((Mob)this.target).getY(), null, 500);
        }
        return this.onInteracted((Mob)this.target);
    }
}

