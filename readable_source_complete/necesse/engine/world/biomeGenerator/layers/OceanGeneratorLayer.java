/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import necesse.engine.GameRandomNoise;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class OceanGeneratorLayer
extends GeneratorLayer {
    protected float sizeModifier;
    protected float maxValue;
    protected float beachSize;
    protected GameRandomNoise noise;

    public OceanGeneratorLayer(int layerSeed, float sizeModifier, float oceanPercent, float beachSize) {
        super(layerSeed, 0);
        this.sizeModifier = sizeModifier;
        this.maxValue = oceanPercent * 2.0f - 1.0f;
        this.beachSize = beachSize;
    }

    @Override
    public void init(GeneratorStack stack, GeneratorLayer parent, boolean isParentDifferentBranch, int worldSeed) {
        super.init(stack, parent, isParentDifferentBranch, worldSeed);
        this.noise = new GameRandomNoise(this.getRandom(0, 0).nextInt());
    }

    protected double getValue(int tileX, int tileY) {
        int scale = this.getStackScale();
        return this.noise.perlin2Fractal((double)tileX / (double)scale * (double)this.sizeModifier, (double)tileY / (double)scale * (double)this.sizeModifier, 4, 0.5);
    }

    @Override
    protected int sample(int x, int y) {
        double value = this.getValue(x, y);
        if (value < (double)this.maxValue) {
            return -1;
        }
        if (value < (double)(this.maxValue + this.beachSize * this.sizeModifier)) {
            return -2;
        }
        return this.getParent(x, y);
    }

    @Override
    protected Color getDebugColor(int value) {
        if (value == -1) {
            return BiomeGeneratorStack.waterDebugColor;
        }
        if (value == -2) {
            return new Color(255, 246, 75);
        }
        return super.getDebugColor(value);
    }
}

