/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.gameTooltips.GameTooltips;

public class SpacerGameTooltip
implements GameTooltips {
    public final int drawOrder;
    public final int height;

    public SpacerGameTooltip(int drawOrder, int height) {
        this.drawOrder = drawOrder;
        this.height = height;
    }

    public SpacerGameTooltip(int height) {
        this(0, height);
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
    }

    @Override
    public int getDrawOrder() {
        return this.drawOrder;
    }

    @Override
    public boolean matchesSearch(String search) {
        return false;
    }
}

