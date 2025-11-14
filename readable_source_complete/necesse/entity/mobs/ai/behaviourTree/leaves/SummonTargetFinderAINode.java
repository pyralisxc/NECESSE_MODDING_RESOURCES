/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import java.util.Comparator;
import necesse.engine.util.GameRandom;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.SummonTargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;

public class SummonTargetFinderAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public TargetFinderDistance<T> distance;
    public String currentTargetKey;
    public int loseTargetTimer;
    public int noTargetFoundTimer;
    public int loseTargetMinCooldown = 2000;
    public int loseTargetMaxCooldown = 4000;

    public SummonTargetFinderAINode(TargetFinderDistance<T> distance, String currentTargetKey) {
        this.distance = distance;
        this.currentTargetKey = currentTargetKey;
        this.startLoseTargetTimer();
    }

    public SummonTargetFinderAINode(TargetFinderDistance<T> distance) {
        this(distance, "currentTarget");
    }

    public SummonTargetFinderAINode(int searchDistance) {
        this(new SummonTargetFinderDistance(searchDistance));
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        Mob lastTarget;
        Mob newTarget = lastTarget = blackboard.getObject(Mob.class, this.currentTargetKey);
        boolean addTargetLostDistance = false;
        int lastSearchDistance = this.distance.searchDistance;
        ItemAttackerMob owner = ((Mob)mob).getFollowingItemAttacker();
        Point base = owner == null ? ((Entity)mob).getPositionPoint() : owner.getPositionPoint();
        Mob focusTarget = blackboard.getObject(Mob.class, "focusTarget");
        try {
            GameAreaStream<Mob> targetStream;
            if (newTarget == null && this.noTargetFoundTimer >= 0) {
                this.noTargetFoundTimer -= 50;
                AINodeResult aINodeResult = AINodeResult.FAILURE;
                return aINodeResult;
            }
            if (newTarget != null && this.loseTargetMinCooldown >= 0 && this.loseTargetMaxCooldown >= 0) {
                this.loseTargetTimer -= 50;
                if (this.loseTargetTimer <= 0) {
                    newTarget = null;
                    addTargetLostDistance = true;
                    this.startLoseTargetTimer();
                }
            }
            if (!(newTarget == null || this.isValidTarget(mob, owner, newTarget, focusTarget) && !(this.distance.getDistance(base, newTarget) > (float)this.distance.getTargetLostDistance(mob, newTarget)) && this.checkCanMoveTo(owner, newTarget))) {
                newTarget = null;
                addTargetLostDistance = true;
            }
            if (addTargetLostDistance) {
                this.distance.searchDistance += this.distance.targetLostAddedDistance;
            }
            if (focusTarget != null && newTarget != focusTarget && this.isValidTarget(mob, owner, focusTarget, focusTarget) && this.distance.getDistance(base, focusTarget) < (float)this.distance.getSearchDistance(mob, focusTarget) && this.checkCanMoveTo(owner, focusTarget)) {
                newTarget = focusTarget;
            }
            if (newTarget == null && (targetStream = this.streamPossibleTargets(owner, mob, base, this.distance)) != null) {
                newTarget = targetStream.filter(m -> this.isValidTarget(mob, owner, (Mob)m, focusTarget) && this.distance.getDistance(base, (Mob)m) < (float)this.distance.getSearchDistance((Mob)mob, (Mob)m)).filter(m -> this.checkCanMoveTo(owner, (Mob)m)).findBestDistance(0, Comparator.comparingInt(m -> (int)mob.getDistance((Mob)m))).orElse(null);
            }
            if (lastTarget != newTarget) {
                blackboard.put(this.currentTargetKey, newTarget);
            }
            AINodeResult aINodeResult = newTarget != null ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
            return aINodeResult;
        }
        finally {
            this.distance.searchDistance = lastSearchDistance;
        }
    }

    public void startLoseTargetTimer() {
        this.loseTargetTimer = GameRandom.globalRandom.getIntBetween(this.loseTargetMinCooldown, this.loseTargetMaxCooldown);
    }

    public GameAreaStream<? extends Mob> streamPossibleTargets(ItemAttackerMob owner, T mob, Point base, TargetFinderDistance<T> distance) {
        if (owner != null && owner.isHostile) {
            return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
        }
        return distance.streamMobsInRange(base, mob).filter(m -> m != null && !m.removed() && m.isVisible()).filter(m -> m.isHostile);
    }

    public boolean isValidTarget(T mob, ItemAttackerMob owner, Mob target, Mob focusTarget) {
        Point base;
        if (target == null || target.removed() || !target.isVisible() || !((Entity)mob).isSamePlace(target) || target != focusTarget && !target.canBeTargeted((Mob)mob, owner == null ? null : owner.getPvPOwner())) {
            return false;
        }
        Point point = base = owner == null ? ((Entity)mob).getPositionPoint() : owner.getPositionPoint();
        if (target.getLevel().getStaticLightLevelFloat(target) <= 0.0f && !target.isBoss() && target.getDistance(base.x, base.y) >= 256.0f) {
            return false;
        }
        boolean hostileOwner = owner != null && owner.isHostile;
        return target.isHostile || hostileOwner || target == focusTarget;
    }

    public boolean checkCanMoveTo(ItemAttackerMob owner, Mob target) {
        if (owner == null) {
            return true;
        }
        return owner.serverFollowersManager.followerTargetCooldowns.canMoveTo(target);
    }
}

