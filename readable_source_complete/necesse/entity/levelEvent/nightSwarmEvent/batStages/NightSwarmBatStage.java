/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.util.LinkedList;
import necesse.entity.levelEvent.nightSwarmEvent.batStages.NightSwarmCompletedCounter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.hostile.bosses.NightSwarmBatMob;

public abstract class NightSwarmBatStage {
    public final boolean idleAllowed;
    private LinkedList<NightSwarmCompletedCounter> completedOrRemoved = new LinkedList();

    public NightSwarmBatStage(boolean idleAllowed) {
        this.idleAllowed = idleAllowed;
    }

    public abstract void onStarted(NightSwarmBatMob var1);

    public abstract void serverTick(NightSwarmBatMob var1);

    public abstract boolean hasCompleted(NightSwarmBatMob var1);

    public abstract void onCompleted(NightSwarmBatMob var1);

    public final void onCompletedOrRemoved(NightSwarmBatMob mob, boolean remove) {
        if (!remove) {
            this.onCompleted(mob);
        }
        for (NightSwarmCompletedCounter counter : this.completedOrRemoved) {
            counter.done.addAndGet(1);
        }
        this.completedOrRemoved.clear();
    }

    public NightSwarmBatStage addCompletedCounter(NightSwarmCompletedCounter counter) {
        counter.total.addAndGet(1);
        this.completedOrRemoved.add(counter);
        return this;
    }

    public void onCollisionHit(NightSwarmBatMob mob, Mob target) {
    }

    public void onWasHit(NightSwarmBatMob mob, MobWasHitEvent event) {
    }
}

