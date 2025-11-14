/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.SpiderNestsWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;

public class DeepSwampSpiderNestsWorldPreset
extends SpiderNestsWorldPreset {
    public DeepSwampSpiderNestsWorldPreset() {
        super(BiomeRegistry.SWAMP, LevelIdentifier.DEEP_CAVE_IDENTIFIER, 0.025f, "smallswampcavespider");
        this.armsMinRange = 15;
        this.armsMaxRange = 25;
    }

    @Override
    public boolean isValidPosition(int tileX, int tileY, int width, int height, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack) {
        return this.runCornerCheck(tileX, tileY, width, height, new WorldPreset.ValidTilePredicate(){

            @Override
            public boolean isValidPosition(int tileX, int tileY) {
                return generatorStack.getLazyBiomeID(tileX, tileY) == BiomeRegistry.SWAMP.getID();
            }
        });
    }
}

