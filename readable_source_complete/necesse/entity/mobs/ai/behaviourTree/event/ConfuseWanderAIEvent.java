/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.event;

import java.awt.geom.Point2D;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;

public class ConfuseWanderAIEvent
extends AIEvent {
    public final boolean canDecreaseTimer;
    public final long confusionTimer;
    public final Point2D.Float nextDirection;

    public ConfuseWanderAIEvent(boolean canDecreaseTimer, long confusionTimer, Point2D.Float nextDirection) {
        this.canDecreaseTimer = canDecreaseTimer;
        this.confusionTimer = confusionTimer;
        this.nextDirection = nextDirection;
    }

    public ConfuseWanderAIEvent(long confusionTimer, Point2D.Float nextDirection) {
        this(false, confusionTimer, nextDirection);
    }

    public ConfuseWanderAIEvent(long confusionTimer) {
        this(confusionTimer, null);
    }
}

