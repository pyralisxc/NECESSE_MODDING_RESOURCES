/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.GameTooltips;

public class DefaultColoredGameTooltips
implements GameTooltips {
    public final GameTooltips tooltips;
    public final Supplier<Color> defaultColor;

    public DefaultColoredGameTooltips(GameTooltips tooltips, Color defaultColor) {
        this(tooltips, () -> defaultColor);
    }

    public DefaultColoredGameTooltips(GameTooltips tooltips, GameColor defaultColor) {
        this(tooltips, defaultColor.color);
    }

    public DefaultColoredGameTooltips(GameTooltips tooltips, Supplier<Color> defaultColor) {
        this.tooltips = tooltips;
        this.defaultColor = defaultColor;
    }

    @Override
    public int getHeight() {
        return this.tooltips.getHeight();
    }

    @Override
    public int getWidth() {
        return this.tooltips.getWidth();
    }

    @Override
    public int getDrawXOffset() {
        return this.tooltips.getDrawXOffset();
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        this.tooltips.draw(x, y, this.defaultColor);
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

