/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.animations;

import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardAnimationList;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.SimpleLoopingAnimation;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.SimpleSingleAnimation;

public class AscendedWizardStage1ChannelingAnimation
extends AscendedWizardAnimationList {
    public AscendedWizardStage1ChannelingAnimation(int totalTime) {
        super(new SimpleSingleAnimation(0, 5, 2, 200), new SimpleLoopingAnimation(0, 6, 4, 400).setTotalTime(totalTime - 200));
    }
}

