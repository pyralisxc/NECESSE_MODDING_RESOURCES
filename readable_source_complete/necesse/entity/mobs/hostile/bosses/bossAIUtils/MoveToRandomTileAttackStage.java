/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses.bossAIUtils;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.hostile.bosses.bossAIUtils.AttackStageInterface;

public class MoveToRandomTileAttackStage<T extends Mob>
extends MoveTaskAINode<T>
implements AttackStageInterface<T> {
    public int baseDistance;
    public boolean isRunningWhileMoving;
    public String targetKey;

    public MoveToRandomTileAttackStage(boolean isRunningWhileMoving, int baseDistance, String targetKey) {
        this.isRunningWhileMoving = isRunningWhileMoving;
        this.baseDistance = baseDistance;
        this.targetKey = targetKey;
    }

    public MoveToRandomTileAttackStage(boolean isRunningWhileMoving, int baseDistance) {
        this(isRunningWhileMoving, baseDistance, "currentTarget");
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void onStarted(T mob, Blackboard<T> blackboard) {
        Mob currentTarget = blackboard.getObject(Mob.class, this.targetKey);
        Point2D.Float base = new Point2D.Float(((Mob)mob).x, ((Mob)mob).y);
        if (currentTarget != null) {
            base = new Point2D.Float(currentTarget.x, currentTarget.y);
        }
        Point tileOffset = ((Mob)mob).getPathMoveOffset();
        Point tile = new Point(((Entity)mob).getTileX(), ((Entity)mob).getTileY());
        for (int i = 0; i < 20; ++i) {
            int randomAngle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float angleDir = GameMath.getAngleDir(randomAngle);
            tile = new Point(GameMath.getTileCoordinate(base.x + angleDir.x * (float)this.baseDistance), GameMath.getTileCoordinate(base.y + angleDir.y * (float)this.baseDistance));
            if (!((Mob)mob).collidesWith(((Entity)mob).getLevel(), tile.x * 32 + tileOffset.x, tile.y * 32 + tileOffset.y) && ((Mob)mob).getDistance(tile.x * 32 + tileOffset.x, tile.y * 32 + tileOffset.y) >= (float)this.baseDistance / 4.0f && ((Mob)mob).estimateCanMoveTo(tile.x, tile.y, false)) break;
        }
        this.moveToTileTask(tile.x, tile.y, null, path -> {
            if (path.moveIfWithin(-1, -1, null)) {
                if (this.isRunningWhileMoving) {
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.SUCCESS;
            }
            return AINodeResult.FAILURE;
        });
    }

    @Override
    public void onEnded(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (this.isRunningWhileMoving && blackboard.mover.isMoving()) {
            return AINodeResult.RUNNING;
        }
        return AINodeResult.SUCCESS;
    }
}

