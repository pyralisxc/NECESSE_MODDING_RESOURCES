/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import necesse.engine.GameRandomNoise;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class SimplexVeinGeneratorLayer
extends GeneratorLayer {
    protected float sizeModifier;
    protected float minValue;
    protected float placeChance;
    protected int placeValue;
    protected GameRandomNoise noise;

    public SimplexVeinGeneratorLayer(int layerSeed, float sizeModifier, float minValue, float placeChance, int placeValue) {
        super(layerSeed, 0);
        this.sizeModifier = sizeModifier;
        this.minValue = minValue;
        this.placeChance = placeChance;
        this.placeValue = placeValue;
    }

    @Override
    public void init(GeneratorStack stack, GeneratorLayer parent, boolean isParentDifferentBranch, int worldSeed) {
        super.init(stack, parent, isParentDifferentBranch, worldSeed);
        this.noise = new GameRandomNoise(this.getRandom(0, 0).nextInt());
    }

    @Override
    protected int sample(int x, int y) {
        int scale = this.getStackScale();
        double value = this.noise.simplex2Fractal((double)x / (double)scale * (double)this.sizeModifier, (double)y / (double)scale * (double)this.sizeModifier, 4, 0.5);
        if (Math.abs(value) > (double)this.minValue && (this.placeChance >= 1.0f || this.getRandom(x, y).getChance(this.placeChance))) {
            return this.placeValue;
        }
        return this.getParent(x, y);
    }

    @Override
    protected Color getDebugColor(int value) {
        if (value == this.placeValue) {
            return new Color(255, 0, 255);
        }
        return super.getDebugColor(value);
    }
}

