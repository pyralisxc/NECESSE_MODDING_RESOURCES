/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;

public class MidArrowComparingTooltips
implements GameTooltips {
    public int height;
    public FontOptions fontOptions = new FontOptions(Settings.tooltipTextSize).outline();

    public MidArrowComparingTooltips(int height) {
        this.height = height;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return 8;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        int charHeight;
        int currentHeight = 0;
        while ((currentHeight += (charHeight = FontManager.bit.getHeightCeil('>', this.fontOptions))) <= this.height) {
            FontManager.bit.drawChar(x - 4, y + currentHeight - charHeight, '>', this.fontOptions);
        }
    }

    @Override
    public int getDrawOrder() {
        return 0;
    }

    @Override
    public boolean matchesSearch(String search) {
        return false;
    }
}

