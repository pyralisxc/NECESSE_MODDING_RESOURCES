/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameMath;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.managers.BiomeBlendingManager;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.layers.BiomeBlendingValue;

public class BiomeBlendingOptions {
    protected HashMap<Integer, BiomeBlendingValue> blendingValues = new HashMap();
    protected boolean shouldUpdateBlendOptions = true;
    protected BiomeBlendingOption[] options;

    public BiomeBlendingOption[] getBlendingOptions(Region region, int tileX, int tileY) {
        if (this.shouldUpdateBlendOptions) {
            this.options = new BiomeBlendingOption[this.blendingValues.size()];
            int currentIndex = 0;
            for (BiomeBlendingValue value : this.blendingValues.values()) {
                float[] blendValues = new float[]{this.getBlendValueFloat(region, value.biomeID, tileX - 1, tileY - 1), this.getBlendValueFloat(region, value.biomeID, tileX, tileY - 1), this.getBlendValueFloat(region, value.biomeID, tileX + 1, tileY - 1), this.getBlendValueFloat(region, value.biomeID, tileX - 1, tileY), (float)value.value / (float)BiomeBlendingManager.MAX_VALUE, this.getBlendValueFloat(region, value.biomeID, tileX + 1, tileY), this.getBlendValueFloat(region, value.biomeID, tileX - 1, tileY + 1), this.getBlendValueFloat(region, value.biomeID, tileX, tileY + 1), this.getBlendValueFloat(region, value.biomeID, tileX + 1, tileY + 1)};
                BiomeBlendingOption option = new BiomeBlendingOption(value.biomeID, GameMath.getAverage(blendValues[0], blendValues[1], blendValues[3], blendValues[4]), GameMath.getAverage(blendValues[1], blendValues[4]), GameMath.getAverage(blendValues[1], blendValues[2], blendValues[4]), GameMath.getAverage(blendValues[4], blendValues[5]), blendValues[4], GameMath.getAverage(blendValues[3], blendValues[4]), GameMath.getAverage(blendValues[3], blendValues[4], blendValues[6], blendValues[7]), GameMath.getAverage(blendValues[4], blendValues[7]), GameMath.getAverage(blendValues[4], blendValues[5], blendValues[7], blendValues[8]));
                this.options[currentIndex++] = option;
            }
            Arrays.sort(this.options, Comparator.comparingInt(o -> BiomeRegistry.getBiome(o.biomeID).getBiomeBlendingPriority()));
            this.shouldUpdateBlendOptions = false;
        }
        return this.options;
    }

    protected float getBlendValueFloat(Region region, int biomeID, int tileX, int tileY) {
        Region nextRegion = region.getRegionByTile(tileX, tileY, false);
        if (nextRegion == null) {
            return 0.0f;
        }
        int regionTileX = tileX - nextRegion.tileXOffset;
        int regionTileY = tileY - nextRegion.tileYOffset;
        if (nextRegion.biomeLayer.getBiomeIDByRegion(regionTileX, regionTileY) == biomeID) {
            return 1.0f;
        }
        BiomeBlendingOptions blendingOptions = nextRegion.biomeBlendingLayer.getBlendingOptionsByRegion(regionTileX, regionTileY);
        BiomeBlendingValue blendValue = blendingOptions.getBlendValue(biomeID);
        return blendValue == null ? 0.0f : (float)blendValue.value / (float)BiomeBlendingManager.MAX_VALUE;
    }

    public void markUpdateBlendOptions() {
        this.shouldUpdateBlendOptions = true;
    }

    public BiomeBlendingValue getBlendValue(int biomeID) {
        return this.blendingValues.get(biomeID);
    }

    public Iterable<Map.Entry<Integer, BiomeBlendingValue>> getBlendingSources() {
        return this.blendingValues.entrySet();
    }

    public LinkedList<BiomeBlendingValue> getBlendingValues() {
        return new LinkedList<BiomeBlendingValue>(this.blendingValues.values());
    }

    public void removeBiome(int biomeID) {
        this.blendingValues.remove(biomeID);
        this.markUpdateBlendOptions();
    }

    public int removeSourceAndReturnBiomeID(int biomeID, int sourceTileX, int sourceTileY) {
        int foundBiomeID = -1;
        for (Map.Entry<Integer, BiomeBlendingValue> entry : this.blendingValues.entrySet()) {
            BiomeBlendingValue value = entry.getValue();
            if (biomeID != -1 && biomeID != entry.getKey() || value.sourceTileX != sourceTileX || value.sourceTileY != sourceTileY) continue;
            foundBiomeID = entry.getKey();
            break;
        }
        if (foundBiomeID != -1) {
            this.blendingValues.remove(foundBiomeID);
            this.markUpdateBlendOptions();
        }
        return foundBiomeID;
    }

    public void setBlendValue(int biomeID, int sourceTileX, int sourceTileY, int value) {
        this.blendingValues.put(biomeID, new BiomeBlendingValue((short)biomeID, sourceTileX, sourceTileY, (byte)value));
        this.markUpdateBlendOptions();
    }

    public boolean isEmpty() {
        return this.blendingValues.isEmpty();
    }

    public void addDebugTooltips(StringTooltips tooltips) {
        tooltips.add("Blends:");
        this.blendingValues.forEach((biomeID, blendValue) -> tooltips.add("\t" + BiomeRegistry.getBiomeStringID(biomeID) + ": " + blendValue.value + " (" + blendValue.sourceTileX + "x" + blendValue.sourceTileY + ")"));
        if (this.options != null) {
            for (BiomeBlendingOption option : this.options) {
                tooltips.add("\t" + this.getDebugTooltips(option));
            }
        }
    }

    public String getDebugTooltips(BiomeBlendingOption option) {
        return BiomeRegistry.getBiomeStringID(option.biomeID) + ": TL: " + GameMath.toDecimals(option.topLeftAlpha, 1) + ", TM: " + GameMath.toDecimals(option.topMidAlpha, 1) + ", TR: " + GameMath.toDecimals(option.topRightAlpha, 1) + ", LM: " + GameMath.toDecimals(option.leftMidAlpha, 1) + ", C: " + GameMath.toDecimals(option.centerAlpha, 1) + ", RM: " + GameMath.toDecimals(option.rightMidAlpha, 1) + ", BL: " + GameMath.toDecimals(option.bottomLeftAlpha, 1) + ", BM: " + GameMath.toDecimals(option.bottomMidAlpha, 1) + ", BR: " + GameMath.toDecimals(option.bottomRightAlpha, 1);
    }

    public static class BiomeBlendingOption {
        public final int biomeID;
        public final float topLeftAlpha;
        public final float topMidAlpha;
        public final float topRightAlpha;
        public final float rightMidAlpha;
        public final float centerAlpha;
        public final float leftMidAlpha;
        public final float bottomLeftAlpha;
        public final float bottomMidAlpha;
        public final float bottomRightAlpha;

        protected BiomeBlendingOption(int biomeID, float topLeftAlpha, float topMidAlpha, float topRightAlpha, float rightMidAlpha, float centerAlpha, float leftMidAlpha, float bottomLeftAlpha, float bottomMidAlpha, float bottomRightAlpha) {
            this.biomeID = biomeID;
            this.topLeftAlpha = topLeftAlpha;
            this.topMidAlpha = topMidAlpha;
            this.topRightAlpha = topRightAlpha;
            this.rightMidAlpha = rightMidAlpha;
            this.centerAlpha = centerAlpha;
            this.leftMidAlpha = leftMidAlpha;
            this.bottomLeftAlpha = bottomLeftAlpha;
            this.bottomMidAlpha = bottomMidAlpha;
            this.bottomRightAlpha = bottomRightAlpha;
        }
    }
}

