/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;
import necesse.level.maps.biomes.Biome;

public abstract class CaveRockPlaceGeneratorLayer
extends GeneratorLayer {
    public CaveRockPlaceGeneratorLayer(int layerSeed) {
        super(layerSeed, 0);
    }

    @Override
    protected int sample(int x, int y) {
        Biome biome = this.getBiome(x, y);
        if (this.getRandom(x, y).getChance(this.getRockPlaceChance(biome, x, y))) {
            return -1;
        }
        return this.getParent(x, y);
    }

    protected abstract Biome getBiome(int var1, int var2);

    protected abstract float getRockPlaceChance(Biome var1, int var2, int var3);

    @Override
    protected Color getDebugColor(int value) {
        if (value == -1) {
            return new Color(255, 0, 255);
        }
        return super.getDebugColor(value);
    }
}

