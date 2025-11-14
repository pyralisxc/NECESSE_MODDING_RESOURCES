/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class PerkFlamelingsCanDieBuff
extends Buff {
    public PerkFlamelingsCanDieBuff() {
        this.isPassive = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }
}

