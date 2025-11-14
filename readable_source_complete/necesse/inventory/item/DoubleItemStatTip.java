/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.awt.Color;
import java.util.function.DoubleFunction;
import necesse.engine.util.GameMath;
import necesse.gfx.GameColor;
import necesse.inventory.item.ItemStatTip;

public abstract class DoubleItemStatTip
extends ItemStatTip {
    protected double value;
    protected double compareValue;
    protected int decimals;
    protected DoubleFunction<String> valueToString = GameMath::removeDecimalIfZero;
    protected DoubleFunction<String> deltaToString = delta -> {
        if (delta > 0.0) {
            return "+" + GameMath.removeDecimalIfZero(delta);
        }
        return GameMath.removeDecimalIfZero(delta);
    };
    protected boolean addParenthesisToChange = true;
    protected boolean higherIsBetter = true;

    public DoubleItemStatTip(double value, int decimals) {
        this.value = value;
        this.decimals = decimals;
        this.compareValue = Double.NaN;
    }

    public DoubleItemStatTip setToString(DoubleFunction<String> valueToString) {
        this.valueToString = valueToString;
        this.deltaToString = delta -> {
            if (delta > 0.0) {
                return "+" + (String)valueToString.apply(delta);
            }
            return (String)valueToString.apply(delta);
        };
        return this;
    }

    public DoubleItemStatTip setValueToString(DoubleFunction<String> valueToString) {
        this.valueToString = valueToString;
        return this;
    }

    public DoubleItemStatTip setDeltaToString(DoubleFunction<String> valueToString) {
        this.deltaToString = valueToString;
        return this;
    }

    public DoubleItemStatTip addParenthesisToChange(boolean value) {
        this.addParenthesisToChange = value;
        return this;
    }

    public DoubleItemStatTip setCompareValue(double compareValue, boolean higherIsBetter) {
        this.compareValue = compareValue;
        this.higherIsBetter = higherIsBetter;
        return this;
    }

    public DoubleItemStatTip setCompareValue(double compareValue) {
        return this.setCompareValue(compareValue, true);
    }

    protected String getReplaceValue(Color betterColor, Color worseColor, boolean showDifference) {
        double value = this.decimals <= 0 ? (double)Math.round(this.value) : GameMath.toDecimals(this.value, this.decimals);
        String replaceString = this.valueToString.apply(value);
        if (!Double.isNaN(this.compareValue) && this.value != this.compareValue) {
            boolean isBetter;
            double delta;
            double d = delta = this.decimals <= 0 ? (double)Math.round(this.value - this.compareValue) : GameMath.toDecimals(this.value - this.compareValue, this.decimals);
            boolean bl = this.higherIsBetter ? delta > 0.0 : (isBetter = delta < 0.0);
            if (showDifference) {
                String append = this.deltaToString.apply(delta);
                if (this.addParenthesisToChange) {
                    append = "(" + append + ")";
                }
                if (isBetter) {
                    if (betterColor != null) {
                        append = GameColor.getCustomColorCode(betterColor) + append + GameColor.NO_COLOR.getColorCode();
                    }
                } else if (worseColor != null) {
                    append = GameColor.getCustomColorCode(worseColor) + append + GameColor.NO_COLOR.getColorCode();
                }
                replaceString = replaceString + " " + append;
            } else if (isBetter) {
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

