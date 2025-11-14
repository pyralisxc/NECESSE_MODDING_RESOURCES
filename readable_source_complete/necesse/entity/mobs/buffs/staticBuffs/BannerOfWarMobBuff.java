/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class BannerOfWarMobBuff
extends Buff {
    public BannerOfWarMobBuff() {
        this.shouldSave = true;
        this.isVisible = false;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.CHASER_RANGE, Float.valueOf(10.0f));
        buff.setModifier(BuffModifiers.CAN_BREAK_OBJECTS, true);
    }
}

