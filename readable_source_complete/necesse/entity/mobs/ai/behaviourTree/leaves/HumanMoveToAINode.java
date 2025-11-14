/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.function.BiPredicate;
import necesse.engine.util.GameMath;
import necesse.entity.Entity;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.MoveToTile;

public class HumanMoveToAINode<T extends HumanMob>
extends MoveTaskAINode<T> {
    public long pathFindingCooldown;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onEvent("newTargetFound", e -> {
            if (mob.objectUser != null) {
                mob.objectUser.stopUsing();
            }
        });
        blackboard.onEvent("resetPathTime", e -> {
            this.pathFindingCooldown = 0L;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        MoveToTile moveToTile = ((HumanMob)mob).getMoveToPoint();
        if (moveToTile != null) {
            BiPredicate<Point, Point> isAtTarget;
            if (((HumanMob)mob).objectUser != null) {
                ((HumanMob)mob).objectUser.stopUsing();
            }
            float tileDist = (float)moveToTile.distance(GameMath.getTileFloatCoordinate(((Entity)mob).getX()), GameMath.getTileFloatCoordinate(((Entity)mob).getY()));
            if (this.pathFindingCooldown < mob.getLocalTime()) {
                this.pathFindingCooldown = mob.getLocalTime() + 2000L;
                BiPredicate<Point, Point> isAtTarget2 = moveToTile.acceptAdjacentTiles ? TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), moveToTile.x, moveToTile.y) : null;
                return this.moveToTileTask(moveToTile.x, moveToTile.y, isAtTarget2, path -> {
                    boolean foundPathTo = moveToTile.moveIfPathFailed(tileDist) ? path.moveIfWithin(-1, -1, () -> {
                        this.pathFindingCooldown = 0L;
                    }) : path.moveIfWithin(-1, 1, () -> {
                        this.pathFindingCooldown = 0L;
                    });
                    if (foundPathTo) {
                        int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 2000, 0.1f);
                        this.pathFindingCooldown = mob.getLocalTime() + (long)nextPathTimeAdd;
                    }
                    if (moveToTile.isAtLocation(tileDist, foundPathTo)) {
                        moveToTile.onArrivedAtLocation();
                    }
                    return AINodeResult.SUCCESS;
                });
            }
            Point finalDestination = blackboard.mover.getFinalDestination();
            if (finalDestination != null && moveToTile.isAtLocation(tileDist, (isAtTarget = moveToTile.acceptAdjacentTiles ? TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), moveToTile.x, moveToTile.y) : (p1, p2) -> p1.x == p2.x && p1.y == p2.y).test(finalDestination, moveToTile))) {
                moveToTile.onArrivedAtLocation();
            }
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }
}

