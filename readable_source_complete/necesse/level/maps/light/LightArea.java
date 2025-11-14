/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import necesse.level.maps.light.GameLight;

public class LightArea {
    public final int startX;
    public final int startY;
    public final int endX;
    public final int endY;
    public final int width;
    protected GameLight[] lights;

    public LightArea(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.width = endX - startX + 1;
    }

    public void initLights() {
        if (this.lights == null) {
            int height = this.endY - this.startY + 1;
            this.lights = new GameLight[this.width * height];
        }
    }

    public boolean isOutsideArea(int x, int y) {
        return x < this.startX || y < this.startY || x > this.endX || y > this.endY;
    }

    protected int getIndex(int x, int y) {
        return x - this.startX + (y - this.startY) * this.width;
    }

    protected void overwriteArea(LightArea other) {
        for (int x = this.startX; x <= this.endX; ++x) {
            for (int y = this.startY; y <= this.endY; ++y) {
                GameLight light = this.lights[this.getIndex(x, y)];
                if (light == null) continue;
                other.lights[other.getIndex((int)x, (int)y)] = light;
            }
        }
    }
}

