/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import necesse.engine.registries.IDData;
import necesse.engine.util.GameRandom;
import necesse.gfx.ThemeColorRegistry;

public class ThemeColorRange {
    public final IDData idData = new IDData();
    protected Color[] colors;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public ThemeColorRange(Color ... colors) {
        this.colors = colors;
    }

    public ThemeColorRange(ThemeColorRange copyColors, Color ... additionalColors) {
        this.colors = new Color[copyColors.count() + additionalColors.length];
        for (int i = 0; i < this.count(); ++i) {
            this.colors[i] = i < copyColors.count() ? copyColors.colors[i] : additionalColors[i - copyColors.count()];
        }
    }

    public ThemeColorRange combine(ThemeColorRange ... colorRanges) {
        return new ThemeColorRange(ThemeColorRegistry.combine(colorRanges), this.colors);
    }

    public Color getRandomColor() {
        return GameRandom.globalRandom.getOneOf(this.colors);
    }

    public Color get(int index) {
        try {
            return this.colors[index];
        }
        catch (IndexOutOfBoundsException e) {
            System.out.println(ThemeColorRange.class.getSimpleName() + ": Caught ThemeColorRange.get(index). | " + e.getMessage());
            return Color.BLACK;
        }
    }

    public int count() {
        return this.colors.length;
    }
}

