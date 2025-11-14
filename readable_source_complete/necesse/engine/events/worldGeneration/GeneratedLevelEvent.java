/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;

public class GeneratedLevelEvent
extends GameEvent {
    public final Level level;

    public GeneratedLevelEvent(Level level) {
        this.level = level;
    }
}

