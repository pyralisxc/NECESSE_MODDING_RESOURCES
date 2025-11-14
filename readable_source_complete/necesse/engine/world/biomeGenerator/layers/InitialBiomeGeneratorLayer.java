/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;
import necesse.level.maps.biomes.Biome;

public class InitialBiomeGeneratorLayer
extends GeneratorLayer {
    public InitialBiomeGeneratorLayer(int layerSeed) {
        super(layerSeed, 0);
    }

    @Override
    protected Color getDebugColor(int value) {
        if (value >= 0 && value < BiomeRegistry.getTotalBiomes()) {
            Biome biome = BiomeRegistry.getBiome(value);
            return biome.getDebugBiomeColor();
        }
        return null;
    }

    @Override
    protected int sample(int x, int y) {
        GameRandom random = this.getRandom(x, y);
        return BiomeRegistry.getRandomBiome(random).getID();
    }
}

