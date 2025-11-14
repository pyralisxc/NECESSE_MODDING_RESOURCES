/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.util.GameMath;
import necesse.gfx.gameFont.AbstractGameFont;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFont;

public class GameFontHandler {
    public final SizeManager regularFonts = new SizeManager();
    public final SizeManager outlineFonts = new SizeManager();

    public void updateFont(String additionalChars) {
        this.regularFonts.fonts.forEach(f -> f.updateFont(additionalChars));
        this.outlineFonts.fonts.forEach(f -> f.updateFont(additionalChars));
    }

    private GameFont get(FontOptions options) {
        if (options.getOutline() && !this.outlineFonts.fonts.isEmpty()) {
            return this.outlineFonts.getFont(options.getSize(), options.isPixelFont());
        }
        return this.regularFonts.getFont(options.getSize(), options.isPixelFont());
    }

    public float drawChar(float x, float y, char ch, FontOptions options) {
        GameFont font = this.get(options);
        if (options.hasShadow()) {
            font.drawCharShadow(x, y, ch, options);
        }
        return font.drawChar(x, y, ch, options);
    }

    public float drawCharShadow(float x, float y, char ch, FontOptions options) {
        return this.get(options).drawCharShadow(x, y, ch, options);
    }

    public float drawCharNoShadow(float x, float y, char ch, FontOptions options) {
        return this.get(options).drawChar(x, y, ch, options);
    }

    public float drawString(float x, float y, String str, FontOptions options) {
        GameFont font = this.get(options);
        if (options.hasShadow()) {
            font.drawStringShadow(x, y, str, options);
        }
        return font.drawString(x, y, str, options);
    }

    public float drawStringNoShadow(float x, float y, String str, FontOptions options) {
        return this.get(options).drawString(x, y, str, options);
    }

    public float drawStringShadow(float x, float y, String str, FontOptions options) {
        return this.get(options).drawStringShadow(x, y, str, options);
    }

    public float getWidth(char ch, FontOptions options) {
        return this.get(options).getWidth(ch, options);
    }

    public float getWidth(String str, FontOptions options) {
        return this.get(options).getWidth(str, options);
    }

    public float getHeight(char ch, FontOptions options) {
        return this.get(options).getHeight(ch, options);
    }

    public float getHeight(String str, FontOptions options) {
        return this.get(options).getHeight(str, options);
    }

    public int getWidthCeil(char ch, FontOptions options) {
        return this.get(options).getWidthCeil(ch, options);
    }

    public int getWidthCeil(String str, FontOptions options) {
        return this.get(options).getWidthCeil(str, options);
    }

    public int getHeightCeil(char ch, FontOptions options) {
        return this.get(options).getHeightCeil(ch, options);
    }

    public int getHeightCeil(String str, FontOptions options) {
        return this.get(options).getHeightCeil(str, options);
    }

    public void deleteFonts() {
        this.regularFonts.deleteFonts();
        this.outlineFonts.deleteFonts();
    }

    public static class SizeManager {
        private final LinkedList<GameFont> fonts = new LinkedList();
        private GameFont[] sizeFonts = new GameFont[0];
        private GameFont[] sizePixelFonts = new GameFont[0];

        public GameFont add(GameFont font, boolean hasPixelFont) {
            int nDelta;
            int cDelta;
            int i;
            this.fonts.add(font);
            if (hasPixelFont) {
                if (this.sizePixelFonts.length < font.getFontHeight()) {
                    this.sizePixelFonts = new GameFont[font.getFontHeight()];
                }
                for (i = 0; i < this.sizePixelFonts.length; ++i) {
                    for (GameFont f : this.fonts) {
                        if (this.sizePixelFonts[i] == null) {
                            this.sizePixelFonts[i] = f;
                            continue;
                        }
                        cDelta = Math.abs(this.sizePixelFonts[i].getFontHeight() - i);
                        nDelta = Math.abs(f.getFontHeight() - i);
                        if (nDelta >= cDelta) continue;
                        this.sizePixelFonts[i] = f;
                    }
                }
            }
            if (this.sizeFonts.length < font.getFontHeight()) {
                this.sizeFonts = new GameFont[font.getFontHeight()];
            }
            for (i = 0; i < this.sizeFonts.length; ++i) {
                for (GameFont f : this.fonts) {
                    if (this.sizeFonts[i] == null) {
                        this.sizeFonts[i] = f;
                        continue;
                    }
                    cDelta = Math.abs(this.sizeFonts[i].getFontHeight() - i);
                    nDelta = Math.abs(f.getFontHeight() - i);
                    if (nDelta >= cDelta) continue;
                    this.sizeFonts[i] = f;
                }
            }
            return font;
        }

        public GameFont getFont(int size, boolean pixelFont) {
            if (pixelFont) {
                size = GameMath.limit(size, 0, this.sizePixelFonts.length - 1);
                return this.sizePixelFonts[size];
            }
            size = GameMath.limit(size, 0, this.sizeFonts.length - 1);
            return this.sizeFonts[size];
        }

        public void forEachFont(Consumer<? super GameFont> action) {
            this.fonts.forEach(action);
        }

        protected void deleteFonts() {
            this.fonts.forEach(AbstractGameFont::deleteTextures);
            this.fonts.clear();
            this.sizeFonts = new GameFont[0];
        }
    }
}

