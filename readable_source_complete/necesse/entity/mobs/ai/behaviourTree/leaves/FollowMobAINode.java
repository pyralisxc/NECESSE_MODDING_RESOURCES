/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.engine.util.pathfinding.PathResult;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;

public abstract class FollowMobAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public Mob lastTarget;
    public long changePositionCountdown;
    public long nextFindPathTime;
    public boolean ranLastTick;
    public boolean startedMoving;
    public int maxChangePositionCooldown = 4000;
    public int minChangePositionCooldown = 8000;
    public int tileRadius = 3;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onEvent("newCommandSet", event -> {
            this.changePositionCountdown = 0L;
        });
        blackboard.onEvent("resetPathTime", e -> {
            this.nextFindPathTime = 0L;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        if (!this.ranLastTick) {
            this.startedMoving = false;
        }
        this.ranLastTick = false;
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        Mob target = this.getFollowingMob(mob);
        if (this.lastTarget != target) {
            this.changePositionCountdown = 0L;
        }
        this.lastTarget = target;
        return this.tickFollowing(target, mob, blackboard);
    }

    public AINodeResult tickFollowing(Mob target, T mob, Blackboard<T> blackboard) {
        if (target != null) {
            this.ranLastTick = true;
            if (this.lastTarget == target && this.startedMoving && !blackboard.mover.isCurrentlyMovingFor(this)) {
                this.startedMoving = false;
                this.onMovedToFollowTarget(target, mob, blackboard, true);
            }
            if (!blackboard.mover.isMoving()) {
                this.changePositionCountdown -= 50L;
            }
            if (this.changePositionCountdown <= 0L || this.nextFindPathTime <= ((Entity)mob).getWorldEntity().getLocalTime() && GameMath.diagonalMoveDistance(((Entity)mob).getX(), ((Entity)mob).getY(), target.getX(), target.getY()) >= (double)((float)this.tileRadius * 1.5f * 32.0f)) {
                this.changePositionCountdown = GameRandom.globalRandom.getIntBetween(this.maxChangePositionCooldown, this.maxChangePositionCooldown);
                Point pos = this.findNewPosition(mob, target);
                if (pos != null) {
                    return this.moveToTileTask(pos.x, pos.y, null, result -> {
                        if (result.moveIfWithin(-1, -1, null)) {
                            this.startedMoving = true;
                            int nextPathTimeAdd = result.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 500, 0.1f);
                            this.nextFindPathTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                        } else {
                            this.nextFindPathTime = mob.getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 15000);
                            this.onMovedToFollowTarget(target, mob, blackboard, false);
                        }
                        return AINodeResult.SUCCESS;
                    });
                }
                this.nextFindPathTime = ((Entity)mob).getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 15000);
                this.onMovedToFollowTarget(target, mob, blackboard, false);
            }
            return AINodeResult.SUCCESS;
        }
        return AINodeResult.FAILURE;
    }

    public void onMovedToFollowTarget(Mob target, T mob, Blackboard<T> blackboard, boolean foundPosition) {
    }

    public Point findNewPosition(T mob, Mob follow) {
        int tileX = follow.getTileX();
        int tileY = follow.getTileY();
        Biome baseBiome = ((Mob)mob).getWanderBaseBiome(((Entity)mob).getLevel());
        PriorityMap<Point> priorityMap = new PriorityMap<Point>();
        for (int x = -this.tileRadius; x <= this.tileRadius; ++x) {
            for (int y = -this.tileRadius; y <= this.tileRadius; ++y) {
                if (x == 0 && y == 0) continue;
                Point lp = new Point(tileX + x, tileY + y);
                if (((Entity)mob).getLevel().isSolidTile(lp.x, lp.y) || !((Mob)mob).estimateCanMoveTo(lp.x, lp.y, false) || !this.canPath(tileX, tileY, lp.x, lp.y, this.tileRadius + 5)) continue;
                priorityMap.add(((Mob)mob).getTileWanderPriority(new TilePosition(((Entity)mob).getLevel(), lp), baseBiome), lp);
            }
        }
        ArrayList positions = priorityMap.getBestObjects(20);
        if (positions.isEmpty()) {
            return null;
        }
        int index = GameRandom.globalRandom.nextInt(positions.size());
        return (Point)positions.get(index);
    }

    public boolean canPath(int fromTileX, int fromTileY, int toTileX, int toTileY, int maxIterations) {
        return this.canPath(new Point(fromTileX, fromTileY), new Point(toTileX, toTileY), maxIterations);
    }

    public boolean canPath(Point fromTile, Point toTile, int maxIterations) {
        TilePathfinding tilePathFinding = new TilePathfinding(((Entity)this.mob()).getLevel().tickManager(), ((Entity)this.mob()).getLevel(), (Mob)this.mob(), null, this.getBlackboard().mover.getPathOptions(this));
        PathResult result = tilePathFinding.findPath(fromTile, toTile, maxIterations);
        return result.foundTarget;
    }

    public abstract Mob getFollowingMob(T var1);
}

