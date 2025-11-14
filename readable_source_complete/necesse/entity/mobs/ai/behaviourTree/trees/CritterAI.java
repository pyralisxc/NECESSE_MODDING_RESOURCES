/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CritterRunAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.friendly.critters.CritterMob;

public class CritterAI<T extends CritterMob>
extends SelectorAINode<T> {
    public final WandererAINode<T> wanderer;

    public CritterAI(AINode<T> runner, WandererAINode<T> wanderer) {
        if (runner != null) {
            this.addChild(runner);
        }
        this.wanderer = wanderer;
        this.addChild(this.wanderer);
    }

    public CritterAI(AINode<T> runner) {
        this(runner, new WandererAINode(10000));
    }

    public CritterAI() {
        this(new CritterRunAINode());
    }
}

