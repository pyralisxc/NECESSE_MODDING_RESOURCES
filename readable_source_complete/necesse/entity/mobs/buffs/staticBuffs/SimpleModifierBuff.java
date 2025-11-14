/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class SimpleModifierBuff
extends Buff {
    protected ModifierValue<?>[] modifiers;

    public SimpleModifierBuff(boolean isVisible, boolean shouldSave, boolean canCancel, boolean isImportant, ModifierValue<?> ... modifiers) {
        this.isVisible = isVisible;
        this.shouldSave = shouldSave;
        this.canCancel = canCancel;
        this.isImportant = isImportant;
        this.modifiers = modifiers;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        for (ModifierValue<?> modifier : this.modifiers) {
            modifier.apply(buff);
        }
    }
}

