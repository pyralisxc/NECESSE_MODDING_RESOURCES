/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.decorators;

import java.awt.Point;
import java.util.function.BiPredicate;
import java.util.function.Function;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.decorators.TaskAINode;
import necesse.entity.mobs.ai.behaviourTree.util.MoveToTileAITask;

public abstract class MoveTaskAINode<T extends Mob>
extends TaskAINode<T> {
    public final AINodeResult moveToTileTask(int tileX, int tileY, BiPredicate<Point, Point> isAtTarget, int maxPathIterations, Function<MoveToTileAITask.AIPathResult, AINodeResult> handler) {
        return this.startTask(this.getBlackboard().mover.moveToTileTask(this, tileX, tileY, isAtTarget, maxPathIterations, handler));
    }

    public final AINodeResult moveToTileTask(int tileX, int tileY, BiPredicate<Point, Point> isAtTarget, Function<MoveToTileAITask.AIPathResult, AINodeResult> handler) {
        return this.startTask(this.getBlackboard().mover.moveToTileTask(this, tileX, tileY, isAtTarget, handler));
    }
}

