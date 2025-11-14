/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.hostile.bosses.FlyingSpiritsHead;
import necesse.gfx.GameResources;

public class SageHead
extends FlyingSpiritsHead {
    public SageHead() {
        super(FlyingSpiritsHead.Variant.SAGE);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            SoundManager.playSound(GameResources.sagebegin, (SoundEffect)SoundEffect.effect(this).volume(1.6f).falloffDistance(4000));
        }
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.sagehurt).volume(0.4f).fallOffDistance(1500);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.sagedeath).fallOffDistance(3000);
    }
}

