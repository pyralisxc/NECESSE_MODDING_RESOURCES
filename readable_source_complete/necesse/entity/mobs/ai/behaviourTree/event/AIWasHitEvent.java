/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

public class AIWasHitEvent
extends AIEvent {
    public final MobWasHitEvent event;

    public AIWasHitEvent(MobWasHitEvent event) {
        this.event = event;
    }
}

