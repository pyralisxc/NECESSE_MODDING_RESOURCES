/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import necesse.gfx.gameTooltips.GameTooltips;

public class ComparingGameTooltips
implements GameTooltips {
    public int padding = 5;
    protected ArrayList<GameTooltips> tooltips;
    protected int height;
    protected int width;

    public ComparingGameTooltips(Collection<GameTooltips> tooltips) {
        this.tooltips = new ArrayList(tooltips.size());
        for (GameTooltips tooltip : tooltips) {
            this.addTooltips(tooltip);
        }
    }

    public ComparingGameTooltips(GameTooltips ... tooltips) {
        this(Arrays.asList(tooltips));
    }

    public void addTooltips(GameTooltips tooltips) {
        this.height = Math.max(tooltips.getHeight(), this.height);
        this.width += tooltips.getWidth();
        this.tooltips.add(tooltips);
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width + this.padding * Math.max(0, this.tooltips.size() - 1);
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        for (GameTooltips tooltip : this.tooltips) {
            tooltip.draw(x, y, defaultColor);
            x += tooltip.getWidth() + this.padding;
        }
    }

    @Override
    public int getDrawOrder() {
        return 0;
    }

    @Override
    public boolean matchesSearch(String search) {
        return this.tooltips.stream().anyMatch(t -> t.matchesSearch(search));
    }
}

