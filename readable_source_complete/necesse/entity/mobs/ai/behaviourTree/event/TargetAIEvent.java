/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

public class TargetAIEvent
extends AIEvent {
    public final Mob target;

    public TargetAIEvent(Mob target) {
        this.target = target;
    }
}

