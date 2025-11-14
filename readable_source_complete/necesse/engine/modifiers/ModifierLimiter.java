/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import java.awt.Color;
import java.util.function.BiFunction;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.inventory.item.DoubleItemStatTip;

public class ModifierLimiter<T> {
    private final BiFunction<T, T, T> min;
    private final BiFunction<T, T, T> max;
    private final LimitTooltipGetter<T> minTooltip;
    private final LimitTooltipGetter<T> maxTooltip;

    private static int normalFloatMinSign(Float limit, Float current, Float def) {
        if (current == null) {
            return limit.floatValue() > def.floatValue() ? 1 : 0;
        }
        return limit.floatValue() > current.floatValue() ? 1 : 0;
    }

    private static int normalFloatMaxSign(Float limit, Float current, Float def) {
        if (current == null) {
            return limit.floatValue() < def.floatValue() ? -1 : 0;
        }
        return limit.floatValue() < current.floatValue() ? -1 : 0;
    }

    private static int normalIntMinSign(Integer limit, Integer current, Integer def) {
        if (current == null) {
            return limit > def ? 1 : 0;
        }
        return limit > current ? 1 : 0;
    }

    private static int normalIntMaxSign(Integer limit, Integer current, Integer def) {
        if (current == null) {
            return limit < def ? -1 : 0;
        }
        return limit < current ? -1 : 0;
    }

    public static ModifierLimiter<Float> NORMAL_PERC_LIMITER(final String localisationKey) {
        return new ModifierLimiter<Float>(Math::min, Math::max, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue() * 100.0f, 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue() * 100.0f);
            }
            return new ModifierTooltip(ModifierLimiter.normalFloatMinSign(limitValue, currentValue, defaultValue), tip);
        }, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue() * 100.0f, 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue() * 100.0f);
            }
            return new ModifierTooltip(ModifierLimiter.normalFloatMaxSign(limitValue, currentValue, defaultValue), tip);
        });
    }

    @Deprecated
    public static ModifierLimiter<Float> PERC_LIMITER(String localisationKey) {
        return ModifierLimiter.NORMAL_PERC_LIMITER(localisationKey);
    }

    public static ModifierLimiter<Float> INVERSE_PERC_LIMITER(final String localisationKey) {
        return new ModifierLimiter<Float>(Math::min, Math::max, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue() * 100.0f, 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue() * 100.0f, false);
            }
            return new ModifierTooltip(-ModifierLimiter.normalFloatMinSign(limitValue, currentValue, defaultValue), tip);
        }, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue() * 100.0f, 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue() * 100.0f, false);
            }
            return new ModifierTooltip(-ModifierLimiter.normalFloatMaxSign(limitValue, currentValue, defaultValue), tip);
        });
    }

    public static ModifierLimiter<Integer> NORMAL_FLAT_INT_LIMITER(final String localisationKey) {
        return new ModifierLimiter<Integer>(Math::min, Math::max, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.intValue(), 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.intValue());
            }
            return new ModifierTooltip(ModifierLimiter.normalIntMinSign(limitValue, currentValue, defaultValue), tip);
        }, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.intValue(), 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.intValue());
            }
            return new ModifierTooltip(ModifierLimiter.normalIntMaxSign(limitValue, currentValue, defaultValue), tip);
        });
    }

    @Deprecated
    public static ModifierLimiter<Integer> FLAT_INT_LIMITER(String localisationKey) {
        return ModifierLimiter.NORMAL_FLAT_INT_LIMITER(localisationKey);
    }

    public static ModifierLimiter<Integer> INVERSE_FLAT_INT_LIMITER(final String localisationKey) {
        return new ModifierLimiter<Integer>(Math::min, Math::max, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.intValue(), 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.intValue(), false);
            }
            return new ModifierTooltip(-ModifierLimiter.normalIntMinSign(limitValue, currentValue, defaultValue), tip);
        }, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.intValue(), 0){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.intValue(), false);
            }
            return new ModifierTooltip(-ModifierLimiter.normalIntMaxSign(limitValue, currentValue, defaultValue), tip);
        });
    }

    public static ModifierLimiter<Float> NORMAL_FLAT_FLOAT_LIMITER(final String localisationKey) {
        return new ModifierLimiter<Float>(Math::min, Math::max, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue(), 2){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue());
            }
            return new ModifierTooltip(ModifierLimiter.normalFloatMinSign(limitValue, currentValue, defaultValue), tip);
        }, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue(), 2){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue());
            }
            return new ModifierTooltip(ModifierLimiter.normalFloatMaxSign(limitValue, currentValue, defaultValue), tip);
        });
    }

    @Deprecated
    public static ModifierLimiter<Float> FLAT_FLOAT_LIMITER(String localisationKey) {
        return ModifierLimiter.NORMAL_FLAT_FLOAT_LIMITER(localisationKey);
    }

    public static ModifierLimiter<Float> INVERSE_FLAT_FLOAT_LIMITER(final String localisationKey) {
        return new ModifierLimiter<Float>(Math::min, Math::max, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue(), 2){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmin", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue(), false);
            }
            return new ModifierTooltip(-ModifierLimiter.normalFloatMinSign(limitValue, currentValue, defaultValue), tip);
        }, (limitValue, lastLimitValue, currentValue, defaultValue) -> {
            DoubleItemStatTip tip = new DoubleItemStatTip(limitValue.floatValue(), 2){

                @Override
                public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                    return new LocalMessage("buffmodifiers", "limitmax", "modifier", new LocalMessage("buffmodifiers", localisationKey, "mod", this.getReplaceValue(betterColor, worseColor, showDifference)));
                }
            };
            if (lastLimitValue != null) {
                tip.setCompareValue(lastLimitValue.floatValue(), false);
            }
            return new ModifierTooltip(-ModifierLimiter.normalFloatMaxSign(limitValue, currentValue, defaultValue), tip);
        });
    }

    public static ModifierLimiter<Boolean> BOOL_LIMITER(String localizationKey) {
        return new ModifierLimiter<Boolean>((v1, v2) -> v2 != false ? v1 : false, (v1, v2) -> v2 != false || v1 != false, (limitValue, lastLimitValue, currentValue, defaultValue) -> null, (limitValue, lastLimitValue, currentValue, defaultValue) -> null);
    }

    public ModifierLimiter(BiFunction<T, T, T> min, BiFunction<T, T, T> max, LimitTooltipGetter<T> minTooltip, LimitTooltipGetter<T> maxTooltip) {
        this.max = max;
        this.min = min;
        this.maxTooltip = maxTooltip;
        this.minTooltip = minTooltip;
    }

    public T min(T currentValue, T minValue) {
        if (this.min != null) {
            return this.min.apply(currentValue, minValue);
        }
        return currentValue;
    }

    public T max(T currentValue, T maxValue) {
        if (this.max != null) {
            return this.max.apply(currentValue, maxValue);
        }
        return currentValue;
    }

    public ModifierTooltip getMinTooltip(T minValue, T lastMinValue, T currentNonLimitedValue, T defaultValue) {
        if (this.minTooltip == null) {
            return null;
        }
        return this.minTooltip.get(minValue, lastMinValue, currentNonLimitedValue, defaultValue);
    }

    public ModifierTooltip getMaxTooltip(T maxValue, T lastMaxValue, T currentNonLimitedValue, T defaultValue) {
        if (this.maxTooltip == null) {
            return null;
        }
        return this.maxTooltip.get(maxValue, lastMaxValue, currentNonLimitedValue, defaultValue);
    }

    @FunctionalInterface
    public static interface LimitTooltipGetter<T> {
        public ModifierTooltip get(T var1, T var2, T var3, T var4);
    }
}

