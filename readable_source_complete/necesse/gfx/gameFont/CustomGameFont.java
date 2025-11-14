/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

import necesse.gfx.gameFont.FontBasicOptions;
import necesse.gfx.gameFont.GameFont;
import necesse.gfx.gameFont.GameFontGlyphPositionTexture;
import necesse.gfx.gameTexture.GameTexture;

public class CustomGameFont
extends GameFont {
    public static final String[] fontLayout = new String[]{"abcdefghijklmnopqrstuvwyxz ", "ABCDEFGHIJKLMNOPQRSTUVWYXZ", "1234567890+-=,;.:-_/\\()[]{}", "!\"'#%&*|^<>$?`\u00b4~\u00a1\u00bf\u00a3\u20ac@\u00df", "\u00e0\u00e1\u00e2\u00e3\u00e4\u00e7\u00e8\u00e9\u00ea\u00eb\u00ec\u00ed\u00ee\u00ef\u00f1\u0144\u0148\u00f2\u00f3\u00f4\u00f5\u00f6\u00f9\u00fa\u00fb\u00fc", "\u00c0\u00c1\u00c2\u00c3\u00c4\u00c7\u00c8\u00c9\u00ca\u00cb\u00cc\u00cd\u00ce\u00cf\u00d1\u0143\u0147\u00d2\u00d3\u00d4\u00d5\u00d6\u00d9\u00da\u00db\u00dc", "\u00e6\u00f8\u00e5", "\u00c6\u00d8\u00c5"};
    private final CharArray charArray;
    private final int fontHeight;
    private final float fontRatio;

    public static boolean fontTextureContains(char ch) {
        for (String line : fontLayout) {
            if (line.indexOf(ch) == -1) continue;
            return true;
        }
        return false;
    }

    public CustomGameFont(GameTexture texture, int fontWidth, int fontHeight) {
        this.charArray = new CharArray(texture, fontWidth, fontHeight);
        this.fontHeight = fontHeight;
        this.fontRatio = (float)fontWidth / (float)fontHeight;
    }

    @Override
    public float drawChar(float x, float y, char ch, FontBasicOptions options) {
        int sizeWidth = (int)((float)options.getSize() * this.fontRatio);
        GameFontGlyphPositionTexture glyph = null;
        if (ch < this.charArray.chars.length) {
            glyph = this.charArray.chars[ch];
        }
        if (glyph == null) {
            glyph = this.charArray.chars[63];
        }
        glyph.texture.bindTexture();
        options.applyGLColor();
        glyph.draw(x, y, sizeWidth, options.getSize());
        return sizeWidth;
    }

    @Override
    public float drawCharShadow(float x, float y, char ch, FontBasicOptions options) {
        int sizeWidth = (int)((float)options.getSize() * this.fontRatio);
        GameFontGlyphPositionTexture glyph = null;
        if (ch < this.charArray.chars.length) {
            glyph = this.charArray.chars[ch];
        }
        if (glyph == null) {
            glyph = this.charArray.chars[63];
        }
        glyph.texture.bindTexture();
        options.applyGLShadowColor();
        int[] offset = options.getShadowOffset();
        glyph.draw(x + (float)offset[0], y + (float)offset[1], sizeWidth, options.getSize());
        return sizeWidth;
    }

    @Override
    public float getWidth(char ch, FontBasicOptions options) {
        return (float)options.getSize() * this.fontRatio;
    }

    @Override
    public float getHeight(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontHeight;
        return ratio * (float)this.fontHeight;
    }

    @Override
    public int getWidthCeil(char ch, FontBasicOptions options) {
        return (int)((float)options.getSize() * this.fontRatio);
    }

    @Override
    public int getFontHeight() {
        return this.fontHeight;
    }

    @Override
    public int getHeightCeil(char ch, FontBasicOptions options) {
        float ratio = (float)options.getSize() / (float)this.fontHeight;
        return (int)(ratio * (float)this.fontHeight);
    }

    @Override
    public boolean canDraw(char ch) {
        return this.charArray.canDraw(ch);
    }

    @Override
    public void deleteTextures() {
        this.charArray.texture.delete();
    }

    @Override
    public GameFont updateFont(String additionalChars) {
        return this;
    }

    public CharArray getCharArray() {
        return this.charArray;
    }

    public static class CharArray {
        public final GameFontGlyphPositionTexture[] chars;
        public final GameTexture texture;

        public CharArray(GameTexture texture, int fontWidth, int fontHeight) {
            this.texture = texture;
            texture.setBlendQuality(GameTexture.BlendQuality.NEAREST);
            int maxChar = 0;
            for (String line : fontLayout) {
                for (int i = 0; i < line.length(); ++i) {
                    maxChar = Math.max(maxChar, line.charAt(i) + '\u0001');
                }
            }
            this.chars = new GameFontGlyphPositionTexture[maxChar];
            for (int line = 0; line < fontLayout.length; ++line) {
                for (int i = 0; i < fontLayout[line].length(); ++i) {
                    char ch = fontLayout[line].charAt(i);
                    this.chars[ch] = new GameFontGlyphPositionTexture(texture, i * fontWidth, line * fontHeight, fontWidth, fontHeight);
                }
            }
        }

        public boolean canDraw(char ch) {
            return ch < this.chars.length && this.chars[ch] != null;
        }
    }
}

