/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class BloodstoneRingRegenActiveBuff
extends Buff {
    public BloodstoneRingRegenActiveBuff() {
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.COMBAT_REGEN_FLAT, Float.valueOf(4.0f));
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }
}

