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
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.event.AIWasHitEvent;
import necesse.entity.mobs.ai.behaviourTree.event.TargetAIEvent;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.ai.behaviourTree.util.TargetValidity;

public abstract class TargetFinderAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public TargetValidity<T> validity;
    public TargetFinderDistance<T> distance;
    public String baseKey = "mobBase";
    public boolean baseKeyIsJustPreference;
    public int targetNonBaseWithinRange;
    public String focusTargetKey;
    public String currentTargetKey;
    public String newTargetFoundEventType = "newTargetFound";
    public String lastTargetInvalidEventType = "lastTargetInvalid";
    public boolean moveToAttacker = true;
    public boolean runOnGlobalTick = false;
    public boolean canRegainSameTargetIfLostToTimer = true;
    public int loseTargetTimer;
    public int noTargetFoundTimer;
    public int loseTargetMinCooldown = 3000;
    public int loseTargetMaxCooldown = 6000;
    public int noTargetFoundMinCooldown = 2000;
    public int noTargetFoundMaxCooldown = 4000;
    public final String resetTargetEvent = "resetTarget";

    public TargetFinderAINode(TargetFinderDistance<T> distance, TargetValidity<T> validity, String focusTargetKey, String currentTargetKey) {
        this.distance = distance;
        this.validity = validity;
        this.focusTargetKey = focusTargetKey;
        this.currentTargetKey = currentTargetKey;
        this.startLoseTargetTimer();
    }

    public TargetFinderAINode(TargetFinderDistance<T> distance, TargetValidity<T> validity) {
        this(distance, validity, "focusTarget", "currentTarget");
    }

    public TargetFinderAINode(TargetFinderDistance<T> distance) {
        this(distance, new TargetValidity());
    }

    public TargetFinderAINode(int searchDistance) {
        this(new TargetFinderDistance(searchDistance));
    }

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        blackboard.onGlobalTick(e -> {
            if (this.runOnGlobalTick) {
                this.tickTargetFinder(mob, blackboard);
            }
        });
        blackboard.onWasHit(e -> {
            this.noTargetFoundTimer = 0;
        });
        blackboard.onEvent("resetTarget", e -> {
            this.noTargetFoundTimer = 0;
            this.loseTargetTimer = 0;
            blackboard.put(this.currentTargetKey, null);
            blackboard.submitEvent(this.newTargetFoundEventType, new TargetAIEvent(null));
        });
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AINodeResult tickTargetFinder(T mob, Blackboard<T> blackboard) {
        Mob lastTarget;
        Mob newTarget = lastTarget = blackboard.getObject(Mob.class, this.currentTargetKey);
        boolean addTargetLostDistance = false;
        int lastSearchDistance = this.distance.searchDistance;
        try {
            Mob focusTarget;
            Point baseKeyObject;
            Point base;
            if (newTarget == null && this.noTargetFoundTimer >= 0) {
                this.noTargetFoundTimer -= 50;
                AINodeResult aINodeResult = AINodeResult.FAILURE;
                return aINodeResult;
            }
            Mob lostTargetToTimer = null;
            if (newTarget != null && this.loseTargetMinCooldown >= 0 && this.loseTargetMaxCooldown >= 0) {
                this.loseTargetTimer -= 50;
                if (this.loseTargetTimer <= 0) {
                    lostTargetToTimer = newTarget;
                    newTarget = null;
                    addTargetLostDistance = true;
                    this.startLoseTargetTimer();
                }
            }
            Point point = base = (baseKeyObject = this.getBase(mob, blackboard)) != null ? baseKeyObject : new Point(((Entity)mob).getX(), ((Entity)mob).getY());
            if (newTarget != null && (newTarget.removed() || !this.validity.isValidTarget(this, mob, newTarget, false) || this.distance.getDistance(base, newTarget) > (float)this.distance.getTargetLostDistance(mob, newTarget))) {
                blackboard.submitEvent(this.lastTargetInvalidEventType, new TargetAIEvent(newTarget));
                newTarget = null;
                addTargetLostDistance = true;
            }
            if (addTargetLostDistance) {
                this.distance.searchDistance += this.distance.targetLostAddedDistance;
            }
            if ((focusTarget = blackboard.getObject(Mob.class, this.focusTargetKey)) != null && newTarget != focusTarget && this.validity.isValidTarget(this, mob, focusTarget, true) && this.distance.getDistance(base, focusTarget) < (float)this.distance.getSearchDistance(mob, focusTarget) && ((Mob)mob).estimateCanMoveTo(focusTarget.getTileX(), focusTarget.getTileY(), focusTarget.canBeTargetedFromAdjacentTiles())) {
                newTarget = focusTarget;
            }
            if (newTarget == null) {
                if (baseKeyObject != null && this.baseKeyIsJustPreference && this.targetNonBaseWithinRange > 0) {
                    GameAreaStream<Mob> baseTargetStream;
                    GameAreaStream<Mob> meTargetStream = this.streamPossibleTargets(mob, new Point(((Entity)mob).getX(), ((Entity)mob).getY()), this.distance);
                    if (meTargetStream != null) {
                        newTarget = meTargetStream.filter(m -> this.validity.isValidTarget(this, (Mob)mob, (Mob)m, true) && mob.getDistance((Mob)m) <= (float)this.targetNonBaseWithinRange * this.distance.getSearchDistanceMod((Mob)mob, (Mob)m)).filter(m -> mob.estimateCanMoveTo(m.getTileX(), m.getTileY(), m.canBeTargetedFromAdjacentTiles())).findBestDistance(0, Comparator.comparingDouble(arg_0 -> mob.getDistance(arg_0))).orElse(null);
                    }
                    if (newTarget == null && (baseTargetStream = this.streamPossibleTargets(mob, base, this.distance)) != null) {
                        newTarget = baseTargetStream.filter(m -> this.validity.isValidTarget(this, (Mob)mob, (Mob)m, true) && this.distance.getDistance(base, (Mob)m) < (float)this.distance.getSearchDistance((Mob)mob, (Mob)m)).filter(m -> mob.estimateCanMoveTo(m.getTileX(), m.getTileY(), m.canBeTargetedFromAdjacentTiles())).findBestDistance(0, Comparator.comparingDouble(m -> this.distance.getDistance(base, (Mob)m))).orElse(null);
                    }
                } else {
                    GameAreaStream<Mob> targetStream = this.streamPossibleTargets(mob, base, this.distance);
                    if (targetStream != null) {
                        newTarget = targetStream.filter(m -> this.validity.isValidTarget(this, (Mob)mob, (Mob)m, true) && this.distance.getDistance(base, (Mob)m) < (float)this.distance.getSearchDistance((Mob)mob, (Mob)m)).filter(m -> mob.estimateCanMoveTo(m.getTileX(), m.getTileY(), m.canBeTargetedFromAdjacentTiles())).findBestDistance(0, Comparator.comparingDouble(arg_0 -> mob.getDistance(arg_0))).orElse(null);
                    }
                }
            }
            if (this.canRegainSameTargetIfLostToTimer && newTarget == null && lostTargetToTimer != null && !lostTargetToTimer.removed() && this.validity.isValidTarget(this, mob, lostTargetToTimer, false) && this.distance.getDistance(base, lostTargetToTimer) <= (float)this.distance.getTargetLostDistance(mob, lostTargetToTimer)) {
                newTarget = lostTargetToTimer;
            }
            if (newTarget == null && this.noTargetFoundMinCooldown >= 0 && this.noTargetFoundMaxCooldown >= 0) {
                this.startNoTargetFoundCooldown();
            }
            if (lastTarget != newTarget) {
                blackboard.put(this.currentTargetKey, newTarget);
                blackboard.submitEvent(this.newTargetFoundEventType, new TargetAIEvent(newTarget));
            }
            AINodeResult aINodeResult = newTarget != null ? AINodeResult.SUCCESS : AINodeResult.FAILURE;
            return aINodeResult;
        }
        finally {
            this.distance.searchDistance = lastSearchDistance;
        }
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        Mob lastTarget = blackboard.getObject(Mob.class, this.currentTargetKey);
        if (this.moveToAttacker && lastTarget == null) {
            for (AIWasHitEvent e : blackboard.getLastHits()) {
                Point base;
                Mob owner = e.event.attacker != null ? e.event.attacker.getAttackOwner() : null;
                Point baseKeyObject = this.getBase(mob, blackboard);
                if (owner == null || !this.validity.isValidTarget(this, mob, owner, true) || !((Mob)mob).estimateCanMoveTo(owner.getTileX(), owner.getTileY(), owner.canBeTargetedFromAdjacentTiles())) continue;
                Point point = base = baseKeyObject != null ? baseKeyObject : new Point(((Entity)mob).getX(), ((Entity)mob).getY());
                if (baseKeyObject != null && this.distance.getDistance(base, owner) > (float)this.distance.getTargetLostDistance(mob, owner)) continue;
                return this.moveToTileTask(owner.getTileX(), owner.getTileY(), null, path -> {
                    path.moveIfWithin(-1, 1, null);
                    Mob currentTarget = blackboard.getObject(Mob.class, this.currentTargetKey);
                    if (currentTarget != owner) {
                        blackboard.put(this.currentTargetKey, owner);
                        blackboard.submitEvent(this.newTargetFoundEventType, new TargetAIEvent(owner));
                    }
                    return AINodeResult.SUCCESS;
                });
            }
        }
        if (!this.runOnGlobalTick) {
            return this.tickTargetFinder(mob, blackboard);
        }
        return AINodeResult.SUCCESS;
    }

    public Point getBase(T mob, Blackboard<T> blackboard) {
        return blackboard.getObject(Point.class, this.baseKey);
    }

    public void startLoseTargetTimer() {
        this.loseTargetTimer = GameRandom.globalRandom.getIntBetween(this.loseTargetMinCooldown, this.loseTargetMaxCooldown);
    }

    public void startNoTargetFoundCooldown() {
        this.noTargetFoundTimer = GameRandom.globalRandom.getIntBetween(this.noTargetFoundMinCooldown, this.noTargetFoundMaxCooldown);
    }

    public abstract GameAreaStream<? extends Mob> streamPossibleTargets(T var1, Point var2, TargetFinderDistance<T> var3);

    public static <T extends Mob> GameAreaStream<PlayerMob> streamPlayers(T mob, Point base, TargetFinderDistance<T> distance) {
        return distance.streamPlayersInRange(base, mob).filter(m -> m != null && m != mob && !m.removed() && m.isVisible());
    }

    public static <T extends Mob> GameAreaStream<Mob> streamPlayersAndHumans(T mob, Point base, TargetFinderDistance<T> distance) {
        return distance.streamMobsAndPlayersInRange(base, mob).filter(m -> {
            if (m == null || m == mob || m.removed() || !m.isVisible()) {
                return false;
            }
            int team = m.getTeam();
            if (team == -100) {
                return true;
            }
            if (m.isHuman && team != -1) {
                return true;
            }
            return m.isPlayer;
        });
    }
}

