/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.trees;

import java.awt.Point;
import necesse.engine.util.gameAreaSearch.GameAreaStream;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.composites.SequenceAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.entity.mobs.ai.behaviourTree.util.TargetFinderDistance;
import necesse.entity.mobs.hostile.pirates.PirateMob;

public class PirateChaserAI<T extends PirateMob>
extends SequenceAINode<T> {
    public final CooldownAttackTargetAINode<T> shootAtTargetNode;
    public final TargetFinderAINode<T> targetFinderNode;
    public final ChaserAINode<T> chaserNode;

    public PirateChaserAI(int shootDistance, int shootCooldown, int meleeDistance, int searchDistance) {
        if (shootDistance > 0) {
            this.shootAtTargetNode = new CooldownAttackTargetAINode<T>(CooldownAttackTargetAINode.CooldownTimer.TICK, shootCooldown, shootDistance){

                @Override
                public boolean attackTarget(T mob, Mob target) {
                    if (((Mob)mob).canAttack()) {
                        ((PirateMob)mob).startShootingAbility.runAndSend(target);
                        return true;
                    }
                    return false;
                }
            };
            this.addChild(this.shootAtTargetNode);
        } else {
            this.shootAtTargetNode = null;
        }
        TargetFinderDistance targetFinder = new TargetFinderDistance(searchDistance);
        targetFinder.targetLostAddedDistance = searchDistance * 2;
        this.targetFinderNode = new TargetFinderAINode<T>(targetFinder){

            @Override
            public GameAreaStream<? extends Mob> streamPossibleTargets(T mob, Point base, TargetFinderDistance<T> distance) {
                return TargetFinderAINode.streamPlayersAndHumans(mob, base, distance);
            }
        };
        this.addChild(this.targetFinderNode);
        this.chaserNode = new ChaserAINode<T>(meleeDistance, false, true){

            @Override
            public boolean attackTarget(T mob, Mob target) {
                if (((Mob)mob).canAttack()) {
                    ((AttackAnimMob)mob).attack(target.getX(), target.getY(), false);
                    target.isServerHit(new GameDamage(((PirateMob)mob).meleeDamage), target.x - ((PirateMob)mob).x, target.y - ((PirateMob)mob).y, 100.0f, (Attacker)mob);
                    return true;
                }
                return false;
            }
        };
        this.addChild(this.chaserNode);
    }
}

