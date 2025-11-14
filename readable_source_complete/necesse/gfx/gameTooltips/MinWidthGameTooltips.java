/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.gameTooltips.GameTooltips;

public class MinWidthGameTooltips
implements GameTooltips {
    private GameTooltips tooltips;
    private int minWidth;

    public MinWidthGameTooltips(GameTooltips tooltips, int minWidth) {
        this.tooltips = tooltips;
        this.minWidth = minWidth;
    }

    @Override
    public int getHeight() {
        return this.tooltips.getHeight();
    }

    @Override
    public int getWidth() {
        return Math.max(this.tooltips.getWidth(), this.minWidth);
    }

    @Override
    public int getDrawXOffset() {
        return this.tooltips.getDrawXOffset();
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        this.tooltips.draw(x, y, defaultColor);
    }

    @Override
    public int getDrawOrder() {
        return this.tooltips.getDrawOrder();
    }

    @Override
    public boolean matchesSearch(String search) {
        return this.tooltips.matchesSearch(search);
    }
}

