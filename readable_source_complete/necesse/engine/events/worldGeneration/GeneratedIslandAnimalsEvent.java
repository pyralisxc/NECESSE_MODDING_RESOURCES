/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.IslandGeneration;

public class GeneratedIslandAnimalsEvent
extends GameEvent {
    public final Level level;
    public float islandSize;
    public final IslandGeneration islandGeneration;

    public GeneratedIslandAnimalsEvent(Level level, float islandSize, IslandGeneration islandGeneration) {
        this.level = level;
        this.islandSize = islandSize;
        this.islandGeneration = islandGeneration;
    }
}

