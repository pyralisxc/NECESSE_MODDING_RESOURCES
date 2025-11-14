/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.events.worldGeneration;

import necesse.engine.events.PreventableGameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.PresetGeneration;

public class GenerateCaveStructuresEvent
extends PreventableGameEvent {
    public final Level level;
    public final CaveGeneration caveGeneration;
    public final PresetGeneration presetGeneration;

    public GenerateCaveStructuresEvent(Level level, CaveGeneration caveGeneration, PresetGeneration presetGeneration) {
        this.level = level;
        this.caveGeneration = caveGeneration;
        this.presetGeneration = presetGeneration;
    }
}

