/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierContainerUpgradeLimits;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.inventory.item.upgradeUtils.AbstractUpgradeValue;

public class ModifierUpgradeValue<T> {
    public final Modifier<T> modifier;
    public final AbstractUpgradeValue<T> value;
    public final ModifierContainerUpgradeLimits<T> limits;

    public ModifierUpgradeValue(Modifier<T> modifier, AbstractUpgradeValue<T> value) {
        this.modifier = modifier;
        this.value = value;
        this.limits = new ModifierContainerUpgradeLimits();
    }

    public ModifierUpgradeValue<T> min(AbstractUpgradeValue<T> min, int priority) {
        this.limits.min = min;
        this.limits.hasMin = true;
        this.limits.minPriority = priority;
        return this;
    }

    public ModifierUpgradeValue<T> min(AbstractUpgradeValue<T> min) {
        return this.min((T)min, 0);
    }

    public ModifierUpgradeValue<T> min(final T min, int priority) {
        return this.min((T)new AbstractUpgradeValue<T>(){

            @Override
            public T getValue(float tier) {
                return min;
            }
        }, priority);
    }

    public ModifierUpgradeValue<T> min(T min) {
        return this.min(min, 0);
    }

    public ModifierUpgradeValue<T> max(AbstractUpgradeValue<T> max, int priority) {
        this.limits.max = max;
        this.limits.hasMax = true;
        this.limits.maxPriority = priority;
        return this;
    }

    public ModifierUpgradeValue<T> max(AbstractUpgradeValue<T> max) {
        return this.max((T)max, 0);
    }

    public ModifierUpgradeValue<T> max(final T max, int priority) {
        return this.max((T)new AbstractUpgradeValue<T>(){

            @Override
            public T getValue(float tier) {
                return max;
            }
        }, priority);
    }

    public ModifierUpgradeValue<T> max(T max) {
        return this.max(max, 0);
    }

    public void apply(ModifierContainer container, float upgradeTier) {
        container.setModifier(this.modifier, this.value.getValue(upgradeTier));
        container.addModifierLimits(this.modifier, this.limits.toLimits(upgradeTier));
    }

    public void add(ModifierContainer container, float upgradeTier) {
        container.addModifier(this.modifier, this.value.getValue(upgradeTier));
        container.addModifierLimits(this.modifier, this.limits.toLimits(upgradeTier));
    }

    public ModifierTooltip getManagerTooltip(float upgradeTier) {
        return this.modifier.getTooltip(this.value.getValue(upgradeTier), this.modifier.defaultBuffManagerValue);
    }

    public ModifierTooltip getTooltip(float upgradeTier) {
        return this.modifier.getTooltip(this.value.getValue(upgradeTier), this.modifier.defaultBuffValue);
    }

    public ModifierTooltip getMaxTooltip(float upgradeTier) {
        if (!this.limits.hasMax) {
            return null;
        }
        return this.modifier.getMaxTooltip(this.limits.max.getValue(upgradeTier));
    }

    public ModifierTooltip getMinTooltip(float upgradeTier) {
        if (!this.limits.hasMin) {
            return null;
        }
        return this.modifier.getMinTooltip(this.limits.min.getValue(upgradeTier));
    }

    public Collection<ModifierTooltip> getAllTooltips(float upgradeTier) {
        ModifierTooltip minTooltip;
        ModifierTooltip maxTooltip;
        ArrayList<ModifierTooltip> list = new ArrayList<ModifierTooltip>(3);
        ModifierTooltip tooltip = this.getTooltip(upgradeTier);
        if (tooltip != null) {
            list.add(tooltip);
        }
        if ((maxTooltip = this.getMaxTooltip(upgradeTier)) != null) {
            list.add(maxTooltip);
        }
        if ((minTooltip = this.getMinTooltip(upgradeTier)) != null) {
            list.add(minTooltip);
        }
        return list;
    }
}

