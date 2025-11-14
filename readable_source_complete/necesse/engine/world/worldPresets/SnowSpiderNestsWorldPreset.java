/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.SpiderNestsWorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;

public class SnowSpiderNestsWorldPreset
extends SpiderNestsWorldPreset {
    public SnowSpiderNestsWorldPreset() {
        super(BiomeRegistry.SNOW, LevelIdentifier.CAVE_IDENTIFIER, 0.064f, "blackcavespider");
    }

    @Override
    public void generateExtraContent(GameRandom random, Level level, PerformanceTimerManager timer, LinesGenerationWorldPreset linesGeneration, CellAutomaton cellAutomaton) {
        super.generateExtraContent(random, level, timer, linesGeneration, cellAutomaton);
        if (random.getChance(0.5f)) {
            level.setObject(linesGeneration.startTileX, linesGeneration.startTileY, ObjectRegistry.getObjectID("royaleggobject"));
            level.setObject(linesGeneration.startTileX, linesGeneration.startTileY + 1, 0);
        }
    }
}

