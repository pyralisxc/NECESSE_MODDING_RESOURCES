/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ShownCooldownBuff
extends Buff {
    protected int maxStacks;
    protected boolean showsFirstStackDurationText;

    public ShownCooldownBuff(int maxStacks, boolean showsFirstStackDurationText) {
        this.maxStacks = maxStacks;
        this.showsFirstStackDurationText = showsFirstStackDurationText;
        this.canCancel = false;
        this.isImportant = true;
    }

    public ShownCooldownBuff() {
        this(1, false);
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return this.maxStacks;
    }

    @Override
    public boolean showsFirstStackDurationText() {
        return this.showsFirstStackDurationText;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }
}

