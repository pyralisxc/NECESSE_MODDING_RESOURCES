/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameFont;

import necesse.gfx.gameFont.TrueTypeGameFontInfo;

public class TrueTypeGameFontSize {
    public final TrueTypeGameFontInfo info;
    public final int size;
    public final float fontSize;
    public final float lineGap;

    public TrueTypeGameFontSize(TrueTypeGameFontInfo info, int size) {
        this.info = info;
        this.size = size;
        this.fontSize = info.getFontSize(size);
        this.lineGap = info.getFontSize(size);
    }
}

