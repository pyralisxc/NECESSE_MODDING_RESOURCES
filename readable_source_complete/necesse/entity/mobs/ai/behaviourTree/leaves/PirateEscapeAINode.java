/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.hostile.pirates.PirateMob;

public class PirateEscapeAINode<T extends PirateMob>
extends MoveTaskAINode<T> {
    public final int maxDistance;
    public boolean firedEvent;
    public long nextPathFindTime;

    public PirateEscapeAINode(int maxDistance) {
        this.maxDistance = maxDistance;
    }

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
        if (((PirateMob)mob).baseTile == null) {
            return AINodeResult.FAILURE;
        }
        if (((Mob)mob).getDistance(((PirateMob)mob).baseTile.x * 32 + 16, ((PirateMob)mob).baseTile.y * 32 + 16) > (float)this.maxDistance) {
            if (!this.firedEvent) {
                this.nextPathFindTime = 0L;
                this.onEscape(mob);
                this.firedEvent = true;
            }
            if (!((PirateMob)mob).buffManager.hasBuff(BuffRegistry.PIRATE_ESCAPE)) {
                ((Mob)mob).addBuff(new ActiveBuff(BuffRegistry.PIRATE_ESCAPE, (Mob)mob, 0, null), true);
            }
            if (this.nextPathFindTime <= ((Entity)mob).getWorldEntity().getLocalTime()) {
                this.nextPathFindTime = ((Entity)mob).getWorldEntity().getLocalTime() + 1000L;
                return this.moveToTileTask(((PirateMob)mob).baseTile.x, ((PirateMob)mob).baseTile.y, null, path -> {
                    if (path.moveIfWithin(-1, 1, () -> {
                        this.nextPathFindTime = 0L;
                    })) {
                        int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 2000, 0.1f);
                        this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                    }
                    return AINodeResult.SUCCESS;
                });
            }
            return AINodeResult.SUCCESS;
        }
        this.firedEvent = false;
        if (((PirateMob)mob).buffManager.hasBuff(BuffRegistry.PIRATE_ESCAPE)) {
            ((PirateMob)mob).buffManager.removeBuff(BuffRegistry.PIRATE_ESCAPE, true);
        }
        return AINodeResult.FAILURE;
    }

    public void onEscape(T mob) {
    }
}

