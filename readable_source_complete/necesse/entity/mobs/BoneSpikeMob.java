/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.sound.SoundPlayer;
import necesse.entity.mobs.ExplosiveSpikeMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class BoneSpikeMob
extends ExplosiveSpikeMob {
    public BoneSpikeMob() {
    }

    public BoneSpikeMob(Mob mobOwner, GameDamage damage, long startCrackingTime) {
        super(mobOwner, damage, startCrackingTime);
    }

    @Override
    protected SoundPlayer playStartCrackingSound() {
        return super.playStartCrackingSound();
    }
}

