/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.animations;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardAnimationList;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.SimpleSingleAnimation;

public class AscendedWizardStage2DeathAnimation
extends AscendedWizardAnimationList {
    public AscendedWizardStage2DeathAnimation(final Runnable onMiddleAnimationEnded) {
        super(new SimpleSingleAnimation(0, 1, 11, 1500), new SimpleSingleAnimation(2, 3, 1, 3000){

            @Override
            public void onAnimationEnded(Mob mob) {
                super.onAnimationEnded(mob);
                onMiddleAnimationEnded.run();
            }
        }, new SimpleSingleAnimation(0, 4, 9, 1000));
    }
}

