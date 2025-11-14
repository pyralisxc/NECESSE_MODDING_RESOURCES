/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class InvulnerableActiveBuff
extends Buff {
    public InvulnerableActiveBuff() {
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event) {
        super.onBeforeHit(buff, event);
        event.prevent();
        event.showDamageTip = false;
        event.playHitSound = false;
    }
}

