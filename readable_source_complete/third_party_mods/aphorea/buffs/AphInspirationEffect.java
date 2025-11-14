/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.buffs.ActiveBuff
 */
package aphorea.buffs;

import aphorea.buffs.AphShownBuff;
import aphorea.registry.AphModifiers;
import necesse.entity.mobs.buffs.ActiveBuff;

public class AphInspirationEffect
extends AphShownBuff {
    public float getInspirationEffect(ActiveBuff ab) {
        return ab.owner == null ? 1.0f : ((Float)ab.owner.buffManager.getModifier(AphModifiers.INSPIRATION_EFFECT)).floatValue();
    }
}

