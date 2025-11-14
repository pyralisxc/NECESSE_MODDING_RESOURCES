/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleLevelPos;

public class ConfusedWandererAINode<T extends Mob>
extends AINode<T> {
    public long confusionTimer = 0L;
    public boolean timerIsBasedOnWorld = false;
    public boolean collisionCausesDirectionChange = true;
    public float hitsCausesConfusionChance = 0.5f;
    public int hitCausesConfusionCooldown = 2000;
    protected long nextHitCausesConfusionTime = 0L;
    public int changeDirectionMinTime = 1000;
    public int changeDirectionMaxTime = 3000;
    public int minCircleRadius = 64;
    public int maxCircleRadius = 192;
    protected int changeDirectionTimer;
    protected Point2D.Float nextDirection;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onWasHit(e -> {
            if (this.hitsCausesConfusionChance > 0.0f && this.nextHitCausesConfusionTime <= mob.getTime() && GameRandom.globalRandom.getChance(this.hitsCausesConfusionChance)) {
                Point2D.Float hitDir = GameMath.normalize(e.event.knockbackX, e.event.knockbackY);
                Point2D.Float perpDir = GameMath.getPerpendicularDir(hitDir);
                float perpAngle = GameMath.getAngle(perpDir);
                float nextAngle = GameMath.fixAngle(perpAngle + (float)GameRandom.globalRandom.getIntBetween(-45, 45));
                Point2D.Float finalDir = GameMath.getAngleDir(nextAngle);
                if (GameRandom.globalRandom.nextBoolean()) {
                    finalDir = new Point2D.Float(-finalDir.x, -finalDir.y);
                }
                int confusionTime = GameRandom.globalRandom.getIntBetween(750, 1500);
                ConfuseWanderAIEvent event = new ConfuseWanderAIEvent(confusionTime, finalDir);
                this.handleEvent(event);
                this.nextHitCausesConfusionTime = mob.getTime() + (long)confusionTime + (long)this.hitCausesConfusionCooldown;
            }
        });
        blackboard.onEvent("confuseWander", e -> {
            if (e instanceof ConfuseWanderAIEvent) {
                this.handleEvent((ConfuseWanderAIEvent)e);
            } else {
                this.confusionTimer = 5000L;
            }
        });
    }

    protected void handleEvent(ConfuseWanderAIEvent event) {
        this.confusionTimer = event.canDecreaseTimer ? event.confusionTimer : Math.max(this.confusionTimer, event.confusionTimer);
        this.changeDirectionTimer = 0;
        this.nextDirection = event.nextDirection;
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        boolean isConfused;
        if (this.timerIsBasedOnWorld) {
            isConfused = mob.getTime() >= this.confusionTimer;
        } else {
            isConfused = this.confusionTimer > 0L;
            this.confusionTimer -= 50L;
        }
        if (isConfused) {
            this.changeDirectionTimer -= 50;
            Point2D.Float dir = this.nextDirection;
            if (this.collisionCausesDirectionChange && ((Mob)mob).isAccelerating()) {
                Point2D.Float moveDir = GameMath.normalize(((Mob)mob).moveX, ((Mob)mob).moveY);
                int range = 10;
                Rectangle collision = ((Mob)mob).getCollision((int)(((Mob)mob).x + moveDir.x * (float)range), (int)(((Mob)mob).y + moveDir.y * (float)range));
                if (((Entity)mob).getLevel().collides((Shape)collision, ((Mob)mob).getLevelCollisionFilter())) {
                    float lastMoveAngle = GameMath.getAngle(moveDir);
                    float newMoveAngle = GameMath.fixAngle(lastMoveAngle + (float)GameRandom.globalRandom.getIntBetween(135, 225));
                    dir = GameMath.getAngleDir(newMoveAngle);
                    this.changeDirectionTimer = 0;
                }
            }
            if (this.changeDirectionTimer <= 0 || !blackboard.mover.isCurrentlyMovingFor(this)) {
                this.changeDirectionTimer = GameRandom.globalRandom.getIntBetween(this.changeDirectionMinTime, this.changeDirectionMaxTime);
                this.changeMovement(mob, blackboard, dir);
            }
            return AINodeResult.SUCCESS;
        }
        if (blackboard.mover.isCurrentlyMovingFor(this)) {
            blackboard.mover.stopMoving((Mob)mob);
        }
        return AINodeResult.FAILURE;
    }

    protected void changeMovement(T mob, Blackboard<T> blackboard, Point2D.Float dir) {
        Point2D.Float centerDir;
        if (dir == null && ((Mob)mob).isAccelerating()) {
            Point2D.Float moveDir = GameMath.normalize(((Mob)mob).moveX, ((Mob)mob).moveY);
            float moveAngle = GameMath.getAngle(moveDir);
            float nextAngle = GameMath.fixAngle(moveAngle + (float)GameRandom.globalRandom.getIntBetween(90, 270));
            dir = GameMath.getAngleDir(nextAngle);
        }
        int circleRadius = GameRandom.globalRandom.getIntBetween(this.minCircleRadius, this.maxCircleRadius);
        boolean reversed = GameRandom.globalRandom.nextBoolean();
        if (dir != null) {
            centerDir = GameMath.getPerpendicularDir(dir);
            if (reversed) {
                centerDir = new Point2D.Float(-centerDir.x, -centerDir.y);
            }
        } else {
            centerDir = GameMath.getAngleDir(GameRandom.globalRandom.nextInt(360));
        }
        Point2D.Float centerPos = new Point2D.Float(((Mob)mob).x + centerDir.x * (float)circleRadius, ((Mob)mob).y + centerDir.y * (float)circleRadius);
        float speed = MobMovementCircle.convertToRotSpeed(circleRadius, ((Mob)mob).getSpeed());
        blackboard.mover.setCustomMovement(this, new MobMovementCircleLevelPos((Mob)mob, centerPos.x, centerPos.y, circleRadius, speed, reversed));
    }
}

