/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.friendly.human.HumanMob;

public class SettlerInteractReqestAIEvent
extends AIEvent {
    public final HumanMob from;
    public boolean accepted;

    public SettlerInteractReqestAIEvent(HumanMob from) {
        this.from = from;
    }
}

