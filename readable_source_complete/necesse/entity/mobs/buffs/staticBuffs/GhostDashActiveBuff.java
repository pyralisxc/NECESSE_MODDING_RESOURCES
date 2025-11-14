/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class GhostDashActiveBuff
extends Buff {
    public GhostDashActiveBuff() {
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.INCOMING_DAMAGE_MOD, Float.valueOf(0.0f));
        buff.setModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.0f));
        buff.setMaxModifier(BuffModifiers.FRICTION, Float.valueOf(0.01f), 1000);
    }
}

