/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class RandomPlaceGeneratorLayer
extends GeneratorLayer {
    protected float chance;
    protected int value;

    public RandomPlaceGeneratorLayer(int layerSeed, float chance, int placeValue) {
        super(layerSeed, 0);
        this.chance = chance;
        this.value = placeValue;
    }

    @Override
    protected Color getDebugColor(int value) {
        if (value == this.value) {
            return new Color(255, 0, 255);
        }
        return null;
    }

    @Override
    protected int sample(int x, int y) {
        GameRandom random = this.getRandom(x, y);
        if (random.getChance(this.chance)) {
            return this.value;
        }
        return this.getParent(x, y);
    }
}

