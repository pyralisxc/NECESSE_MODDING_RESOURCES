/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 */
package aphorea.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class DaggerAttackBuff
extends Buff {
    public DaggerAttackBuff() {
        this.isVisible = false;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        float prevMovementMod = ((Float)buff.owner.buffManager.getModifier(BuffModifiers.ATTACK_MOVEMENT_MOD)).floatValue();
        if (prevMovementMod != 1.0f) {
            buff.setModifier(BuffModifiers.SPEED, (Object)Float.valueOf(1.0f - prevMovementMod));
        }
        buff.setModifier(BuffModifiers.ATTACK_MOVEMENT_MOD, (Object)Float.valueOf(0.0f));
    }
}

