/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.Entity;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.composites.SelectorAINode;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanChillingAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanCommandFollowMobAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanCommandMoveToAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanDoJobAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanInteractWithSettlerAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanInteractingAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanJobMoveToAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanJobSearchingAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanMoveToAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanSleepAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.HumanWanderHomeLowHealthAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WanderHomeAtConditionAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.WandererAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.HumanTargetFinderAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ItemAttackerChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.util.WandererBaseOptions;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.SettlerMission;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class HumanAI<T extends HumanMob>
extends SelectorAINode<T> {
    public final HumanMoveToAINode<T> humanMoveToAINode;
    public final HumanInteractingAINode<T> humanInteractingAINode;
    public final HumanWanderHomeLowHealthAINode<T> wanderHomeLowHealthAINode;
    public final HumanTargetFinderAI<T> humanTargetFinderAI;
    public final ItemAttackerChaserAINode<T> chaserAINode;
    public final HumanJobSearchingAINode<T> humanJobSearchingAINode;
    public final HumanJobMoveToAINode<T> humanJobMoveToAINode;
    public final HumanDoJobAINode<T> humanDoJobAINode;
    public final WanderHomeAtConditionAINode<T> wanderHomeAtRaidAINode;
    public final WandererAINode<T> wandererAINode;

    public HumanAI(int searchDistance, boolean attackHostiles, boolean ignoreHiding, int wanderFrequency) {
        this.addChild(new AINode<T>(){

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                SettlerMission currentMission = ((HumanMob)mob).getCurrentMission();
                if (currentMission != null && currentMission.isMobIdle((HumanMob)mob)) {
                    if (blackboard.mover.isMoving()) {
                        blackboard.mover.stopMoving((Mob)mob);
                    }
                    return AINodeResult.RUNNING;
                }
                return AINodeResult.FAILURE;
            }
        });
        this.humanInteractingAINode = new HumanInteractingAINode();
        this.addChild(this.humanInteractingAINode);
        this.humanMoveToAINode = new HumanMoveToAINode();
        this.addChild(this.humanMoveToAINode);
        this.addChild(new AINode<T>(){

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
                blackboard.onGlobalTick(e -> {
                    if (mob.isSettlerOnCurrentLevel() && ((Boolean)mob.hideOnLowHealth.get()).booleanValue()) {
                        float healthPercent = (float)mob.getHealth() / (float)mob.getMaxHealth();
                        if (healthPercent <= 0.4f) {
                            if (!mob.isHiding && mob.home != null && mob.estimateCanMoveTo(mob.home.x, mob.home.y, true)) {
                                mob.clearCommandsOrders(null);
                                mob.cancelJob();
                                mob.isHiding = true;
                            }
                        } else if (healthPercent >= 0.8f) {
                            mob.isHiding = false;
                        }
                    } else {
                        mob.isHiding = false;
                    }
                });
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                return AINodeResult.FAILURE;
            }
        });
        this.wanderHomeLowHealthAINode = new HumanWanderHomeLowHealthAINode();
        this.addChild(this.wanderHomeLowHealthAINode);
        this.addChild(new HumanCommandMoveToAINode(true));
        SequenceAINode chaserSequence = new SequenceAINode();
        this.humanTargetFinderAI = new HumanTargetFinderAI(searchDistance, attackHostiles, ignoreHiding);
        chaserSequence.addChild(this.humanTargetFinderAI);
        this.chaserAINode = new ItemAttackerChaserAINode();
        chaserSequence.addChild(this.chaserAINode);
        chaserSequence.addChild(new AINode<T>(){

            @Override
            protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            }

            @Override
            public void init(T mob, Blackboard<T> blackboard) {
            }

            @Override
            public AINodeResult tick(T mob, Blackboard<T> blackboard) {
                Mob target = blackboard.getObject(Mob.class, "chaserTarget");
                if (target != null) {
                    LocalMessage activityDescription = new LocalMessage("activities", "attacking", "target", target.getLocalization());
                    ((HumanMob)mob).setActivity("chaser", 20000, activityDescription);
                } else {
                    ((HumanMob)mob).clearActivity("chaser");
                }
                return AINodeResult.SUCCESS;
            }
        });
        this.addChild(chaserSequence);
        this.addChild(new HumanCommandFollowMobAINode());
        this.addChild(new HumanCommandMoveToAINode(false));
        SequenceAINode jobSequence = new SequenceAINode();
        this.humanJobSearchingAINode = new HumanJobSearchingAINode();
        jobSequence.addChild(this.humanJobSearchingAINode);
        this.humanJobMoveToAINode = new HumanJobMoveToAINode();
        jobSequence.addChild(this.humanJobMoveToAINode);
        this.humanDoJobAINode = new HumanDoJobAINode();
        jobSequence.addChild(this.humanDoJobAINode);
        this.addChild(jobSequence);
        this.wanderHomeAtRaidAINode = new WanderHomeAtConditionAINode<T>(){

            @Override
            public boolean shouldGoHome(T mob) {
                if (((HumanMob)mob).isVisitor()) {
                    return false;
                }
                NetworkSettlementData settlement = ((HumanMob)mob).getSettlerSettlementNetworkData();
                return settlement != null && settlement.isRaidActive();
            }

            @Override
            public Point getHomeTile(T mob) {
                if (!mob.isSettlerOnCurrentLevel()) {
                    return null;
                }
                return ((HumanMob)mob).home;
            }

            @Override
            public boolean isHomeRoom(T mob) {
                return mob.isSettlerOnCurrentLevel();
            }

            @Override
            public boolean isHomeHouse(T mob) {
                return true;
            }
        };
        this.addChild(this.wanderHomeAtRaidAINode);
        this.addChild(new HumanSleepAINode());
        this.addChild(new HumanInteractWithSettlerAINode());
        this.addChild(new HumanChillingAINode());
        this.wandererAINode = new WandererAINode<T>(wanderFrequency){

            @Override
            public WandererBaseOptions<T> getBaseOptions() {
                return new WandererBaseOptions<T>(){

                    @Override
                    public Point getBaseTile(T mob) {
                        ServerSettlementData serverData;
                        if (((HumanMob)mob).isSettler() && !mob.isSettlerOnCurrentLevel()) {
                            return null;
                        }
                        if (((HumanMob)mob).isOnStrike() && (serverData = ((HumanMob)mob).getSettlerSettlementServerData()) != null && serverData.networkData.isRaidActive()) {
                            return serverData.getFlagTile();
                        }
                        return ((HumanMob)mob).home;
                    }

                    @Override
                    public int getBaseRadius(T mob, WandererAINode<T> node) {
                        if (((HumanMob)mob).isOnStrike()) {
                            return 5;
                        }
                        return HumanAI.this.wandererAINode.searchRadius;
                    }

                    @Override
                    public boolean forceFindAroundBase(T mob) {
                        return ((HumanMob)mob).isOnStrike();
                    }

                    @Override
                    public boolean isBaseHouse(T mob, Point base) {
                        return true;
                    }

                    @Override
                    public boolean isBaseRoom(T mob, Point base) {
                        return mob.isSettlerOnCurrentLevel();
                    }
                };
            }
        };
        this.wandererAINode.runAwayFromAttacker = true;
        this.wandererAINode.runAwayFromAttackerToBase = true;
        this.wandererAINode.hideInside = mob -> mob.getWorldEntity().isNight() || mob.isHiding;
        this.wandererAINode.getZoneTester = mob -> {
            if (mob.levelSettler != null) {
                return mob.levelSettler.isTileInSettlementBoundsAndRestrictZoneTester();
            }
            return null;
        };
        this.addChild(this.wandererAINode);
    }

    public boolean shootSimpleProjectile(T mob, Mob target, String projectileID, int projectileDamage, int projectileSpeed, int projectileDistance) {
        return this.shootSimpleProjectile(mob, target, projectileID, DamageTypeRegistry.NORMAL, projectileDamage, projectileSpeed, projectileDistance);
    }

    public boolean shootSimpleProjectile(T mob, Mob target, String projectileID, DamageType damageType, int projectileDamage, int projectileSpeed, int projectileDistance) {
        if (((HumanMob)mob).canAttack()) {
            ((AttackAnimMob)mob).attack(target.getX(), target.getY(), false);
            GameDamage damage = new GameDamage(damageType, (float)projectileDamage);
            Projectile projectile = ProjectileRegistry.getProjectile(projectileID, ((Entity)mob).getLevel(), ((HumanMob)mob).x, ((HumanMob)mob).y, target.x, target.y, (float)projectileSpeed, projectileDistance, damage, mob);
            projectile.setTargetPrediction(target, -10.0f);
            projectile.moveDist(10.0);
            ((Entity)mob).getLevel().entityManager.projectiles.add(projectile);
            return true;
        }
        return false;
    }
}

