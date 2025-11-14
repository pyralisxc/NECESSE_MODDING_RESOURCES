/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.animations;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardAnimationList;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.SimpleSingleAnimation;
import necesse.gfx.GameResources;

public class AscendedWizardStage1TransformationAnimation
extends AscendedWizardAnimationList {
    public static int SLOW_FRAMES_TIME = 1000;
    public static int FAST_FRAMES_TIME = 500;

    public AscendedWizardStage1TransformationAnimation() {
        super(new SimpleSingleAnimation(0, 13, 3, SLOW_FRAMES_TIME){

            @Override
            public void onAnimationEnded(Mob mob) {
                super.onAnimationEnded(mob);
                SoundManager.playSound(GameResources.ascendedWizardStage2Begin, (SoundEffect)SoundEffect.effect(mob).volume(1.5f).falloffDistance(4000));
            }
        }, new SimpleSingleAnimation(3, 13, 5, FAST_FRAMES_TIME));
    }
}

