/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import java.awt.Color;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierAppendFunction;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.util.GameMath;
import necesse.gfx.GameColor;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;

public class Modifier<T> {
    public static final ModifierAppendFunction<Float> FLOAT_ADD_APPEND = (oldValue, newValue, count) -> Float.valueOf(oldValue.floatValue() + newValue.floatValue() * (float)count);
    public static final ModifierAppendFunction<Integer> INT_ADD_APPEND = (oldValue, newValue, count) -> oldValue + newValue * count;
    public static final ModifierAppendFunction<Float> FLOAT_MULT_APPEND = (oldValue, newValue, count) -> Float.valueOf(oldValue.floatValue() * (count == 1 ? newValue.floatValue() : (float)Math.pow(newValue.floatValue(), count)));
    public static final ModifierAppendFunction<Boolean> OR_APPEND = (oldValue, newValue, count) -> oldValue != false || newValue != false;
    public static final BiFunction<Float, Float, Float> FLOAT_ADD_APPEND_DEPRICATED = (oldValue, newValue) -> Float.valueOf(oldValue.floatValue() + newValue.floatValue());
    public static final BiFunction<Integer, Integer, Integer> INT_ADD_APPEND_DEPRICATED = (oldValue, newValue) -> oldValue + newValue;
    public static final BiFunction<Float, Float, Float> FLOAT_MULT_APPEND_DEPRICATED = (oldValue, newValue) -> Float.valueOf(oldValue.floatValue() * newValue.floatValue());
    public static final BiFunction<Boolean, Boolean, Boolean> OR_APPEND_DEPRICATED = (oldValue, newValue) -> oldValue != false || newValue != false;
    public final int index;
    public final ModifierList list;
    public final String stringID;
    public final T defaultBuffManagerValue;
    public final T defaultBuffValue;
    private ModifierAppendFunction<T> appendFunc;
    private Function<T, T> finalLimiter;
    private ModifierTooltipGetter<T> tooltipFunc;
    public final ModifierLimiter<T> limiter;

    public static ModifierTooltipGetter<Float> NORMAL_PERC_PARSER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            float v = currentValue.floatValue() - defaultValue.floatValue();
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", Math.round(v * 100.0f), 0);
            tip.setValueToString(value -> {
                if (value > 0.0) {
                    return "+" + GameMath.removeDecimalIfZero(value);
                }
                return GameMath.removeDecimalIfZero(value);
            });
            if (lastValue != null) {
                tip.setCompareValue(Math.round((lastValue.floatValue() - defaultValue.floatValue()) * 100.0f));
            }
            return new ModifierTooltip((int)Math.signum(v), tip);
        };
    }

    public static ModifierTooltipGetter<Float> INVERSE_PERC_PARSER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            float v = currentValue.floatValue() - defaultValue.floatValue();
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", Math.round(v * 100.0f), 0);
            tip.setValueToString(value -> {
                if (value > 0.0) {
                    return "+" + GameMath.removeDecimalIfZero(value);
                }
                return GameMath.removeDecimalIfZero(value);
            });
            if (lastValue != null) {
                tip.setCompareValue(Math.round((lastValue.floatValue() - defaultValue.floatValue()) * 100.0f), false);
            }
            return new ModifierTooltip((int)Math.signum(-v), tip);
        };
    }

    public static ModifierTooltipGetter<Float> NORMAL_FLAT_FLOAT_PARSER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            float v = currentValue.floatValue() - defaultValue.floatValue();
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", v, 2);
            tip.setValueToString(value -> {
                if (value > 0.0) {
                    return "+" + GameMath.removeDecimalIfZero(value);
                }
                return GameMath.removeDecimalIfZero(value);
            });
            if (lastValue != null) {
                tip.setCompareValue(lastValue.floatValue() - defaultValue.floatValue());
            }
            return new ModifierTooltip((int)Math.signum(v), tip);
        };
    }

    public static ModifierTooltipGetter<Float> INVERSE_FLAT_FLOAT_PARSER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            float v = currentValue.floatValue() - defaultValue.floatValue();
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", v, 2);
            tip.setValueToString(value -> {
                if (value > 0.0) {
                    return "+" + GameMath.removeDecimalIfZero(value);
                }
                return GameMath.removeDecimalIfZero(value);
            });
            if (lastValue != null) {
                tip.setCompareValue(lastValue.floatValue() - defaultValue.floatValue(), false);
            }
            return new ModifierTooltip((int)Math.signum(-v), tip);
        };
    }

    public static ModifierTooltipGetter<Integer> NORMAL_FLAT_INT_PARSER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            int v = currentValue - defaultValue;
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", v, 0);
            tip.setValueToString(value -> {
                if (value > 0.0) {
                    return "+" + GameMath.removeDecimalIfZero(value);
                }
                return GameMath.removeDecimalIfZero(value);
            });
            if (lastValue != null) {
                tip.setCompareValue(lastValue - defaultValue);
            }
            return new ModifierTooltip((int)Math.signum(v), tip);
        };
    }

    public static ModifierTooltipGetter<Integer> INVERSE_FLAT_INT_PARSER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            int v = currentValue - defaultValue;
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", v, 0);
            tip.setValueToString(value -> {
                if (value > 0.0) {
                    return "+" + GameMath.removeDecimalIfZero(value);
                }
                return GameMath.removeDecimalIfZero(value);
            });
            if (lastValue != null) {
                tip.setCompareValue(lastValue - defaultValue, false);
            }
            return new ModifierTooltip((int)Math.signum(-v), tip);
        };
    }

    public static ModifierTooltipGetter<Boolean> BOOL_PARSER(final String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            if (currentValue.booleanValue()) {
                return new ModifierTooltip(1, new ItemStatTip((Boolean)lastValue){
                    final /* synthetic */ Boolean val$lastValue;
                    {
                        this.val$lastValue = bl;
                    }

                    @Override
                    public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                        LocalMessage message = new LocalMessage("buffmodifiers", localisationKey);
                        if (this.val$lastValue != null && !this.val$lastValue.booleanValue()) {
                            return new GameMessageBuilder().append(GameColor.getCustomColorCode(betterColor)).append(message);
                        }
                        return message;
                    }
                });
            }
            return null;
        };
    }

    public static ModifierTooltipGetter<Boolean> INVERSE_BOOL_PARSER(final String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            if (currentValue.booleanValue()) {
                return new ModifierTooltip(-1, new ItemStatTip((Boolean)lastValue){
                    final /* synthetic */ Boolean val$lastValue;
                    {
                        this.val$lastValue = bl;
                    }

                    @Override
                    public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                        LocalMessage message = new LocalMessage("buffmodifiers", localisationKey);
                        if (this.val$lastValue != null && this.val$lastValue.booleanValue()) {
                            return new GameMessageBuilder().append(GameColor.getCustomColorCode(worseColor)).append(message);
                        }
                        return message;
                    }
                });
            }
            return null;
        };
    }

    public static ModifierTooltipGetter<Boolean> NEUTRAL_BOOL_PARSER(final String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            if (currentValue.booleanValue()) {
                return new ModifierTooltip(0, new ItemStatTip(){

                    @Override
                    public GameMessage toMessage(Color betterColor, Color worseColor, Color neutralColor, boolean showDifference) {
                        return new LocalMessage("buffmodifiers", localisationKey);
                    }
                });
            }
            return null;
        };
    }

    public static ModifierTooltipGetter<Float> BAD_PERCENT_MODIFIER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            float v = currentValue.floatValue() - defaultValue.floatValue();
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", Math.round(v * 100.0f), 0);
            if (lastValue != null) {
                tip.setCompareValue(Math.round((lastValue.floatValue() - defaultValue.floatValue()) * 100.0f), false);
            }
            return new ModifierTooltip(-1, tip);
        };
    }

    public static ModifierTooltipGetter<Float> LESS_GOOD_PERCENT_MODIFIER(String localisationKey) {
        return (currentValue, lastValue, defaultValue) -> {
            LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("buffmodifiers", localisationKey, "mod", Math.round(currentValue.floatValue() * 100.0f), 0);
            if (lastValue != null) {
                tip.setCompareValue(Math.round(lastValue.floatValue() * 100.0f), false);
            }
            return new ModifierTooltip(currentValue.floatValue() < 1.0f ? 1 : -1, tip);
        };
    }

    public Modifier(ModifierList list, String stringID, T defaultBuffManagerValue, T defaultBuffValue, ModifierAppendFunction<T> appendFunc, Function<T, T> finalLimiter, ModifierTooltipGetter<T> tooltipFunc, ModifierLimiter<T> limiter) {
        Objects.requireNonNull(appendFunc);
        this.stringID = stringID;
        this.list = list;
        this.defaultBuffManagerValue = defaultBuffManagerValue;
        this.defaultBuffValue = defaultBuffValue;
        this.appendFunc = appendFunc;
        this.finalLimiter = finalLimiter;
        this.tooltipFunc = tooltipFunc;
        this.limiter = limiter;
        this.index = list.addModifier(this);
    }

    @Deprecated
    public Modifier(ModifierList list, T defaultBuffManagerValue, T defaultBuffValue, ModifierAppendFunction<T> appendFunc, Function<T, T> finalLimiter, ModifierTooltipGetter<T> tooltipFunc, ModifierLimiter<T> limiter) {
        this(list, null, defaultBuffManagerValue, defaultBuffValue, appendFunc, finalLimiter, tooltipFunc, limiter);
    }

    public Modifier(ModifierList list, String stringID, T defaultBuffManagerValue, T defaultBuffValue, ModifierAppendFunction<T> appendFunc, ModifierTooltipGetter<T> tooltipFunc, ModifierLimiter<T> limiter) {
        this(list, stringID, defaultBuffManagerValue, defaultBuffValue, appendFunc, null, tooltipFunc, limiter);
    }

    @Deprecated
    public Modifier(ModifierList list, T defaultBuffManagerValue, T defaultBuffValue, ModifierAppendFunction<T> appendFunc, ModifierTooltipGetter<T> tooltipFunc, ModifierLimiter<T> limiter) {
        this(list, null, defaultBuffManagerValue, defaultBuffValue, appendFunc, null, tooltipFunc, limiter);
    }

    @Deprecated
    public Modifier(ModifierList list, T defaultBuffManagerValue, T defaultBuffValue, BiFunction<T, T, T> appendFunc, Function<T, T> finalLimiter, ModifierTooltipGetter<T> tooltipFunc, ModifierLimiter<T> limiter) {
        this(list, null, defaultBuffManagerValue, defaultBuffValue, (currentValue, appendValue, count) -> appendFunc.apply(currentValue, appendValue), finalLimiter, tooltipFunc, limiter);
    }

    @Deprecated
    public Modifier(ModifierList list, T defaultBuffManagerValue, T defaultBuffValue, BiFunction<T, T, T> appendFunc, ModifierTooltipGetter<T> tooltipFunc, ModifierLimiter<T> limiter) {
        this(list, null, defaultBuffManagerValue, defaultBuffValue, (currentValue, appendValue, count) -> appendFunc.apply(currentValue, appendValue), null, tooltipFunc, limiter);
    }

    public ModifierTooltip getTooltip(T value, T lastValue, T defaultValue) {
        if (Objects.equals(value, defaultValue)) {
            return null;
        }
        if (this.tooltipFunc == null) {
            return null;
        }
        return this.tooltipFunc.get(value, lastValue, defaultValue);
    }

    public ModifierTooltip getTooltip(T value, T defaultValue) {
        return this.getTooltip(value, null, defaultValue);
    }

    public T appendManager(T currentValue, T appendValue) {
        return this.appendManager(currentValue, appendValue, 1);
    }

    public T appendManager(T currentValue, T appendValue, int count) {
        if (count <= 0) {
            return currentValue;
        }
        return this.appendFunc.apply(currentValue, appendValue, count);
    }

    public T finalLimit(T currentValue) {
        if (this.finalLimiter != null) {
            return this.finalLimiter.apply(currentValue);
        }
        return currentValue;
    }

    public T max(T currentValue, T maxValue) {
        if (this.limiter != null) {
            return this.limiter.max(currentValue, maxValue);
        }
        return currentValue;
    }

    public T min(T currentValue, T minValue) {
        if (this.limiter != null) {
            return this.limiter.min(currentValue, minValue);
        }
        return currentValue;
    }

    public ModifierTooltip getMinTooltip(T minValue, T lastMinValue, T currentNonLimitedValue) {
        if (this.limiter != null) {
            return this.limiter.getMinTooltip(minValue, lastMinValue, currentNonLimitedValue, this.defaultBuffManagerValue);
        }
        return null;
    }

    public ModifierTooltip getMinTooltip(T minValue, T currentNonLimitedValue) {
        return this.getMinTooltip(minValue, null, currentNonLimitedValue);
    }

    public ModifierTooltip getMinTooltip(T minValue) {
        return this.getMinTooltip(minValue, null);
    }

    public ModifierTooltip getMaxTooltip(T maxValue, T lastMaxValue, T currentNonLimitedValue) {
        if (this.limiter != null) {
            return this.limiter.getMaxTooltip(maxValue, lastMaxValue, currentNonLimitedValue, this.defaultBuffManagerValue);
        }
        return null;
    }

    public ModifierTooltip getMaxTooltip(T maxValue, T currentNonLimitedValue) {
        return this.getMaxTooltip(maxValue, null, currentNonLimitedValue);
    }

    public ModifierTooltip getMaxTooltip(T maxValue) {
        return this.getMaxTooltip(maxValue, null);
    }

    @FunctionalInterface
    public static interface ModifierTooltipGetter<V> {
        public ModifierTooltip get(V var1, V var2, V var3);
    }
}

