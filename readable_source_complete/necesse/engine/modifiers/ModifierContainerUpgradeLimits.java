/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import necesse.engine.modifiers.ModifierContainerLimits;
import necesse.inventory.item.upgradeUtils.AbstractUpgradeValue;

public class ModifierContainerUpgradeLimits<T> {
    protected boolean hasMax;
    protected boolean hasMin;
    protected AbstractUpgradeValue<T> min;
    protected AbstractUpgradeValue<T> max;
    protected int minPriority = Integer.MIN_VALUE;
    protected int maxPriority = Integer.MIN_VALUE;

    public boolean hasMax() {
        return this.hasMax;
    }

    public boolean hasMin() {
        return this.hasMin;
    }

    public AbstractUpgradeValue<T> min() {
        return this.min;
    }

    public AbstractUpgradeValue<T> max() {
        return this.max;
    }

    public ModifierContainerLimits<T> toLimits(float upgradeTier) {
        ModifierContainerLimits limits = new ModifierContainerLimits();
        limits.hasMin = this.hasMin;
        limits.min = this.min.getValue(upgradeTier);
        limits.minPriority = this.minPriority;
        limits.hasMax = this.hasMax;
        limits.max = this.max.getValue(upgradeTier);
        limits.maxPriority = this.maxPriority;
        return limits;
    }
}

