/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserActive;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.BedObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.SettlerBedObject;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public class HumanSleepAINode<T extends HumanMob>
extends MoveTaskAINode<T> {
    public int ticks;
    public Point moveToBedPos;
    public ObjectUserActive active;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        this.ticks = 20 * GameRandom.globalRandom.getIntBetween(5, 10);
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

    public boolean canSleep(T mob) {
        if (((HumanMob)mob).isVisitor()) {
            return false;
        }
        if (mob.isBeingInteractedWith()) {
            return false;
        }
        if (((HumanMob)mob).isDowned() || ((HumanMob)mob).isTrapped()) {
            return false;
        }
        if (((HumanMob)mob).home == null) {
            return false;
        }
        NetworkSettlementData settlement = ((HumanMob)mob).getSettlerSettlementNetworkData();
        if (settlement != null && settlement.isRaidActive()) {
            return false;
        }
        return !((HumanMob)mob).hasCommandOrders() || ((HumanMob)mob).isHiding;
    }

    public boolean shouldSleep(T mob) {
        if (((HumanMob)mob).isOnStrike()) {
            return false;
        }
        return ((HumanMob)mob).isHiding || ((Entity)mob).getWorldEntity().isNight();
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (this.active != null && ((HumanMob)mob).objectUser == this.active) {
            this.getBlackboard().put("isHidingInside", true);
            boolean wakeUp = false;
            if (!this.canSleep(mob)) {
                wakeUp = true;
            } else {
                --this.ticks;
                if (this.ticks <= 0) {
                    if (!this.shouldSleep(mob)) {
                        wakeUp = true;
                    } else {
                        this.ticks = 20 * GameRandom.globalRandom.getIntBetween(10, 30);
                    }
                }
            }
            if (wakeUp) {
                this.active.stopUsing();
                this.active = null;
                this.ticks = 20 * GameRandom.globalRandom.getIntBetween(5, 10);
            } else {
                this.active.keepUsing();
            }
            return AINodeResult.SUCCESS;
        }
        if (this.moveToBedPos != null && this.canSleep(mob)) {
            if (blackboard.mover.isCurrentlyMovingFor(this)) {
                return AINodeResult.RUNNING;
            }
            ObjectEntity oe = ((Entity)mob).getLevel().entityManager.getObjectEntity(this.moveToBedPos.x, this.moveToBedPos.y);
            if (oe instanceof BedObjectEntity && TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), this.moveToBedPos.x, this.moveToBedPos.y, ((Entity)mob).getTileX(), ((Entity)mob).getTileY()) && ((BedObjectEntity)oe).getCanUseError((Mob)mob) == null) {
                ((BedObjectEntity)oe).startUser((Mob)mob);
                this.active = ((HumanMob)mob).objectUser;
                this.moveToBedPos = null;
                this.ticks = 20 * GameRandom.globalRandom.getIntBetween(10, 30);
                return AINodeResult.RUNNING;
            }
        }
        this.moveToBedPos = null;
        this.active = null;
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            blackboard.mover.stopMoving((Mob)mob);
        }
        if (this.canSleep(mob) && this.shouldSleep(mob)) {
            --this.ticks;
            if (this.ticks <= 0) {
                BedObjectEntity bedObjectEntity;
                this.ticks = 20 * GameRandom.globalRandom.getIntBetween(5, 10);
                GameObject object = ((Entity)mob).getLevel().getObject(((HumanMob)mob).home.x, ((HumanMob)mob).home.y);
                if (object instanceof SettlerBedObject && ((SettlerBedObject)((Object)object)).isMasterBedObject(((Entity)mob).getLevel(), ((HumanMob)mob).home.x, ((HumanMob)mob).home.y) && ((Mob)mob).estimateCanMoveTo(((HumanMob)mob).home.x, ((HumanMob)mob).home.y, true) && (bedObjectEntity = ((SettlerBedObject)((Object)object)).getBedObjectEntity(((Entity)mob).getLevel(), ((HumanMob)mob).home.x, ((HumanMob)mob).home.y)) != null && bedObjectEntity.getCanUseError((Mob)mob) == null) {
                    return this.moveToTileTask(((HumanMob)mob).home.x, ((HumanMob)mob).home.y, TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), ((HumanMob)mob).home.x, ((HumanMob)mob).home.y), path -> {
                        if (path.result.foundTarget) {
                            this.moveToBedPos = new Point(mob.home.x, mob.home.y);
                            path.move(null);
                            return AINodeResult.RUNNING;
                        }
                        return null;
                    });
                }
            }
        }
        return AINodeResult.FAILURE;
    }
}

