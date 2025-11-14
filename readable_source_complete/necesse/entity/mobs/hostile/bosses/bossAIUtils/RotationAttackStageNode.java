/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.CompositeAINode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class RotationAttackStageNode<T extends Mob>
extends CompositeAINode<T>
implements AttackStageInterface<T> {
    public ArrayList<AINode<T>> nodes;
    public int currentNode = -1;

    public RotationAttackStageNode(AINode<T> ... nodes) {
        this.nodes = new ArrayList<AINode<T>>(Arrays.asList(nodes));
    }

    @Override
    protected AINodeResult tickChildren(AINode<T> lastRunningChild, AINodeResult runningChildResult, Iterable<AINode<T>> children, T mob, Blackboard<T> blackboard) {
        AINode<T> child = this.nodes.get(this.currentNode);
        AINodeResult result = child.tick(mob, blackboard);
        if (result == AINodeResult.RUNNING) {
            this.runningNode = child;
        }
        return result;
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        for (AINode<T> node : this.nodes) {
            node.init(mob, blackboard);
        }
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        this.currentNode = (this.currentNode + 1) % this.nodes.size();
        AINode<T> next = this.nodes.get(this.currentNode);
        if (next instanceof AttackStageInterface) {
            ((AttackStageInterface)((Object)next)).onStarted(mob, blackboard);
        }
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
        AINode<T> last = this.nodes.get(this.currentNode);
        if (last instanceof AttackStageInterface) {
            ((AttackStageInterface)((Object)last)).onEnded(mob, blackboard);
        }
    }
}

