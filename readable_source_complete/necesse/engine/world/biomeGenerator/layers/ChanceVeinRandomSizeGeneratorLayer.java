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

public class ChanceVeinRandomSizeGeneratorLayer
extends GeneratorLayer {
    protected int minVeinSize;
    protected int maxVeinSize;
    protected float placeChance;
    protected int placeValue;
    protected GameRandomNoise noise;
    protected GameTileRange[] tileRanges;

    public ChanceVeinRandomSizeGeneratorLayer(int layerSeed, int minVeinSize, int maxVeinSize, float placeChance, int placeValue, boolean circular) {
        super(layerSeed, 0);
        if (minVeinSize < 0 || maxVeinSize < 0 || minVeinSize > maxVeinSize) {
            throw new IllegalArgumentException("Invalid vein size parameters");
        }
        this.minVeinSize = minVeinSize;
        this.maxVeinSize = maxVeinSize;
        this.placeChance = placeChance;
        this.placeValue = placeValue;
        if (circular) {
            int deltaSize = maxVeinSize - minVeinSize + 1;
            this.tileRanges = new GameTileRange[deltaSize];
            for (int i = 0; i < deltaSize; ++i) {
                this.tileRanges[i] = new GameTileRange(Math.max((minVeinSize + i) / 2, 2), new Point[0]);
            }
        }
    }

    @Override
    public void init(GeneratorStack stack, GeneratorLayer parent, boolean isParentDifferentBranch, int worldSeed) {
        super.init(stack, parent, isParentDifferentBranch, worldSeed);
        this.noise = new GameRandomNoise(this.getRandom(0, 0).nextInt());
    }

    @Override
    protected int sample(int x, int y) {
        int scaledTileX = x / this.maxVeinSize;
        int scaledTileY = y / this.maxVeinSize;
        GameRandom random = this.getRandom(scaledTileX, scaledTileY);
        if (random.getChance(this.placeChance)) {
            int modTileX = Math.abs(x % this.maxVeinSize);
            int modTileY = Math.abs(y % this.maxVeinSize);
            if (this.tileRanges != null) {
                int offsetTileY;
                int offsetTileX;
                int offsetY;
                int veinSize;
                int padding = (veinSize = this.minVeinSize + random.nextInt(this.maxVeinSize - this.minVeinSize + 1)) - this.minVeinSize;
                int offsetX = padding < 1 ? 0 : random.nextInt(padding);
                if (this.tileRanges[veinSize - this.minVeinSize].isWithinRange(veinSize / 2 - offsetX, veinSize / 2 - (offsetY = padding < 1 ? 0 : random.nextInt(padding)), offsetTileX = modTileX - offsetX, offsetTileY = modTileY - offsetY)) {
                    return this.placeValue;
                }
                return -2;
            }
            int veinSizeX = this.minVeinSize + random.nextInt(this.maxVeinSize - this.minVeinSize + 1);
            int veinSizeY = this.minVeinSize + random.nextInt(this.maxVeinSize - this.minVeinSize + 1);
            int paddingX = veinSizeX - this.minVeinSize;
            int paddingY = veinSizeY - this.minVeinSize;
            int offsetX = paddingX < 1 ? 0 : random.nextInt(paddingX);
            int offsetY = paddingY < 1 ? 0 : random.nextInt(paddingY);
            int offsetTileX = modTileX - offsetX;
            int offsetTileY = modTileY - offsetY;
            if (offsetTileX >= 0 && offsetTileY >= 0 && offsetTileX < veinSizeX && offsetTileY < veinSizeY) {
                return this.placeValue;
            }
            return -2;
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

