/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierContainerLimits;
import necesse.engine.modifiers.ModifierTooltip;

public class ModifierValue<T> {
    public final Modifier<T> modifier;
    public final T value;
    public final ModifierContainerLimits<T> limits;

    public ModifierValue(Modifier<T> modifier, T value) {
        this.modifier = modifier;
        this.value = value;
        this.limits = new ModifierContainerLimits();
    }

    public ModifierValue(Modifier<T> modifier) {
        this(modifier, modifier.defaultBuffValue);
    }

    public ModifierValue<T> min(T min, int priority) {
        this.limits.min = min;
        this.limits.hasMin = true;
        this.limits.minPriority = priority;
        return this;
    }

    public ModifierValue<T> min(T min) {
        return this.min(min, 0);
    }

    public ModifierValue<T> max(T max, int priority) {
        this.limits.max = max;
        this.limits.hasMax = true;
        this.limits.maxPriority = priority;
        return this;
    }

    public ModifierValue<T> max(T max) {
        return this.max(max, 0);
    }

    public void apply(ModifierContainer container) {
        container.setModifier(this.modifier, this.value);
        container.addModifierLimits(this.modifier, this.limits);
    }

    public void add(ModifierContainer container) {
        container.addModifier(this.modifier, this.value);
        container.addModifierLimits(this.modifier, this.limits);
    }

    public ModifierTooltip getManagerTooltip() {
        return this.modifier.getTooltip(this.value, this.modifier.defaultBuffManagerValue);
    }

    public ModifierTooltip getTooltip() {
        return this.modifier.getTooltip(this.value, this.modifier.defaultBuffValue);
    }

    public ModifierTooltip getMaxTooltip() {
        if (!this.limits.hasMax) {
            return null;
        }
        return this.modifier.getMaxTooltip(this.limits.max);
    }

    public ModifierTooltip getMinTooltip() {
        if (!this.limits.hasMin) {
            return null;
        }
        return this.modifier.getMinTooltip(this.limits.min);
    }

    public Collection<ModifierTooltip> getAllTooltips() {
        ModifierTooltip minTooltip;
        ModifierTooltip maxTooltip;
        ArrayList<ModifierTooltip> list = new ArrayList<ModifierTooltip>(3);
        ModifierTooltip tooltip = this.getTooltip();
        if (tooltip != null) {
            list.add(tooltip);
        }
        if ((maxTooltip = this.getMaxTooltip()) != null) {
            list.add(maxTooltip);
        }
        if ((minTooltip = this.getMinTooltip()) != null) {
            list.add(minTooltip);
        }
        return list;
    }
}

