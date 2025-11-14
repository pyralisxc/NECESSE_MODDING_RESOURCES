/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.activeJob.SlowlyConsumeItemActiveJob;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class HumanChillingAINode<T extends HumanMob>
extends MoveTaskAINode<T> {
    public int chillTicksCountdown;
    public Point moveToChillPos;
    public ObjectUserActive active;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        this.chillTicksCountdown = 20 * GameRandom.globalRandom.getIntBetween(5, 120);
        blackboard.onWasHit(e -> {
            if (this.active != null && this.active == mob.objectUser) {
                this.active.stopUsing();
                this.active = null;
            }
        });
        blackboard.onUnloading(e -> {
            if (this.active != null && this.active == mob.objectUser) {
                this.active.stopUsing();
                this.active = null;
            }
        });
    }

    @Override
    protected void onInterruptRunning(T mob, Blackboard<T> blackboard) {
        super.onInterruptRunning(mob, blackboard);
        if (this.active != null && this.active == ((HumanMob)mob).objectUser) {
            this.active.stopUsing();
            this.active = null;
        }
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    public boolean canChill(T mob) {
        if (((HumanMob)mob).isVisitor()) {
            return false;
        }
        if (mob.isBeingInteractedWith()) {
            return false;
        }
        if (((HumanMob)mob).isDowned() || ((HumanMob)mob).isTrapped()) {
            return false;
        }
        NetworkSettlementData settlement = ((HumanMob)mob).getSettlerSettlementNetworkData();
        if (settlement != null && settlement.isRaidActive()) {
            return false;
        }
        return !((HumanMob)mob).hasCommandOrders() || ((HumanMob)mob).isHiding;
    }

    public boolean shouldChill(T mob) {
        return true;
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (this.active != null && ((HumanMob)mob).objectUser == this.active) {
            boolean stopChill = false;
            if (!this.canChill(mob)) {
                stopChill = true;
            } else {
                --this.chillTicksCountdown;
                if (this.chillTicksCountdown <= 0) {
                    stopChill = true;
                }
            }
            if (stopChill) {
                this.active.stopUsing();
                this.active = null;
                this.chillTicksCountdown = 20 * GameRandom.globalRandom.getIntBetween(5, 120);
            } else {
                this.active.keepUsing();
                ((HumanMob)mob).regenWorkBreakBuffer(50);
            }
            return AINodeResult.SUCCESS;
        }
        if (this.moveToChillPos != null && this.canChill(mob)) {
            if (blackboard.mover.isCurrentlyMovingFor(this)) {
                return AINodeResult.RUNNING;
            }
            OEUsers chair = SlowlyConsumeItemActiveJob.isChair(((Entity)mob).getLevel(), this.moveToChillPos.x, this.moveToChillPos.y);
            if (chair != null && chair.getCanUseError((Mob)mob) == null) {
                if (TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), this.moveToChillPos.x, this.moveToChillPos.y, ((Entity)mob).getTileX(), ((Entity)mob).getTileY())) {
                    chair.startUser((Mob)mob);
                    this.active = ((HumanMob)mob).objectUser;
                    this.moveToChillPos = null;
                    this.chillTicksCountdown = 20 * GameRandom.globalRandom.getIntBetween(30, 60);
                    return AINodeResult.RUNNING;
                }
            } else {
                this.chillTicksCountdown = 0;
            }
        }
        this.moveToChillPos = null;
        this.active = null;
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            blackboard.mover.stopMoving((Mob)mob);
        }
        if (this.canChill(mob) && this.shouldChill(mob)) {
            --this.chillTicksCountdown;
            if (this.chillTicksCountdown <= 0) {
                this.chillTicksCountdown = 20 * GameRandom.globalRandom.getIntBetween(10, 20);
                ZoneTester zoneTester = ((HumanMob)mob).levelSettler != null ? ((HumanMob)mob).levelSettler.isTileInSettlementBoundsAndRestrictZoneTester() : (x, y) -> true;
                PriorityMap<Point> chairs = SlowlyConsumeItemActiveJob.findValidChairs(((Entity)mob).getLevel(), ((Entity)mob).getTileX(), ((Entity)mob).getTileY(), mob, 2, (tile, oeUsers) -> zoneTester.containsTile(tile.x, tile.y) && mob.estimateCanMoveTo(tile.x, tile.y, true), false);
                if (!chairs.isEmpty()) {
                    Point chair = chairs.getRandomBestObject(GameRandom.globalRandom, 1);
                    return this.moveToTileTask(chair.x, chair.y, TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), chair.x, chair.y), path -> {
                        if (path.result.foundTarget) {
                            this.moveToChillPos = chair;
                            path.move(null);
                            return AINodeResult.RUNNING;
                        }
                        return null;
                    });
                }
                this.chillTicksCountdown = 20 * GameRandom.globalRandom.getIntBetween(30, 60);
            }
        }
        return AINodeResult.FAILURE;
    }
}

