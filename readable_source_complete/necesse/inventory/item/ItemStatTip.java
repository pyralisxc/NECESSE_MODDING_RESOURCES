/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltips;

public abstract class ItemStatTip {
    public abstract GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4);

    public String toString(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
        return this.toMessage(betterColor, worseColor, neutralColor, showDifference).translate();
    }

    public FairType toFairType(FontOptions fontOptions, Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
        FairType fairType = new FairType();
        fairType.append(fontOptions, this.toString(betterColor, worseColor, neutralColor, showDifference));
        fairType.applyParsers(TypeParsers.GAME_COLOR);
        return fairType;
    }

    public GameTooltips toTooltip(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
        return new FairTypeTooltip(this.toFairType(new FontOptions(Settings.tooltipTextSize).outline(), betterColor, worseColor, neutralColor, showDifference));
    }
}

