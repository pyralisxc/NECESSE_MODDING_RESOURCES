/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;

public abstract class WanderHomeAtConditionAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public long nextPathFindTime;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onEvent("resetPathTime", e -> {
            this.nextPathFindTime = 0L;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (this.shouldGoHome(mob)) {
            boolean findNewPos;
            Point home;
            if (this.nextPathFindTime <= ((Entity)mob).getWorldEntity().getLocalTime() && (home = this.getHomeTile(mob)) != null && (findNewPos = this.shouldFindNewPos(mob, home))) {
                Point newPos = null;
                if (((Entity)mob).getLevel().isOutside(home.x, home.y)) {
                    int outsideHomeRadius = this.getOutsideHomeRadius(mob);
                    newPos = WandererAINode.findWanderingPointAround(mob, home.x, home.y, outsideHomeRadius, null, (tp, biome) -> {
                        if (mob.getDistance(tp.tileX * 32 + 16, tp.tileY * 32 + 16) > (float)(outsideHomeRadius * 32)) {
                            return Integer.MIN_VALUE;
                        }
                        return mob.getTileWanderPriority((TilePosition)tp, (Biome)biome);
                    }, 20, 5);
                } else {
                    ConnectedSubRegionsResult regions = null;
                    if (this.isHomeRoom(mob)) {
                        regions = ((Entity)mob).getLevel().regionManager.getRoomConnectedByTile(home.x, home.y, true, 2000);
                    } else if (this.isHomeHouse(mob)) {
                        regions = ((Entity)mob).getLevel().regionManager.getHouseConnectedByTile(home.x, home.y, 4000);
                    }
                    if (regions != null) {
                        newPos = WandererAINode.findWanderingPointInsideRegions(mob, regions, 25, (arg_0, arg_1) -> mob.getTileWanderPriority(arg_0, arg_1), 20, 5);
                    }
                }
                if (newPos != null) {
                    return this.moveToTileTask(newPos.x, newPos.y, null, path -> {
                        if (path.moveIfWithin(-1, -1, () -> {
                            this.nextPathFindTime = 0L;
                        })) {
                            int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 2000, 0.1f);
                            this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                        } else {
                            this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 16000);
                        }
                        return AINodeResult.RUNNING;
                    });
                }
                this.nextPathFindTime = ((Entity)mob).getWorldEntity().getLocalTime() + (long)GameRandom.globalRandom.getIntBetween(10000, 16000);
            }
            if (blackboard.mover.isMoving() && blackboard.mover.isCurrentlyMovingFor(this)) {
                Point base = this.getHomeTile(mob);
                if (base != null && !this.shouldFindNewPos(mob, base)) {
                    return AINodeResult.FAILURE;
                }
                return AINodeResult.RUNNING;
            }
            return AINodeResult.FAILURE;
        }
        this.nextPathFindTime = 0L;
        return AINodeResult.FAILURE;
    }

    protected int getOutsideHomeRadius(T mob) {
        return 6;
    }

    protected boolean shouldFindNewPos(T mob, Point homeTile) {
        boolean isOutside = ((Entity)mob).getLevel().isOutside(homeTile.x, homeTile.y);
        return isOutside ? ((Mob)mob).getDistance(homeTile.x * 32 + 16, homeTile.y * 32 + 16) > (float)(this.getOutsideHomeRadius(mob) * 32) : ((Entity)mob).getLevel().getRoomID(((Entity)mob).getTileX(), ((Entity)mob).getTileY()) != ((Entity)mob).getLevel().getRoomID(homeTile.x, homeTile.y);
    }

    public abstract boolean shouldGoHome(T var1);

    public abstract Point getHomeTile(T var1);

    public abstract boolean isHomeRoom(T var1);

    public abstract boolean isHomeHouse(T var1);
}

