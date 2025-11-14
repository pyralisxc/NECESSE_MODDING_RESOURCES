/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.hostile.JumpingHostileMob;
import necesse.gfx.GameResources;

public abstract class HostileSlimeMob
extends JumpingHostileMob {
    public HostileSlimeMob(int health) {
        super(health);
    }

    @Override
    public void onJump() {
        if (this.isClient()) {
            SoundManager.playSound(new SoundSettings(GameResources.slimeSplash2).volume(0.15f).pitchVariance(0.1f), this);
        }
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.slimeSplash1).volume(0.5f);
    }
}

