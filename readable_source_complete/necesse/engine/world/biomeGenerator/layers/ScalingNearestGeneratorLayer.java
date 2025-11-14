/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class ScalingNearestGeneratorLayer
extends GeneratorLayer {
    public ScalingNearestGeneratorLayer(int layerSeed) {
        super(layerSeed, 1);
    }

    @Override
    protected int sample(int x, int y) {
        return this.getParent(x >> 1, y >> 1);
    }
}

