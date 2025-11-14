/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.ascendedWizard.animations;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.animations.AscendedWizardAnimation;

public class AscendedWizardAnimationList
extends AscendedWizardAnimation {
    protected AscendedWizardAnimation[] animations;
    protected int currentIndex = 0;

    public AscendedWizardAnimationList(AscendedWizardAnimation ... animations) {
        this.animations = animations;
    }

    @Override
    public void onMovementTick(Mob mob, float delta) {
        super.onMovementTick(mob, delta);
        if (this.currentIndex >= this.animations.length) {
            return;
        }
        AscendedWizardAnimation currentAnimation = this.animations[this.currentIndex];
        while (currentAnimation.isAnimationFinished(mob)) {
            currentAnimation.onAnimationEnded(mob);
            ++this.currentIndex;
            if (this.currentIndex >= this.animations.length) {
                return;
            }
            currentAnimation = this.animations[this.currentIndex];
            currentAnimation.onAnimationStarted(mob);
        }
        currentAnimation.onMovementTick(mob, delta);
    }

    @Override
    public void onAnimationStarted(Mob mob) {
        this.currentIndex = 0;
        this.animations[0].onAnimationStarted(mob);
    }

    @Override
    public boolean isAnimationFinished(Mob mob) {
        return this.currentIndex >= this.animations.length;
    }

    @Override
    public Point getSprite(Mob mob) {
        int currentIndex = Math.min(this.currentIndex, this.animations.length - 1);
        return this.animations[currentIndex].getSprite(mob);
    }
}

