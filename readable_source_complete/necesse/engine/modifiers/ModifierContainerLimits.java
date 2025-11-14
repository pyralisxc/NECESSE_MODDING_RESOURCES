/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import necesse.engine.modifiers.Modifier;

public class ModifierContainerLimits<T> {
    protected boolean hasMax;
    protected boolean hasMin;
    protected T min;
    protected T max;
    protected int minPriority = Integer.MIN_VALUE;
    protected int maxPriority = Integer.MIN_VALUE;

    public boolean hasMax() {
        return this.hasMax;
    }

    public boolean hasMin() {
        return this.hasMin;
    }

    public T min() {
        return this.min;
    }

    public T max() {
        return this.max;
    }

    public void combine(Modifier<T> modifier, ModifierContainerLimits<T> other) {
        if (other.hasMin()) {
            this.min = this.hasMin() ? modifier.max(this.min(), other.min()) : other.min();
            this.minPriority = Math.max(this.minPriority, other.minPriority);
            this.hasMin = true;
        }
        if (other.hasMax()) {
            this.max = this.hasMax() ? modifier.min(this.max(), other.max()) : other.max();
            this.maxPriority = Math.max(this.maxPriority, other.maxPriority);
            this.hasMax = true;
        }
        if (this.hasMin() && this.hasMax()) {
            if (this.minPriority > this.maxPriority) {
                this.max = modifier.max(this.max(), this.min());
            } else {
                this.min = modifier.min(this.max(), this.min());
            }
        }
    }

    public T applyModifierLimits(Modifier<T> modifier, T currentValue) {
        T out = currentValue;
        if (this.hasMin()) {
            out = modifier.max(out, this.min());
        }
        if (this.hasMax()) {
            out = modifier.min(out, this.max());
        }
        return modifier.finalLimit(out);
    }
}

