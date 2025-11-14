/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import necesse.level.maps.light.SourcedGameLight;

public class SourcedLightArea {
    public final int startX;
    public final int startY;
    public final int endX;
    public final int endY;
    public final int width;
    protected SourcedGameLight[] lights;

    public SourcedLightArea(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.width = endX - startX + 1;
    }

    public void initLights() {
        if (this.lights == null) {
            int height = this.endY - this.startY + 1;
            this.lights = new SourcedGameLight[this.width * height];
        }
    }

    public boolean isOutsideArea(int x, int y) {
        return x < this.startX || y < this.startY || x > this.endX || y > this.endY;
    }

    protected int getIndex(int x, int y) {
        return x - this.startX + (y - this.startY) * this.width;
    }
}

