/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.nightSwarmEvent.batStages;

import java.util.concurrent.atomic.AtomicInteger;

public class NightSwarmCompletedCounter {
    AtomicInteger done = new AtomicInteger();
    AtomicInteger total = new AtomicInteger();

    public boolean isComplete() {
        return this.done.get() >= this.total.get();
    }

    public boolean isMajorityComplete() {
        return this.done.get() >= Math.min((int)((float)Math.max(this.total.get(), 10) / 1.5f), this.total.get());
    }
}

