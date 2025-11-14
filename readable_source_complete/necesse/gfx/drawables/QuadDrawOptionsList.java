/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import java.awt.Color;
import necesse.gfx.drawables.GLDrawOptionsList;

public class QuadDrawOptionsList {
    private final GLDrawOptionsList drawOptions = new GLDrawOptionsList();

    public synchronized QuadDrawOptionsList add(int drawX, int drawY, int width, int height, float[] advColor) {
        this.drawOptions.add(new float[]{drawX, drawY, drawX + width, drawY, drawX + width, drawY + height, drawX, drawY + height}, advColor);
        return this;
    }

    public synchronized QuadDrawOptionsList add(int drawX, int drawY, int width, int height, float red, float green, float blue, float alpha) {
        return this.add(drawX, drawY, width, height, new float[]{red, green, blue, alpha, red, green, blue, alpha, red, green, blue, alpha, red, green, blue, alpha});
    }

    public synchronized QuadDrawOptionsList add(int drawX, int drawY, int width, int height, Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        return this.add(drawX, drawY, width, height, red, green, blue, alpha);
    }

    public synchronized void draw() {
        this.drawOptions.draw(7, 4);
    }
}

