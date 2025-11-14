/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.animations;

import java.awt.Point;
import necesse.entity.mobs.Mob;

public abstract class AscendedWizardAnimation {
    public void onMovementTick(Mob mob, float delta) {
    }

    public abstract void onAnimationStarted(Mob var1);

    public abstract boolean isAnimationFinished(Mob var1);

    public void onAnimationEnded(Mob mob) {
    }

    public abstract Point getSprite(Mob var1);
}

