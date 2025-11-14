/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import java.awt.Color;
import necesse.gfx.drawables.GLDrawOptionsList;

public class LineStripDrawOptionsList {
    private final GLDrawOptionsList drawOptions = new GLDrawOptionsList();

    public synchronized LineStripDrawOptionsList add(float x1, float y1, float x2, float y2, float red, float green, float blue, float alpha) {
        this.drawOptions.add(new float[]{x1, y1}, new float[]{red, green, blue, alpha});
        return this;
    }

    public synchronized LineStripDrawOptionsList add(float x1, float y1, float x2, float y2, Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        return this.add(x1, y1, x2, y2, red, green, blue, alpha);
    }

    public synchronized void draw() {
        this.drawOptions.draw(3, 2);
    }
}

