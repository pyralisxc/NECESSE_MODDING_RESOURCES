/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.BirdCritterRunAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.IdleAnimationAINode;
import necesse.entity.mobs.friendly.critters.BirdMob;

public class BirdCritterAI<T extends BirdMob>
extends SelectorAINode<T> {
    public BirdCritterRunAINode<T> runNode;

    public BirdCritterAI() {
        this.addChild(new IdleAnimationAINode<T>(){

            @Override
            public int getIdleAnimationCooldown(GameRandom random) {
                return random.getIntBetween(100, 200);
            }

            @Override
            public void runIdleAnimation(BirdMob mob) {
                mob.peckAbility.runAndSend();
            }
        });
        this.runNode = new BirdCritterRunAINode();
        this.addChild(this.runNode);
    }
}

