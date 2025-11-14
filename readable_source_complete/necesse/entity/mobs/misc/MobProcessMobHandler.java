/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.misc;

import necesse.engine.util.GameUtils;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.misc.ProcessMobHandler;

public class MobProcessMobHandler<C extends Mob, T extends Mob> {
    public final Mob mob;
    public long startTime;
    public LevelMob<T> targetMob;
    public int processTime;
    public ProcessMobHandler<C, T> target;

    public MobProcessMobHandler(Mob mob) {
        this.mob = mob;
    }

    public boolean isInProgress() {
        return this.startTime != 0L;
    }

    public long getTimeSinceStart() {
        return this.mob.getTime() - this.startTime;
    }

    public float getProgressPercent() {
        if (this.processTime != 0) {
            return GameUtils.getAnimFloat(this.getTimeSinceStart(), this.processTime);
        }
        return 0.0f;
    }

    public void tick() {
        if (this.startTime > 0L) {
            long timeSinceStart = this.getTimeSinceStart();
            if (timeSinceStart <= (long)this.processTime) {
                this.tickInProgress();
            } else {
                this.startTime = 0L;
                this.processTime = 0;
                this.onCompleted();
            }
        }
    }

    public void tickInProgress() {
    }

    public void onCompleted() {
    }
}

