/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

public class AIBeforeHitCalculatedEvent
extends AIEvent {
    public final MobBeforeHitCalculatedEvent event;

    public AIBeforeHitCalculatedEvent(MobBeforeHitCalculatedEvent event) {
        this.event = event;
    }
}

