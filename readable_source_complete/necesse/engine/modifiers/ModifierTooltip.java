/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.inventory.item.ItemStatTip;

public class ModifierTooltip {
    public final int sign;
    public final ItemStatTip tip;

    public ModifierTooltip(int sign, ItemStatTip tip) {
        this.sign = sign;
        this.tip = tip;
    }

    public GameColor getGameColor() {
        if (this.sign < 0) {
            return GameColor.RED;
        }
        if (this.sign == 0) {
            return GameColor.YELLOW;
        }
        return GameColor.GREEN;
    }

    public Color getTooltipColor() {
        return this.getGameColor().color.get();
    }

    public Color getTextColor() {
        if (this.sign < 0) {
            return Settings.UI.errorTextColor;
        }
        if (this.sign == 0) {
            return Settings.UI.warningTextColor;
        }
        return Settings.UI.successTextColor;
    }

    public FairType toFairType(FontOptions fontOptions, boolean showDifference) {
        return this.tip.toFairType(fontOptions, GameColor.RED.color.get(), GameColor.GREEN.color.get(), GameColor.YELLOW.color.get(), showDifference);
    }

    public FairTypeTooltip toTooltip(boolean colored) {
        FontOptions fontOptions = new FontOptions(Settings.tooltipTextSize).outline();
        if (colored) {
            fontOptions = fontOptions.color(this.getTooltipColor());
        }
        return new FairTypeTooltip(this.toFairType(fontOptions, true));
    }
}

