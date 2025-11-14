/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import java.awt.Color;
import necesse.gfx.drawables.GLDrawOptionsList;

public class LinesDrawOptionsList {
    private final GLDrawOptionsList drawOptions = new GLDrawOptionsList();

    public synchronized LinesDrawOptionsList add(float x1, float y1, float x2, float y2, float[] advColor) {
        this.drawOptions.add(new float[]{x1, y1, x2, y2}, advColor);
        return this;
    }

    public synchronized LinesDrawOptionsList add(float x1, float y1, float x2, float y2, float red, float green, float blue, float alpha) {
        return this.add(x1, y1, x2, y2, new float[]{red, green, blue, alpha, red, green, blue, alpha});
    }

    public synchronized LinesDrawOptionsList add(float x1, float y1, float x2, float y2, Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        return this.add(x1, y1, x2, y2, red, green, blue, alpha);
    }

    public synchronized void draw() {
        this.drawOptions.draw(1, 2);
    }
}

