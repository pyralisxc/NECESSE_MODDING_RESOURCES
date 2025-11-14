/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.awt.Color;
import necesse.gfx.GameColor;
import necesse.inventory.item.ItemStatTip;

public abstract class StringItemStatTip
extends ItemStatTip {
    protected String value;
    protected String compareValue;
    protected boolean addParenthesisToChange = true;
    protected Boolean compareValueIsBetter = null;

    public StringItemStatTip(String value) {
        this.value = value;
        this.compareValue = null;
    }

    public StringItemStatTip addParenthesisToChange(boolean value) {
        this.addParenthesisToChange = value;
        return this;
    }

    public StringItemStatTip setCompareValue(String compareValue, Boolean isBetter) {
        this.compareValue = compareValue;
        this.compareValueIsBetter = isBetter;
        return this;
    }

    public StringItemStatTip setCompareValue(String compareValue) {
        return this.setCompareValue(compareValue, null);
    }

    protected String getReplaceValue(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
        String replaceString = this.value;
        if (this.compareValue != null && !this.value.equals(this.compareValue)) {
            if (showDifference) {
                String append = this.compareValue;
                if (this.addParenthesisToChange) {
                    append = "(" + append + ")";
                }
                if (this.compareValueIsBetter == null) {
                    if (neutralColor != null) {
                        append = GameColor.getCustomColorCode(neutralColor) + append + GameColor.NO_COLOR.getColorCode();
                    }
                } else if (this.compareValueIsBetter.booleanValue()) {
                    if (betterColor != null) {
                        append = GameColor.getCustomColorCode(betterColor) + append + GameColor.NO_COLOR.getColorCode();
                    }
                } else if (worseColor != null) {
                    append = GameColor.getCustomColorCode(worseColor) + append + GameColor.NO_COLOR.getColorCode();
                }
                replaceString = replaceString + " " + append;
            } else if (this.compareValueIsBetter == null) {
                if (neutralColor != null) {
                    replaceString = GameColor.getCustomColorCode(neutralColor) + replaceString + GameColor.NO_COLOR.getColorCode();
                }
            } else if (this.compareValueIsBetter.booleanValue()) {
                if (betterColor != null) {
                    replaceString = GameColor.getCustomColorCode(betterColor) + replaceString + GameColor.NO_COLOR.getColorCode();
                }
            } else if (worseColor != null) {
                replaceString = GameColor.getCustomColorCode(worseColor) + replaceString + GameColor.NO_COLOR.getColorCode();
            }
        }
        return replaceString;
    }
}

