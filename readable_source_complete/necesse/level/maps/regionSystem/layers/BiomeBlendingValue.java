/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

public class BiomeBlendingValue {
    public final short biomeID;
    public final int sourceTileX;
    public final int sourceTileY;
    public final byte value;

    public BiomeBlendingValue(short biomeID, int sourceTileX, int sourceTileY, byte value) {
        this.biomeID = biomeID;
        this.sourceTileX = sourceTileX;
        this.sourceTileY = sourceTileY;
        this.value = value;
    }
}

