/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modifiers;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierContainerLimits;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.util.HashMapArrayList;

public abstract class ModifierManager<M extends ModifierContainer> {
    private final ModifierList list;
    private Object[] nonLimitedModifiers;
    private Object[] limitedModifiers;
    private ModifierContainerLimits[] limits;
    private HashSet<Integer> queryableModifiers;
    private HashMapArrayList<Modifier<?>, M> queryModifiers;

    public ModifierManager(ModifierList list) {
        this.list = list;
        this.nonLimitedModifiers = new Object[list.getModifierCount()];
        this.limitedModifiers = new Object[list.getModifierCount()];
        this.limits = new ModifierContainerLimits[list.getModifierCount()];
        for (int i = 0; i < this.limits.length; ++i) {
            this.limits[i] = new ModifierContainerLimits();
        }
        this.queryableModifiers = new HashSet();
        this.queryModifiers = new HashMapArrayList();
    }

    protected void makeQueryable(Modifier modifier) {
        this.queryableModifiers.add(modifier.index);
    }

    protected Collection<M> queryContainers(Modifier modifier) {
        return (Collection)this.queryModifiers.get(modifier);
    }

    protected void updateModifiers() {
        for (int i = 0; i < this.nonLimitedModifiers.length; ++i) {
            this.nonLimitedModifiers[i] = this.list.getModifier((int)i).defaultBuffManagerValue;
            this.limits[i] = new ModifierContainerLimits();
        }
        HashMapArrayList<Modifier, ModifierContainer> newQueryModifiers = new HashMapArrayList<Modifier, ModifierContainer>();
        for (ModifierContainer mc : this.getModifierContainers()) {
            Modifier modifier;
            mc.onUpdate();
            for (Integer i : mc.getNonDefaultModifierIndexes()) {
                modifier = this.list.getModifier(i);
                this.nonLimitedModifiers[i.intValue()] = mc.applyModifierUnlimited(modifier, this.nonLimitedModifiers[i]);
                if (!this.queryableModifiers.contains(i)) continue;
                newQueryModifiers.add(modifier, mc);
            }
            for (Integer i : mc.getNonDefaultLimitIndexes()) {
                modifier = this.list.getModifier(i);
                this.limits[i].combine(modifier, mc.getLimits(modifier));
                if (!this.queryableModifiers.contains(i)) continue;
                newQueryModifiers.add(modifier, mc);
            }
        }
        ((Stream)this.getDefaultModifiers().sequential()).map(e -> e).forEach(modifierValue -> {
            this.nonLimitedModifiers[modifierValue.modifier.index] = modifierValue.modifier.appendManager(this.nonLimitedModifiers[modifierValue.modifier.index], modifierValue.value);
            this.limits[modifierValue.modifier.index].combine(modifierValue.modifier, modifierValue.limits);
        });
        for (int i = 0; i < this.limitedModifiers.length; ++i) {
            this.limitedModifiers[i] = this.limits[i].applyModifierLimits(this.list.getModifier(i), this.nonLimitedModifiers[i]);
        }
        this.queryModifiers = newQueryModifiers;
    }

    protected abstract Iterable<? extends M> getModifierContainers();

    public <T> T getNonLimitedModifier(Modifier<T> modifier) {
        if (modifier.list != this.list) {
            throw new IllegalArgumentException("Modifier is not part of list.");
        }
        return (T)this.nonLimitedModifiers[modifier.index];
    }

    public <T> T getModifier(Modifier<T> modifier) {
        if (modifier.list != this.list) {
            throw new IllegalArgumentException("Modifier is not part of list.");
        }
        return (T)this.limitedModifiers[modifier.index];
    }

    public <T> ModifierContainerLimits<T> getLimits(Modifier<T> modifier) {
        return this.limits[modifier.index];
    }

    @SafeVarargs
    public final <T> T applyModifiers(Modifier<T> modifier, T value, ModifierValue<T> ... modifiers) {
        T out = value;
        for (ModifierValue<T> modValue : modifiers) {
            out = modifier.appendManager(out, modValue.value);
        }
        out = this.limits[modifier.index].applyModifierLimits(modifier, out);
        for (ModifierValue<T> modValue : modifiers) {
            out = modValue.limits.applyModifierLimits(modifier, out);
        }
        return out;
    }

    @SafeVarargs
    public final <T> T getAndApplyModifiers(Modifier<T> modifier, ModifierValue<T> ... modifiers) {
        return this.applyModifiers(modifier, this.getModifier(modifier), modifiers);
    }

    @SafeVarargs
    public final <T> T getAndApplyModifiers(Modifier<T> modifier, T ... values) {
        ModifierValue[] modifiers = new ModifierValue[values.length];
        for (int i = 0; i < values.length; ++i) {
            modifiers[i] = new ModifierValue<T>(modifier, values[i]);
        }
        return this.applyModifiers(modifier, this.getModifier(modifier), modifiers);
    }

    public LinkedList<ModifierTooltip> getModifierTooltips() {
        return this.getModifierTooltips(null);
    }

    public LinkedList<ModifierTooltip> getModifierTooltips(ModifierManager<M> lastValues) {
        LinkedList<ModifierTooltip> out = new LinkedList<ModifierTooltip>();
        for (Modifier modifier : this.list) {
            ModifierTooltip tooltip;
            ModifierTooltip maxTooltip;
            ModifierTooltip minTooltip;
            ModifierContainerLimits limiter = this.limits[modifier.index];
            if (limiter.hasMin() && (minTooltip = modifier.getMinTooltip(limiter.min(), lastValues != null ? (Object)lastValues.limits[modifier.index].min() : null, this.getNonLimitedModifier(modifier))) != null) {
                out.add(minTooltip);
            }
            if (limiter.hasMax() && (maxTooltip = modifier.getMaxTooltip(limiter.max(), lastValues != null ? (Object)lastValues.limits[modifier.index].max() : null, this.getNonLimitedModifier(modifier))) != null) {
                out.add(maxTooltip);
            }
            if ((tooltip = modifier.getTooltip(this.getModifier(modifier), lastValues != null ? (Object)lastValues.getModifier(modifier) : null, modifier.defaultBuffManagerValue)) == null) continue;
            out.add(tooltip);
        }
        return out;
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.empty();
    }
}

