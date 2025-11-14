/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.witches;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.witches.EvilWitchMob;
import necesse.entity.projectile.bulletProjectile.NecroticBoltProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;

public class EvilWitchNecroticBowMob
extends EvilWitchMob {
    private int attacksSinceLastMove = 0;

    public EvilWitchNecroticBowMob() {
        this.attackCooldown = 1500;
        this.attackAnimTime = 600;
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
    }

    @Override
    public void init() {
        super.init();
        ConfusedPlayerChaserWandererAI<EvilWitchNecroticBowMob> aiNode = new ConfusedPlayerChaserWandererAI<EvilWitchNecroticBowMob>(null, 384, 640, 40000, true, false){

            @Override
            public boolean attackTarget(EvilWitchNecroticBowMob mob, Mob target) {
                if (!mob.canAttack() || mob.isAccelerating() || mob.hasCurrentMovement()) {
                    return false;
                }
                GameDamage damage = new GameDamage(20.0f);
                EvilWitchNecroticBowMob.this.attack(target.getX(), target.getY(), false);
                NecroticBoltProjectile projectile = new NecroticBoltProjectile(EvilWitchNecroticBowMob.this.getLevel(), mob, EvilWitchNecroticBowMob.this.x, EvilWitchNecroticBowMob.this.y, target.x, target.y, 100.0f, 800, damage);
                projectile.moveDist(20.0);
                EvilWitchNecroticBowMob.this.getLevel().entityManager.projectiles.add(projectile);
                EvilWitchNecroticBowMob.this.attacksSinceLastMove++;
                if (EvilWitchNecroticBowMob.this.attacksSinceLastMove >= 3) {
                    EvilWitchNecroticBowMob.this.attacksSinceLastMove = 0;
                    EvilWitchNecroticBowMob.this.getLevel().entityManager.events.addHidden(new WaitForSecondsEvent(1.0f){

                        @Override
                        public void onWaitOver() {
                            EvilWitchNecroticBowMob.this.ai.blackboard.submitEvent("chaserMovePosition", new AIEvent());
                        }
                    });
                } else {
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                }
                return true;
            }
        };
        aiNode.playerChaserAI.chaserAINode.moveSearchPositionRange = 256;
        this.ai = new BehaviourTreeAI<EvilWitchNecroticBowMob>(this, aiNode);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isAccelerating()) {
            this.attacksSinceLastMove = 0;
        }
    }

    @Override
    protected void setupWitchDrawOptions(HumanDrawOptions humanDrawOptions) {
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            humanDrawOptions.itemAttack(new InventoryItem("necroticbow"), null, animProgress, this.attackDir.x, this.attackDir.y);
        }
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.bow, (SoundEffect)SoundEffect.effect(this));
        }
    }
}

