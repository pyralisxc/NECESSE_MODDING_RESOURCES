/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.GameBackground;
import necesse.gfx.gameTooltips.GameTooltips;

public class BackgroundedGameTooltips
implements GameTooltips {
    public final GameTooltips tooltips;
    public final GameBackground background;

    public BackgroundedGameTooltips(GameTooltips tooltips, GameBackground background) {
        this.tooltips = tooltips;
        this.background = background;
    }

    @Override
    public int getHeight() {
        return this.tooltips.getHeight() + (this.background == null ? 0 : this.background.getContentPadding() * 2);
    }

    @Override
    public int getWidth() {
        return this.tooltips.getWidth() + (this.background == null ? 0 : this.background.getContentPadding() * 2);
    }

    @Override
    public int getDrawXOffset() {
        return this.tooltips.getDrawXOffset();
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        int padding = 0;
        if (this.background != null) {
            padding = this.background.getContentPadding();
            this.background.getDrawOptions(x, y, this.tooltips.getWidth() + padding * 2, this.tooltips.getHeight() + padding * 2).draw();
        }
        this.tooltips.draw(x + padding, y + padding, defaultColor);
        if (this.background != null) {
            this.background.getEdgeDrawOptions(x, y, this.tooltips.getWidth() + padding * 2, this.tooltips.getHeight() + padding * 2).draw();
        }
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

