/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.GameRandomNoise;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.GeneratorStack;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class ChanceVeinStaticSizeGeneratorLayer
extends GeneratorLayer {
    protected int veinSize;
    protected float placeChance;
    protected int placeValue;
    protected GameRandomNoise noise;
    protected GameTileRange tileRange;

    public ChanceVeinStaticSizeGeneratorLayer(int layerSeed, int veinSize, float placeChance, int placeValue, boolean circular) {
        super(layerSeed, 0);
        if (veinSize < 0) {
            throw new IllegalArgumentException("Invalid vein size parameter");
        }
        this.veinSize = veinSize;
        this.placeChance = placeChance;
        this.placeValue = placeValue;
        if (circular) {
            this.tileRange = new GameTileRange(Math.max(veinSize / 2, 1), new Point[0]);
        }
    }

    @Override
    public void init(GeneratorStack stack, GeneratorLayer parent, boolean isParentDifferentBranch, int worldSeed) {
        super.init(stack, parent, isParentDifferentBranch, worldSeed);
        this.noise = new GameRandomNoise(this.getRandom(0, 0).nextInt());
    }

    @Override
    protected int sample(int x, int y) {
        int scaledTileX = x / this.veinSize;
        int scaledTileY = y / this.veinSize;
        GameRandom random = this.getRandom(scaledTileX, scaledTileY);
        if (random.getChance(this.placeChance)) {
            if (this.tileRange != null) {
                int modTileY;
                int modTileX = Math.abs(x % this.veinSize);
                if (this.tileRange.isWithinRange(this.veinSize / 2, this.veinSize / 2, modTileX, modTileY = Math.abs(y % this.veinSize))) {
                    return this.placeValue;
                }
                return -2;
            }
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

