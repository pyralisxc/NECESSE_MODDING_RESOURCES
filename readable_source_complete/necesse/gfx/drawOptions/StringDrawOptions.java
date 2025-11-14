/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions;

import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class StringDrawOptions {
    public final FontOptions fontOptions;
    private String str;

    public StringDrawOptions(FontOptions fontOptions, String str) {
        this.fontOptions = fontOptions;
        this.string(str);
    }

    public StringDrawOptions string(String str) {
        this.str = str;
        return this;
    }

    public StringDrawOptions append(String str) {
        this.str = this.str + str;
        return this;
    }

    public DrawOptions pos(int drawX, int drawY) {
        return () -> FontManager.bit.drawString(drawX, drawY, this.str, this.fontOptions);
    }

    public DrawOptions posCenterX(int drawX, int drawY) {
        return () -> {
            int halfWidth = FontManager.bit.getWidthCeil(this.str, this.fontOptions) / 2;
            FontManager.bit.drawString(drawX - halfWidth, drawY, this.str, this.fontOptions);
        };
    }

    public DrawOptions posCenter(int drawX, int drawY) {
        return () -> {
            int halfWidth = FontManager.bit.getWidthCeil(this.str, this.fontOptions) / 2;
            int halfHeight = FontManager.bit.getHeightCeil(this.str, this.fontOptions) / 2;
            FontManager.bit.drawString(drawX - halfWidth, drawY - halfHeight, this.str, this.fontOptions);
        };
    }

    public void draw(int drawX, int drawY) {
        this.pos(drawX, drawY).draw();
    }

    public void drawCenterX(int drawX, int drawY) {
        this.posCenterX(drawX, drawY).draw();
    }

    public void drawCenter(int drawX, int drawY) {
        this.posCenter(drawX, drawY).draw();
    }
}

