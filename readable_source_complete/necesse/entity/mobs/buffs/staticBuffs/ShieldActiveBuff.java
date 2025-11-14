/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ShieldActiveBuff
extends Buff {
    public ShieldActiveBuff() {
        this.shouldSave = false;
        this.isVisible = false;
    }

    @Override
    public boolean shouldNetworkSync() {
        return false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        float minSlow = buff.getGndData().getFloat("minSlow", -1.0f);
        if (minSlow != -1.0f) {
            buff.setMinModifier(BuffModifiers.SLOW, Float.valueOf(minSlow), 1000000);
        }
    }
}

