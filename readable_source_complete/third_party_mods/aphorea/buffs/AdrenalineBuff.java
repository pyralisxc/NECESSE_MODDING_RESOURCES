/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 */
package aphorea.buffs;

import aphorea.buffs.AphShownBuff;
import aphorea.registry.AphBuffs;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class AdrenalineBuff
extends AphShownBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.ALL_DAMAGE, (Object)Float.valueOf(0.05f));
        buff.addModifier(BuffModifiers.ARMOR, (Object)Float.valueOf(-0.05f));
    }

    public int getStackSize(ActiveBuff buff) {
        return 5;
    }

    public static void giveAdrenaline(Mob mob, int levels, int duration, boolean sendPacket) {
        for (int i = 0; i < levels; ++i) {
            AdrenalineBuff.giveAdrenaline(mob, duration, sendPacket);
        }
    }

    public static void giveAdrenaline(Mob mob, int duration, boolean sendPacket) {
        mob.buffManager.addBuff(new ActiveBuff(AphBuffs.ADRENALINE, mob, duration, null), sendPacket);
    }

    public static int getAdrenalineLevel(Mob mob) {
        if (!mob.buffManager.hasBuff(AphBuffs.ADRENALINE)) {
            return 0;
        }
        return mob.buffManager.getBuff(AphBuffs.ADRENALINE).getStacks();
    }
}

