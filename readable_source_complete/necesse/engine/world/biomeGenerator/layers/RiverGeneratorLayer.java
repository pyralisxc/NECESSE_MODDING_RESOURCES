/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import necesse.engine.GameRandomNoise;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class RiverGeneratorLayer
extends GeneratorLayer {
    protected float spreadModifier;
    protected float size;
    protected float beachSize;
    protected GameRandomNoise noise;

    public RiverGeneratorLayer(int layerSeed, float spreadModifier, float size, float beachSize) {
        super(layerSeed, 0);
        this.spreadModifier = spreadModifier;
        this.size = size;
        this.beachSize = beachSize;
    }

    @Override
    public void init(GeneratorStack stack, GeneratorLayer parent, boolean isParentDifferentBranch, int worldSeed) {
        super.init(stack, parent, isParentDifferentBranch, worldSeed);
        this.noise = new GameRandomNoise(this.getRandom(0, 0).nextInt());
    }

    @Override
    protected int sample(int x, int y) {
        float maxValue;
        int parent = this.getParent(x, y);
        if (parent == -1) {
            return parent;
        }
        int scale = this.getStackScale();
        double value = this.noise.perlin2Fractal((double)x / (double)scale * (double)this.spreadModifier, (double)y / (double)scale * (double)this.spreadModifier, 4, 0.5);
        double abs = Math.abs(value);
        if (abs < (double)(maxValue = this.size * this.spreadModifier)) {
            return -1;
        }
        if (abs < (double)(maxValue + this.beachSize * this.spreadModifier)) {
            return -2;
        }
        return parent;
    }

    @Override
    protected Color getDebugColor(int value) {
        if (value == -1) {
            return BiomeGeneratorStack.waterDebugColor;
        }
        return super.getDebugColor(value);
    }
}

