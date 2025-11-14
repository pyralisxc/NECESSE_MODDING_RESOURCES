/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

public class AIBeforeHitEvent
extends AIEvent {
    public final MobBeforeHitEvent event;

    public AIBeforeHitEvent(MobBeforeHitEvent event) {
        this.event = event;
    }
}

