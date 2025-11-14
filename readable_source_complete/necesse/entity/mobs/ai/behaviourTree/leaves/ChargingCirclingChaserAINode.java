/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;

public class ChargingCirclingChaserAINode<T extends Mob>
extends AINode<T> {
    public String targetKey = "currentTarget";
    public String chaserTargetKey = "chaserTarget";
    public int circlingRange;
    public int nextAngleOffset;
    public boolean backOffOnReset = true;
    public boolean resetOnOutsideCircleRange = false;
    public int circlingTicks = 1;
    protected Mob chargingTarget;
    protected int nextAttack;
    protected float circlingDirection = 1.0f;
    private int startMoveAccuracy;

    public ChargingCirclingChaserAINode(int circlingRange, int nextAngleOffset) {
        this.circlingRange = circlingRange;
        this.nextAngleOffset = nextAngleOffset;
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        this.startMoveAccuracy = ((Mob)mob).moveAccuracy;
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        blackboard.put(this.chaserTargetKey, null);
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, this.targetKey);
        if (target != null && target.isSamePlace((Entity)mob)) {
            if (this.chargingTarget != null) {
                if (!this.chargingTarget.removed() && this.chargingTarget.canBeTargeted((Mob)mob, null)) {
                    float targetAngle;
                    float currentAngle;
                    float diff;
                    blackboard.mover.setMobTarget(this, this.chargingTarget);
                    if (((Mob)mob).getDistance(this.chargingTarget) < (float)this.circlingRange / 3.0f && (Math.abs(diff = GameMath.getAngleDifference(currentAngle = GameMath.getAngle(new Point2D.Float(((Mob)mob).dx, ((Mob)mob).dy)), targetAngle = GameMath.getAngle(new Point2D.Float(target.x - ((Mob)mob).x, target.y - ((Mob)mob).y)))) >= 40.0f || ((Mob)mob).dx == 0.0f && ((Mob)mob).dy == 0.0f)) {
                        this.startCircling(mob, blackboard, target, this.circlingTicks);
                    }
                } else {
                    this.startCircling(mob, blackboard, target, this.circlingTicks);
                }
                blackboard.put(this.chaserTargetKey, this.chargingTarget);
            } else {
                if (!blackboard.mover.isCurrentlyMovingFor(this) || ((Mob)mob).hasArrivedAtTarget() || this.resetOnOutsideCircleRange && ((Mob)mob).getDistance(target) > (float)this.circlingRange) {
                    --this.nextAttack;
                    if (this.nextAttack <= 0) {
                        this.startCharge(mob, blackboard, target);
                    } else {
                        float currentAngle = GameMath.getAngle(new Point2D.Float(((Mob)mob).x - target.x, ((Mob)mob).y - target.y));
                        float nextAngle = currentAngle + GameRandom.globalRandom.getFloatBetween(0.0f, (float)this.nextAngleOffset * this.circlingDirection);
                        this.findNewPosition(nextAngle, mob, blackboard, target);
                    }
                }
                blackboard.put(this.chaserTargetKey, target);
            }
            return AINodeResult.SUCCESS;
        }
        this.chargingTarget = null;
        return AINodeResult.FAILURE;
    }

    public void startCharge(T mob, Blackboard<T> blackboard, Mob target) {
        ((Mob)mob).moveAccuracy = 5;
        blackboard.mover.setMobTarget(this, target);
        this.chargingTarget = target;
    }

    public void startCircling(T mob, Blackboard<T> blackboard, Mob target, int circlingTicks) {
        this.startCircling(mob, blackboard, target, circlingTicks, 0.0f);
    }

    public void startCircling(T mob, Blackboard<T> blackboard, Mob target, int circlingTicks, float direction) {
        float escapeAngle = this.backOffOnReset ? GameMath.getAngle(new Point2D.Float(((Mob)mob).x - target.x, ((Mob)mob).y - target.y)) : GameMath.getAngle(new Point2D.Float(target.x - ((Mob)mob).x, target.y - ((Mob)mob).y));
        this.findNewPosition((int)escapeAngle, mob, blackboard, target);
        this.chargingTarget = null;
        this.nextAttack = circlingTicks;
        this.circlingDirection = Math.signum(direction);
        if (this.circlingDirection == 0.0f) {
            float currentAngle = GameMath.getAngle(new Point2D.Float(((Mob)mob).dx, ((Mob)mob).dy));
            float angleDifference = GameMath.getAngleDifference(escapeAngle, currentAngle);
            this.circlingDirection = angleDifference > 0.0f ? 1.0f : -1.0f;
        }
    }

    public void findNewPosition(float escapeAngle, T mob, Blackboard<T> blackboard, Mob target) {
        float nx = (float)Math.cos(Math.toRadians(escapeAngle));
        float ny = (float)Math.sin(Math.toRadians(escapeAngle));
        ((Mob)mob).moveAccuracy = this.startMoveAccuracy;
        blackboard.mover.directMoveTo(this, (int)(target.x + nx * (float)this.circlingRange), (int)(target.y + ny * (float)this.circlingRange));
    }

    public void fixMoveAccuracy() {
        ((Mob)this.mob()).moveAccuracy = this.startMoveAccuracy;
    }
}

