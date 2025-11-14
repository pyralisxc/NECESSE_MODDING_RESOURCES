/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.animations;

import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardAnimationList;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.SimpleLoopingAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.SimpleSingleAnimation;

public class AscendedWizardStage1StartTransformationAnimation
extends AscendedWizardAnimationList {
    public AscendedWizardStage1StartTransformationAnimation(int totalTime) {
        super(new SimpleSingleAnimation(0, 11, 2, 200), new SimpleLoopingAnimation(0, 12, 4, 400).setTotalTime(totalTime - 200));
    }
}

