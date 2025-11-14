/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob;

import necesse.engine.sound.SameNearSoundCooldown;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.summon.summonFollowingMob.SummonedFollowingMob;

public class PetFollowingMob
extends SummonedFollowingMob {
    public PetFollowingMob(int health) {
        super(health);
        this.ambientSoundCooldownMin = 10000;
        this.ambientSoundCooldownMax = 20000;
    }

    @Override
    public void init() {
        if (this.isClient()) {
            this.playAmbientSound();
        }
        this.shouldPlayAmbience = false;
        super.init();
    }

    @Override
    public void playAmbientSound() {
        SoundSettings ambientSound = this.getAmbientSound();
        if (ambientSound != null) {
            ambientSound.cooldown(new SameNearSoundCooldown(200, 96));
            SoundManager.playSound(ambientSound, this);
        }
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return null;
    }
}

