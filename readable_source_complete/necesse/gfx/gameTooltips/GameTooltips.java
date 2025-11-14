/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;

public interface GameTooltips {
    public int getHeight();

    public int getWidth();

    default public int getDrawXOffset() {
        return 0;
    }

    public void draw(int var1, int var2, Supplier<Color> var3);

    public int getDrawOrder();

    public boolean matchesSearch(String var1);
}

