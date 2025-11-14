/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.BiomeCenterWorldPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.Level;
import necesse.level.maps.presets.AncientVultureArenaPreset;

public class AncientVultureArenaWorldPreset
extends BiomeCenterWorldPreset {
    public int size = 36;

    public AncientVultureArenaWorldPreset() {
        super(BiomeRegistry.DESERT);
        this.pointsPerRegion = 0.015f;
        this.randomAttempts = 20;
        this.sectionMaxRegionCount = 400;
        this.sectionMinRegionCount = 25;
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER);
    }

    @Override
    public boolean isValidSectionRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return true;
    }

    @Override
    public boolean isValidFinalRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        int startTileX = centerTileX - this.size / 2;
        int startTileY = centerTileY - this.size / 2;
        return this.runCornerCheck(startTileX, startTileY, this.size, this.size, (tileX, tileY) -> generatorStack.getLazyBiomeID(tileX, tileY) == this.biome.getID());
    }

    @Override
    public void onFoundRegion(int regionX, int regionY, GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        final int startTileX = centerTileX - this.size / 2;
        final int startTileY = centerTileY - this.size / 2;
        presetsRegion.addPreset((WorldPreset)this, startTileX, startTileY, new Dimension(this.size, this.size), new String[]{"minibiomes", "loot"}, new LevelPresetsRegion.WorldPresetPlaceFunction(){

            @Override
            public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                AncientVultureArenaPreset preset = new AncientVultureArenaPreset(AncientVultureArenaWorldPreset.this.size, random);
                int applyTileX = startTileX + AncientVultureArenaWorldPreset.this.size / 2 - preset.width / 2;
                int applyTileY = startTileY + AncientVultureArenaWorldPreset.this.size / 2 - preset.height / 2;
                WorldPreset.ensureRegionsAreGenerated(level, applyTileX, applyTileY, AncientVultureArenaWorldPreset.this.size, AncientVultureArenaWorldPreset.this.size);
                preset.applyToLevel(level, applyTileX, applyTileY);
            }
        });
    }
}

