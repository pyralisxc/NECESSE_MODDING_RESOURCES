/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public abstract class AdjacentGeneratorLayer
extends GeneratorLayer {
    public AdjacentGeneratorLayer(int layerSeed) {
        super(layerSeed, 0);
    }

    @Override
    protected int sample(int x, int y) {
        return this.sample(x, y, new int[]{this.getParent(x - 1, y - 1), this.getParent(x, y - 1), this.getParent(x + 1, y - 1), this.getParent(x - 1, y), this.getParent(x, y), this.getParent(x + 1, y), this.getParent(x - 1, y + 1), this.getParent(x, y + 1), this.getParent(x + 1, y + 1)});
    }

    protected abstract int sample(int var1, int var2, int[] var3);
}

