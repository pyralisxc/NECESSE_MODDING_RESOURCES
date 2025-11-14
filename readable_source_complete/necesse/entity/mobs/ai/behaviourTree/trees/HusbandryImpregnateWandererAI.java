/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.EscapeAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.friendly.FriendlyRopableMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.misc.ProcessMobHandler;
import necesse.entity.mobs.mobMovement.MobMovement;

public class HusbandryImpregnateWandererAI<T extends HusbandryMob>
extends SelectorAINode<T> {
    public final HusbandryImpregnateAINode<T> impregnateAINode;
    public final EscapeAINode<T> escapeAINode;
    public final FollowerAINode<T> ropeFollowerAINode = new FollowerAINode<T>(320, 64){

        @Override
        public Mob getFollowingMob(T mob) {
            return ((FriendlyRopableMob)mob).getRopeMob();
        }
    };
    public final WandererAINode<T> wandererAINode;

    public HusbandryImpregnateWandererAI(int wanderFrequency) {
        this.addChild(this.ropeFollowerAINode);
        this.escapeAINode = new EscapeAINode<T>(){

            @Override
            public boolean shouldEscape(T mob, Blackboard<T> blackboard) {
                return ((HusbandryMob)mob).shouldEscape;
            }
        };
        this.addChild(this.escapeAINode);
        this.impregnateAINode = new HusbandryImpregnateAINode();
        this.addChild(this.impregnateAINode);
        this.wandererAINode = new WandererAINode(wanderFrequency);
        this.addChild(this.wandererAINode);
    }

    public static class HusbandryImpregnateAINode<T extends HusbandryMob>
    extends MoveTaskAINode<T> {
        public long nextCheckTime;
        public ProcessMobHandler<HusbandryMob, HusbandryMob> target;
        public MoveToTarget moveToTarget;
        public long nextMoveTime;
        public int pathsSinceProgress;
        public double lastPathDistToTarget;

        @Override
        protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
        }

        @Override
        public void init(T mob, Blackboard<T> blackboard) {
        }

        @Override
        public AINodeResult tick(T mob, Blackboard<T> blackboard) {
            if (this.target != null) {
                this.target.tickReserve();
            }
            return super.tick(mob, blackboard);
        }

        @Override
        public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
            if (((HusbandryMob)mob).isImpregnating() || ((HusbandryMob)mob).isReservedForImpregnating()) {
                blackboard.mover.stopMoving((Mob)mob);
                return AINodeResult.SUCCESS;
            }
            if (this.target != null && this.target.isValid()) {
                MoveToTarget last = this.moveToTarget;
                this.moveToTarget = this.target.getMoveToTile();
                if (this.moveToTarget == null || ((Mob)mob).collidesWith((Mob)this.target.target)) {
                    blackboard.mover.stopMoving((Mob)mob);
                    this.target.completeProgress();
                    this.target = null;
                    return AINodeResult.FAILURE;
                }
                if (this.moveToTarget.custom != null) {
                    blackboard.mover.setCustomMovement(this, this.moveToTarget.custom);
                    this.pathsSinceProgress = 0;
                    return AINodeResult.RUNNING;
                }
                if (!this.moveToTarget.equals(last)) {
                    this.nextMoveTime = 0L;
                }
                long currentTime = mob.getLocalTime();
                if (this.pathsSinceProgress > 5) {
                    blackboard.mover.stopMoving((Mob)mob);
                    this.target = null;
                    return AINodeResult.FAILURE;
                }
                if (this.nextMoveTime <= currentTime) {
                    this.nextMoveTime = currentTime + 5000L;
                    if (((Mob)mob).estimateCanMoveTo(this.moveToTarget.tileX, this.moveToTarget.tileY, false)) {
                        MoveToTarget temp = this.moveToTarget;
                        return this.moveToTileTask(temp.tileX, temp.tileY, null, path -> {
                            Point lastNode = path.result.getLastPathResult();
                            if (lastNode == null) {
                                ++this.pathsSinceProgress;
                            } else {
                                double pathDistToTarget = lastNode.distance((Point2D)path.result.target);
                                if (this.lastPathDistToTarget < 0.0 || pathDistToTarget == 0.0 || pathDistToTarget < this.lastPathDistToTarget) {
                                    this.pathsSinceProgress = 0;
                                    this.lastPathDistToTarget = pathDistToTarget;
                                } else {
                                    ++this.pathsSinceProgress;
                                }
                            }
                            if (path.moveIfWithin(-1, -1, () -> {
                                this.nextMoveTime = 0L;
                            })) {
                                int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 5000, 0.1f);
                                this.nextMoveTime = mob.getWorldEntity().getLocalTime() + (long)nextPathTimeAdd;
                            }
                            return AINodeResult.RUNNING;
                        });
                    }
                    ++this.pathsSinceProgress;
                }
                return AINodeResult.SUCCESS;
            }
            this.target = null;
            if (((HusbandryMob)mob).canImpregnate() && !((HusbandryMob)mob).isImpregnating() && !((HusbandryMob)mob).isOnBirthingCooldown() && this.nextCheckTime <= mob.getTime()) {
                this.nextCheckTime = mob.getTime() + (long)GameRandom.globalRandom.getIntBetween(20, 30) * 1000L;
                List<HusbandryMob> nearbyMobs = ((HusbandryMob)mob).getNearbyHusbandryMobs();
                if (nearbyMobs.size() <= HusbandryMob.maxCloseMobsToBirth) {
                    ArrayList<HusbandryMob> validImpregnateMobs = new ArrayList<HusbandryMob>(nearbyMobs.size());
                    for (HusbandryMob other : nearbyMobs) {
                        if (!other.canBirth() || other.isOnBirthingCooldown() || !((HusbandryMob)mob).canImpregnateMob(other) || !((Mob)mob).estimateCanMoveTo(other.getTileX(), other.getTileY(), false) || other.impregnateReservable != null && !other.impregnateReservable.isAvailable((Entity)mob)) continue;
                        validImpregnateMobs.add(other);
                    }
                    if (!validImpregnateMobs.isEmpty()) {
                        HusbandryMob impregnateMob = (HusbandryMob)GameRandom.globalRandom.getOneOf(validImpregnateMobs);
                        this.target = new ProcessMobHandler<HusbandryMob, HusbandryMob>(mob, impregnateMob, impregnateMob.impregnateReservable){

                            @Override
                            public boolean isValid() {
                                return !((HusbandryMob)this.target).removed() && ((HusbandryMob)this.target).canBirth() && ((HusbandryMob)this.mob).canImpregnate() && ((HusbandryMob)this.mob).canImpregnateMob((HusbandryMob)this.target);
                            }

                            @Override
                            public void completeProgress() {
                                ((HusbandryMob)this.mob).impregnateMobAbility.runAndSend(this);
                            }

                            @Override
                            public int getTimeItTakesInMilliseconds() {
                                return 10000;
                            }
                        };
                        this.target.tickReserve();
                        return AINodeResult.SUCCESS;
                    }
                }
            }
            return AINodeResult.FAILURE;
        }
    }

    public static class MoveToTarget {
        public final MobMovement custom;
        public final int tileX;
        public final int tileY;

        public MoveToTarget(int tileX, int tileY) {
            this.custom = null;
            this.tileX = tileX;
            this.tileY = tileY;
        }

        public MoveToTarget(MobMovement custom) {
            this.custom = custom;
            this.tileX = -1;
            this.tileY = -1;
        }

        public boolean equals(MoveToTarget other) {
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (!Objects.equals(this.custom, other.custom)) {
                return false;
            }
            if (this.custom != null) {
                return true;
            }
            return this.tileX == other.tileX && this.tileY == other.tileY;
        }

        public boolean equals(Object obj) {
            if (obj instanceof MoveToTarget) {
                return this.equals((MoveToTarget)obj);
            }
            return super.equals(obj);
        }
    }
}

