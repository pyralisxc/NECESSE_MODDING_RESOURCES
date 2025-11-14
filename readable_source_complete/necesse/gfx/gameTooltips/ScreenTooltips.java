/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameBackground;
import necesse.gfx.Renderer;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class ScreenTooltips {
    public static final int margin = 5;
    public final GameTooltips tooltips;
    public final GameBackground background;
    public final TooltipLocation location;

    public ScreenTooltips(GameTooltips tooltips, GameBackground background, TooltipLocation location) {
        this.tooltips = tooltips;
        this.background = background;
        this.location = location;
    }

    public void draw(int x, int y, Supplier<Color> defaultColor) {
        int xOffset = this.tooltips.getDrawXOffset();
        int padding = this.background != null ? this.background.getContentPadding() : 0;
        int drawX = Math.max(5, x + xOffset);
        if (this.background != null) {
            this.background.getDrawOptions(drawX, y, this.tooltips.getWidth() + padding * 2, this.tooltips.getHeight() + padding * 2).draw();
        } else if (Settings.showBasicTooltipBackground) {
            padding = 2;
            Renderer.initQuadDraw(this.tooltips.getWidth() + 4, this.tooltips.getHeight() + 4).color(0.0f, 0.0f, 0.0f, 0.7f).draw(drawX, y);
        }
        this.tooltips.draw(drawX + padding, y + padding, defaultColor);
        if (this.background != null) {
            this.background.getEdgeDrawOptions(drawX, y, this.tooltips.getWidth() + padding * 2, this.tooltips.getHeight() + padding * 2).draw();
        }
    }

    public int getWidth() {
        int padding;
        int n = padding = this.background != null ? this.background.getContentPadding() : 0;
        if (this.background == null && Settings.showBasicTooltipBackground) {
            padding = 2;
        }
        return this.tooltips.getWidth() + padding * 2;
    }

    public int getHeight() {
        int padding;
        int n = padding = this.background != null ? this.background.getContentPadding() : 0;
        if (this.background == null && Settings.showBasicTooltipBackground) {
            padding = 2;
        }
        return this.tooltips.getHeight() + padding * 2;
    }

    static int getDrawX(ScreenTooltips screenTooltips, int x) {
        int width = screenTooltips.getWidth();
        int drawXOffset = screenTooltips.tooltips.getDrawXOffset();
        int xOffset = 0;
        if (x + width + 5 - drawXOffset > WindowManager.getWindow().getHudWidth()) {
            xOffset = WindowManager.getWindow().getHudWidth() - (x + width + 5) + drawXOffset;
        }
        return x + xOffset;
    }

    static int getDrawY(ScreenTooltips screenTooltips, int y) {
        int height = screenTooltips.getHeight();
        if (y - 5 - height < 0) {
            y = height + 5;
        }
        return y - height;
    }

    public static void drawAt(LinkedList<ScreenTooltips> tooltips, TooltipLocation location, Point pos, Supplier<Color> defaultColor) {
        if (pos == null) {
            return;
        }
        LinkedList<ScreenTooltips> draws = new LinkedList<ScreenTooltips>();
        ListIterator li = tooltips.listIterator();
        while (li.hasNext()) {
            ScreenTooltips next = (ScreenTooltips)li.next();
            if (next.location != location) continue;
            draws.add(next);
            li.remove();
        }
        if (!draws.isEmpty()) {
            ScreenTooltips.drawAt(draws, pos.x, pos.y, defaultColor);
        }
    }

    public static void drawAt(LinkedList<ScreenTooltips> tooltips, int x, int y, Supplier<Color> defaultColor) {
        int finalYOffset = 0;
        for (int i = 1; i < tooltips.size(); ++i) {
            finalYOffset += tooltips.get(i).getHeight() + 5;
        }
        int drawX = x + 5;
        for (ScreenTooltips t : tooltips) {
            drawX = Math.min(drawX, ScreenTooltips.getDrawX(t, x));
        }
        if (drawX < 5) {
            drawX = 5;
        }
        ScreenTooltips first = tooltips.size() > 0 ? tooltips.get(0) : null;
        int startY = first != null ? ScreenTooltips.getDrawY(first, y - finalYOffset) : 0;
        int yOffset = 0;
        for (ScreenTooltips t : tooltips) {
            int drawY = startY + yOffset;
            t.draw(drawX, drawY, defaultColor);
            yOffset += t.getHeight() + 5;
        }
    }
}

