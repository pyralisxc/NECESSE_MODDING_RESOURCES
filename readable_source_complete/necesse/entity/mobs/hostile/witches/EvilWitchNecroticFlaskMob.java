/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.witches;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.witches.EvilWitchMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.ColumnObject;
import necesse.level.maps.CollisionFilter;

public class EvilWitchNecroticFlaskMob
extends EvilWitchMob {
    private int attacksSinceLastMove = 0;

    public EvilWitchNecroticFlaskMob() {
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.attackCooldown = 1500;
        this.attackAnimTime = 600;
    }

    @Override
    public void init() {
        super.init();
        ConfusedPlayerChaserWandererAI<EvilWitchNecroticFlaskMob> aiNode = new ConfusedPlayerChaserWandererAI<EvilWitchNecroticFlaskMob>(null, 576, 480, 40000, true, false){

            @Override
            public boolean canHitTarget(EvilWitchNecroticFlaskMob mob, float fromX, float fromY, Mob target) {
                CollisionFilter collisionFilter = new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock || tp.object().object instanceof ColumnObject);
                collisionFilter = mob.modifyChasingCollisionFilter(collisionFilter, target);
                return !mob.getLevel().collides(new Line2D.Float(fromX, fromY, target.x, target.y), collisionFilter);
            }

            @Override
            public boolean attackTarget(EvilWitchNecroticFlaskMob mob, Mob target) {
                float deltaDist;
                if (!mob.canAttack() || mob.isAccelerating() || mob.hasCurrentMovement()) {
                    return false;
                }
                GameDamage damage = new GameDamage(20.0f);
                int flaskAirTime = 1000;
                float deltaX = Entity.getPositionAfterMillis(target.dx, flaskAirTime);
                float deltaY = Entity.getPositionAfterMillis(target.dy, flaskAirTime);
                if ((deltaDist = (float)Math.sqrt((deltaX += GameRandom.globalRandom.getFloatBetween(-40.0f, 40.0f)) * deltaX + (deltaY += GameRandom.globalRandom.getFloatBetween(-40.0f, 40.0f)) * deltaY)) > 200.0f) {
                    Point2D.Float dir = GameMath.normalize(deltaX, deltaY);
                    deltaDist = 200.0f;
                    deltaX = dir.x * deltaDist;
                    deltaY = dir.y * deltaDist;
                }
                float predictedX = target.x + deltaX;
                float predictedY = target.y + deltaY;
                float distance = EvilWitchNecroticFlaskMob.this.getDistance(predictedX, predictedY);
                float speed = Entity.getTravelSpeedForMillis(flaskAirTime, distance);
                EvilWitchNecroticFlaskMob.this.attack((int)predictedX, (int)predictedY, false);
                Projectile projectile = ProjectileRegistry.getProjectile("necroticflasksplash", EvilWitchNecroticFlaskMob.this.getLevel(), EvilWitchNecroticFlaskMob.this.x, EvilWitchNecroticFlaskMob.this.y, predictedX, predictedY, speed, (int)distance, damage, (Mob)mob);
                projectile.moveDist(10.0);
                EvilWitchNecroticFlaskMob.this.getLevel().entityManager.projectiles.add(projectile);
                EvilWitchNecroticFlaskMob.this.attacksSinceLastMove++;
                if (EvilWitchNecroticFlaskMob.this.attacksSinceLastMove >= 3) {
                    EvilWitchNecroticFlaskMob.this.attacksSinceLastMove = 0;
                    EvilWitchNecroticFlaskMob.this.getLevel().entityManager.events.addHidden(new WaitForSecondsEvent(1.0f){

                        @Override
                        public void onWaitOver() {
                            EvilWitchNecroticFlaskMob.this.ai.blackboard.submitEvent("chaserMovePosition", new AIEvent());
                        }
                    });
                } else {
                    this.wanderAfterAttack = GameRandom.globalRandom.getChance(0.75f);
                }
                return true;
            }
        };
        aiNode.playerChaserAI.chaserAINode.moveSearchPositionRange = 256;
        this.ai = new BehaviourTreeAI<EvilWitchNecroticFlaskMob>(this, aiNode);
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
            humanDrawOptions.itemAttack(new InventoryItem("necroticflask"), null, animProgress, this.attackDir.x, this.attackDir.y);
        }
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(this));
        }
    }
}

