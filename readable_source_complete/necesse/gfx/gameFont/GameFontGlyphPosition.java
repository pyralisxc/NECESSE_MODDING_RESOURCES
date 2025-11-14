/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

public class GameFontGlyphPosition {
    public final int textureX;
    public final int textureY;
    public final int width;
    public final int height;
    public int drawXOffset;
    public int drawYOffset;

    public GameFontGlyphPosition(int textureX, int textureY, int width, int height) {
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = width;
        this.height = height;
    }
}

