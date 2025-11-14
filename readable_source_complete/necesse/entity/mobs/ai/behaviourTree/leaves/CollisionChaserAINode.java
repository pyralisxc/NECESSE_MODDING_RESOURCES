/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.function.BiPredicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.ai.behaviourTree.util.MoveToTileAITask;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class CollisionChaserAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public BiPredicate<Mob, MoveToTileAITask.AIPathResult> moveIfFailedPath;
    public String targetKey;
    public long nextPathFindTime;
    public boolean pathIfStopped;
    public int directSearchDistance;
    public int stoppingDistance;
    public MobHitCooldowns hitCooldowns = new MobHitCooldowns();
    public String chaserTargetKey = "chaserTarget";
    public int attackMoveCooldown = 1000;
    public float attacksCausesConfusionChance = 0.5f;
    protected long moveCooldownTimer;
    protected boolean movingToTarget = false;

    public CollisionChaserAINode(String targetKey) {
        this.targetKey = targetKey;
    }

    public CollisionChaserAINode() {
        this("currentTarget");
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        if (this.directSearchDistance == 0) {
            int pathMoveDistance = (int)((Mob)mob).getPathMoveOffset().distance(0.0, 0.0);
            this.directSearchDistance = pathMoveDistance * 8 + 32;
        }
        blackboard.onEvent("resetPathTime", e -> {
            this.nextPathFindTime = 0L;
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
        blackboard.put(this.chaserTargetKey, null);
    }

    public static boolean simpleAttack(Mob mob, Mob target, GameDamage damage, int knockback) {
        if (damage == null || !target.canBeHit(mob)) {
            return false;
        }
        Mob from = mob;
        Mob followingMob = mob.getFollowingMob();
        if (followingMob != null) {
            from = followingMob;
        }
        target.isServerHit(damage, target.x - from.x, target.y - from.y, knockback, mob);
        return true;
    }

    public abstract boolean attackTarget(T var1, Mob var2);

    @Override
    public AINodeResult tick(T mob, Blackboard<T> blackboard) {
        Mob target = blackboard.getObject(Mob.class, this.targetKey);
        if (target != null && target.getCollision().intersects(((Mob)mob).getCollision()) && this.hitCooldowns.canHit(target) && ((Mob)mob).canAttack()) {
            if (this.attacksCausesConfusionChance > 0.0f && GameRandom.globalRandom.getChance(this.attacksCausesConfusionChance)) {
                blackboard.mover.stopMoving((Mob)mob);
                Point2D.Float hitDir = GameMath.normalize(target.x - ((Mob)mob).x, target.y - ((Mob)mob).y);
                Point2D.Float perpDir = GameMath.getPerpendicularDir(hitDir);
                float perpAngle = GameMath.getAngle(perpDir);
                float nextAngle = GameMath.fixAngle(perpAngle + (float)GameRandom.globalRandom.getIntBetween(-45, 45));
                Point2D.Float finalDir = GameMath.getAngleDir(nextAngle);
                if (GameRandom.globalRandom.nextBoolean()) {
                    finalDir = new Point2D.Float(-finalDir.x, -finalDir.y);
                }
                int confusionTime = GameRandom.globalRandom.getIntBetween(500, 1000);
                ConfuseWanderAIEvent event = new ConfuseWanderAIEvent(confusionTime, finalDir);
                blackboard.submitEvent("confuseWander", event);
            }
            if (this.attackMoveCooldown > 0) {
                blackboard.mover.stopMoving((Mob)mob);
                this.moveCooldownTimer = mob.getTime() + (long)this.attackMoveCooldown;
            }
            if (this.attackTarget(mob, target)) {
                this.hitCooldowns.startCooldown(target);
            }
        }
        return super.tick(mob, blackboard);
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        boolean isOnMoveCooldown;
        Mob target = blackboard.getObject(Mob.class, this.targetKey);
        boolean bl = isOnMoveCooldown = this.moveCooldownTimer >= mob.getTime();
        if (target != null && !isOnMoveCooldown) {
            float dist = ((Mob)mob).getDistance(target);
            if ((dist < (float)this.directSearchDistance || dist < (float)this.stoppingDistance) && !((Entity)mob).getLevel().collides((Shape)new MovedRectangle((Mob)mob, target.getX(), target.getY()), ((Mob)mob).modifyChasingCollisionFilter(((Mob)mob).getLevelCollisionFilter(), target))) {
                if (dist < (float)this.stoppingDistance) {
                    blackboard.mover.stopMoving((Mob)mob);
                } else {
                    blackboard.mover.setMobTarget(this, target, true);
                }
                this.nextPathFindTime = 0L;
                this.movingToTarget = true;
            } else if (this.nextPathFindTime <= ((Entity)mob).getWorldEntity().getLocalTime() || this.pathIfStopped && (((Mob)mob).hasArrivedAtTarget() || !blackboard.mover.isMoving())) {
                this.pathIfStopped = false;
                return this.moveToTileTask(target.getTileX(), target.getTileY(), null, path -> {
                    if (path.moveIfWithin(-1, this.moveIfFailedPath != null && this.moveIfFailedPath.test((Mob)mob, (MoveToTileAITask.AIPathResult)path) ? -1 : 1, () -> {
                        this.movingToTarget = false;
                        this.nextPathFindTime = 0L;
                    })) {
                        if (path.isResultWithin(1)) {
                            int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 500, 0.1f);
                            this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                            this.pathIfStopped = true;
                        } else {
                            int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 5000, 0.1f);
                            this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                        }
                        this.movingToTarget = true;
                    } else {
                        this.movingToTarget = false;
                        this.nextPathFindTime = mob.getWorldEntity().getLocalTime() + (long)(1000 * (GameRandom.globalRandom.nextInt(5) + 3));
                    }
                    return this.lastTick(this.movingToTarget, this.moveCooldownTimer >= mob.getTime(), target);
                });
            }
        } else {
            this.movingToTarget = false;
        }
        return this.lastTick(this.movingToTarget, isOnMoveCooldown, target);
    }

    protected AINodeResult lastTick(boolean movingToTarget, boolean isOnMoveCooldown, Mob target) {
        if (movingToTarget) {
            this.getBlackboard().put(this.chaserTargetKey, target);
            return AINodeResult.SUCCESS;
        }
        return isOnMoveCooldown ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
    }

    @Override
    public void addDebugTooltips(ListGameTooltips tooltips) {
        super.addDebugTooltips(tooltips);
        tooltips.add("movingToTarget: " + this.movingToTarget);
        tooltips.add("nextPathFindTime: " + (this.nextPathFindTime - ((Entity)this.mob()).getWorldEntity().getLocalTime()));
        tooltips.add("pathIfStopped: " + this.pathIfStopped);
    }
}

