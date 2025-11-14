/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 */
package aphorea.buffs;

import aphorea.buffs.AphInspirationEffect;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;

public class HarmonyBuff
extends AphInspirationEffect {
    public void init(ActiveBuff ab, BuffEventSubscriber eventSubscriber) {
        super.init(ab, eventSubscriber);
        float inspirationEffect = this.getInspirationEffect(ab);
        ab.setModifier(BuffModifiers.HEALTH_REGEN_FLAT, (Object)Float.valueOf(0.5f * inspirationEffect));
        ab.setModifier(BuffModifiers.COMBAT_HEALTH_REGEN_FLAT, (Object)Float.valueOf(0.25f * inspirationEffect));
        ab.setModifier(BuffModifiers.MANA_REGEN_FLAT, (Object)Float.valueOf(2.0f * inspirationEffect));
        ab.setModifier(BuffModifiers.COMBAT_MANA_REGEN_FLAT, (Object)Float.valueOf(inspirationEffect));
        ab.setModifier(BuffModifiers.SPEED, (Object)Float.valueOf(0.1f * inspirationEffect));
    }
}

