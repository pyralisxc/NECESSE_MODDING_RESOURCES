/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class ListGameTooltips
extends LinkedList<GameTooltips>
implements GameTooltips {
    public int yMargin = 0;
    public int drawOrder = 0;

    public ListGameTooltips() {
    }

    public ListGameTooltips(GameTooltips tooltip) {
        this();
        if (tooltip != null) {
            this.add(tooltip);
        }
    }

    public ListGameTooltips(String tooltip) {
        this();
        if (tooltip != null) {
            this.add(tooltip);
        }
    }

    public ListGameTooltips(GameMessage tooltip) {
        this();
        if (tooltip != null) {
            this.add(tooltip);
        }
    }

    public void add(String string, int maxWidth) {
        this.add(new StringTooltips(string, maxWidth));
    }

    public void add(GameMessage message, int maxWidth) {
        this.add(message.translate(), maxWidth);
    }

    public void add(String string) {
        this.add(new StringTooltips(string));
    }

    public void add(GameMessage message) {
        this.add(message.translate());
    }

    @Override
    public void addFirst(String string) {
        this.addFirst(new StringTooltips(string));
    }

    @Override
    public int getHeight() {
        int height = 0;
        for (GameTooltips tip : this) {
            height += tip.getHeight() + this.yMargin;
        }
        return height - this.yMargin;
    }

    @Override
    public int getWidth() {
        int width = 0;
        for (GameTooltips tip : this) {
            width = Math.max(width, tip.getWidth() + tip.getDrawXOffset());
        }
        return width;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        this.sort(Comparator.comparing(GameTooltips::getDrawOrder, Comparator.reverseOrder()));
        for (GameTooltips tip : this) {
            int xOffset = tip.getDrawXOffset();
            int drawX = Math.max(5, x + xOffset);
            tip.draw(drawX, y, defaultColor);
            y += tip.getHeight() + this.yMargin;
        }
    }

    @Override
    public int getDrawOrder() {
        return this.drawOrder;
    }

    @Override
    public boolean matchesSearch(String search) {
        return this.stream().anyMatch(t -> t.matchesSearch(search));
    }
}

