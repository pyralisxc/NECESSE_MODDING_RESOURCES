/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import necesse.engine.world.biomeGenerator.layers.RandomReplaceGeneratorLayer;

public class RandomReplaceValueGeneratorLayer
extends RandomReplaceGeneratorLayer {
    protected int valueToReplace;
    protected int valueToPlace;

    public RandomReplaceValueGeneratorLayer(int layerSeed, int valueToReplace, float replaceChance, int valueToPlace) {
        super(layerSeed, replaceChance);
        this.valueToReplace = valueToReplace;
        this.valueToPlace = valueToPlace;
    }

    @Override
    protected boolean shouldReplaceValue(int value) {
        return value == this.valueToReplace;
    }

    @Override
    protected int getPlaceValue(int x, int y) {
        return this.valueToPlace;
    }
}

