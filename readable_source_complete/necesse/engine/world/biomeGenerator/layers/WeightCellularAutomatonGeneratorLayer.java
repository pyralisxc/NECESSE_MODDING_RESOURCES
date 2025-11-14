/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.layers.AdjacentGeneratorLayer;

public class WeightCellularAutomatonGeneratorLayer
extends AdjacentGeneratorLayer {
    public WeightCellularAutomatonGeneratorLayer(int layerSeed) {
        super(layerSeed);
    }

    @Override
    protected int sample(int x, int y, int[] adjacent) {
        GameRandom random = this.getRandom(x, y);
        int index = random.nextInt(adjacent.length);
        return adjacent[index];
    }
}

