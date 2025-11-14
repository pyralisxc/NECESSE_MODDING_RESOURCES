/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;

public class GeneratedIslandFeatureEvent
extends GameEvent {
    public final Level level;
    public final float islandSize;

    public GeneratedIslandFeatureEvent(Level level, float islandSize) {
        this.level = level;
        this.islandSize = islandSize;
    }
}

