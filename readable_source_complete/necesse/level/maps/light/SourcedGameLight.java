/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import necesse.level.maps.light.GameLight;

public class SourcedGameLight {
    public final int sourceTileX;
    public final int sourceTileY;
    public final GameLight light;

    public SourcedGameLight(int sourceTileX, int sourceY, GameLight light) {
        this.sourceTileX = sourceTileX;
        this.sourceTileY = sourceY;
        this.light = light;
    }

    public String toString() {
        return this.light.toString() + "{" + this.sourceTileX + "," + this.sourceTileY + "}";
    }
}

