/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.InvulnerableActiveBuff
 */
package aphorea.buffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.InvulnerableActiveBuff;

public class StopBuff
extends InvulnerableActiveBuff {
    public StopBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SLOW, (Object)Float.valueOf(10.0f));
        buff.addModifier(BuffModifiers.SPEED, (Object)Float.valueOf(-10.0f));
        if (!buff.owner.isBoss()) {
            buff.addModifier(BuffModifiers.PARALYZED, (Object)true);
            buff.addModifier(BuffModifiers.INTIMIDATED, (Object)true);
        }
    }
}

