/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.GameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;

public class GeneratedCaveOresEvent
extends GameEvent {
    public final Level level;
    public final CaveGeneration caveGeneration;

    public GeneratedCaveOresEvent(Level level, CaveGeneration caveGeneration) {
        this.level = level;
        this.caveGeneration = caveGeneration;
    }
}

