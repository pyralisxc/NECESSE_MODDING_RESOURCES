/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Line2D;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.level.maps.CollisionFilter;

public abstract class CooldownAttackTargetAINode<T extends Mob>
extends AINode<T> {
    public String targetKey;
    public CooldownTimer cooldownTimer;
    public int attackCooldown;
    public int attackDistance;
    public long attackTimer;

    public CooldownAttackTargetAINode(String targetKey, CooldownTimer cooldownTimer, int attackCooldown, int attackDistance) {
        this.targetKey = targetKey;
        this.cooldownTimer = cooldownTimer;
        this.attackCooldown = attackCooldown;
        this.attackDistance = attackDistance;
    }

    public CooldownAttackTargetAINode(CooldownTimer cooldownTimer, int attackCooldown, int attackDistance) {
        this("currentTarget", cooldownTimer, attackCooldown, attackDistance);
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, this.targetKey);
        if (this.cooldownTimer == CooldownTimer.TICK) {
            this.attackTimer += 50L;
        }
        if (target != null) {
            if (this.cooldownTimer == CooldownTimer.HAS_TARGET) {
                this.attackTimer += 50L;
            }
            if (this.attackDistance < 0 || ((Mob)mob).getDistance(target) < (float)this.attackDistance) {
                if (this.cooldownTimer == CooldownTimer.IN_RANGE) {
                    this.attackTimer += 50L;
                }
                if (this.canAttackTarget(mob, target)) {
                    if (this.cooldownTimer == CooldownTimer.CAN_ATTACK) {
                        this.attackTimer += 50L;
                    }
                    if (this.attackTimer >= (long)this.attackCooldown && this.attackTarget(mob, target)) {
                        this.attackTimer = 0L;
                    }
                }
            }
        }
        if (this.attackTimer > (long)this.attackCooldown) {
            this.attackTimer = this.attackCooldown;
        }
        return AINodeResult.SUCCESS;
    }

    public void randomizeAttackTimer() {
        this.attackTimer = GameRandom.globalRandom.nextInt(this.attackCooldown);
    }

    public boolean hasLineOfSight(T mob, Mob target) {
        CollisionFilter collisionFilter = ((Mob)mob).modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target);
        return !((Entity)mob).getLevel().collides(new Line2D.Float(((Mob)mob).x, ((Mob)mob).y, target.x, target.y), collisionFilter);
    }

    public boolean canAttackTarget(T mob, Mob target) {
        return this.hasLineOfSight(mob, target);
    }

    public abstract boolean attackTarget(T var1, Mob var2);

    public static enum CooldownTimer {
        TICK,
        HAS_TARGET,
        IN_RANGE,
        CAN_ATTACK;

    }
}

