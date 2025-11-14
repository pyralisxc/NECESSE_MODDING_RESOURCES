/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.PreventableGameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;

public class GenerateCaveMiniBiomesEvent
extends PreventableGameEvent {
    public final Level level;
    public final CaveGeneration caveGeneration;

    public GenerateCaveMiniBiomesEvent(Level level, CaveGeneration caveGeneration) {
        this.level = level;
        this.caveGeneration = caveGeneration;
    }
}

