/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.IslandGeneration;

public class GeneratedIslandLayoutEvent
extends GameEvent {
    public final Level level;
    public final float islandSize;
    public final IslandGeneration islandGeneration;

    public GeneratedIslandLayoutEvent(Level level, float islandSize, IslandGeneration islandGeneration) {
        this.level = level;
        this.islandSize = islandSize;
        this.islandGeneration = islandGeneration;
    }
}

