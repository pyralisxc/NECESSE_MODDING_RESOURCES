/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

import necesse.gfx.gameFont.FontBasicOptions;

public abstract class AbstractGameFont<T extends FontBasicOptions> {
    public abstract float drawChar(float var1, float var2, char var3, T var4);

    public abstract float drawCharShadow(float var1, float var2, char var3, T var4);

    public float drawString(float x, float y, String str, T options) {
        int width = 0;
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            width = (int)((float)width + this.drawChar(x + (float)width, y, ch, options));
        }
        return width;
    }

    public float drawStringShadow(float x, float y, String str, T options) {
        int width = 0;
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            width = (int)((float)width + this.drawCharShadow(x + (float)width, y, ch, options));
        }
        return width;
    }

    public abstract float getWidth(char var1, T var2);

    public float getWidth(String str, T options) {
        float width = 0.0f;
        for (int i = 0; i < str.length(); ++i) {
            width += this.getWidth(str.charAt(i), options);
        }
        return width;
    }

    public abstract float getHeight(char var1, T var2);

    public float getHeight(String str, T options) {
        float height = 0.0f;
        for (int i = 0; i < str.length(); ++i) {
            height = Math.max(height, this.getHeight(str.charAt(i), options));
        }
        return height;
    }

    public abstract int getWidthCeil(char var1, T var2);

    public int getWidthCeil(String str, T options) {
        int width = 0;
        for (int i = 0; i < str.length(); ++i) {
            width += this.getWidthCeil(str.charAt(i), options);
        }
        return width;
    }

    public abstract int getFontHeight();

    public abstract int getHeightCeil(char var1, T var2);

    public int getHeightCeil(String str, T options) {
        int height = 0;
        for (int i = 0; i < str.length(); ++i) {
            height = Math.max(height, this.getHeightCeil(str.charAt(i), options));
        }
        return height;
    }

    public abstract boolean canDraw(char var1);

    public abstract void deleteTextures();
}

