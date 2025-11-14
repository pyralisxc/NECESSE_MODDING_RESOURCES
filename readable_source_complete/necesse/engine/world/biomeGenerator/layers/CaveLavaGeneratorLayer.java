/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import necesse.engine.GameRandomNoise;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class CaveLavaGeneratorLayer
extends GeneratorLayer {
    protected float sizeModifier;
    protected float maxValue;
    protected GameRandomNoise noise;

    public CaveLavaGeneratorLayer(int layerSeed, float sizeModifier, float oceanPercent) {
        super(layerSeed, 0);
        this.sizeModifier = sizeModifier;
        this.maxValue = oceanPercent * 2.0f - 1.0f;
    }

    @Override
    public void init(GeneratorStack stack, GeneratorLayer parent, boolean isParentDifferentBranch, int worldSeed) {
        super.init(stack, parent, isParentDifferentBranch, worldSeed);
        this.noise = new GameRandomNoise(this.getRandom(0, 0).nextInt());
    }

    @Override
    protected int sample(int x, int y) {
        int scale = this.getStackScale();
        double value = this.noise.perlin2Fractal((double)x / (double)scale * (double)this.sizeModifier, (double)y / (double)scale * (double)this.sizeModifier, 4, 0.5);
        if (value < (double)this.maxValue) {
            return -1;
        }
        return this.getParent(x, y);
    }

    @Override
    protected Color getDebugColor(int value) {
        if (value == -1) {
            return BiomeGeneratorStack.lavaDebugColor;
        }
        return super.getDebugColor(value);
    }
}

