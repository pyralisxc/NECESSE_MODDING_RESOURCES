/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class PiratePassiveBuff
extends Buff {
    public PiratePassiveBuff() {
        this.shouldSave = false;
        this.isVisible = false;
        this.isPassive = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, Float.valueOf((float)buff.owner.getMaxHealth() / 10.0f));
    }
}

