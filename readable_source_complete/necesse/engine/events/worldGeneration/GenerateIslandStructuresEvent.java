/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.PreventableGameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.IslandGeneration;

public class GenerateIslandStructuresEvent
extends PreventableGameEvent {
    public final Level level;
    public final float islandSize;
    public final IslandGeneration islandGeneration;

    public GenerateIslandStructuresEvent(Level level, float islandSize, IslandGeneration islandGeneration) {
        this.level = level;
        this.islandSize = islandSize;
        this.islandGeneration = islandGeneration;
    }
}

