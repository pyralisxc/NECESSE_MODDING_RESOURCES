/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public abstract class RandomReplaceGeneratorLayer
extends GeneratorLayer {
    protected float replaceChance;

    public RandomReplaceGeneratorLayer(int layerSeed, float replaceChance) {
        super(layerSeed, 0);
        this.replaceChance = replaceChance;
    }

    @Override
    protected int sample(int x, int y) {
        int parent = this.getParent(x, y);
        if (this.shouldReplaceValue(parent) && this.getRandom(x, y).getChance(this.replaceChance)) {
            return this.getPlaceValue(x, y);
        }
        return parent;
    }

    protected abstract boolean shouldReplaceValue(int var1);

    protected abstract int getPlaceValue(int var1, int var2);
}

