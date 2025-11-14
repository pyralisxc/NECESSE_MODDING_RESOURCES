/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 */
package aphorea.buffs.TrinketsActive;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class SpinelShieldActiveBuff
extends Buff {
    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
        activeBuff.setModifier(BuffModifiers.SPEED, (Object)Float.valueOf(-0.6f));
        activeBuff.setModifier(BuffModifiers.FRICTION, (Object)Float.valueOf(1.0f));
        activeBuff.setModifier(BuffModifiers.INTIMIDATED, (Object)true);
    }
}

