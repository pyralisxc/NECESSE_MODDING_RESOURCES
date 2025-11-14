/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.witches;

import java.awt.geom.Line2D;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ability.CoordinateMobAbility;
import necesse.entity.mobs.ability.TargetedMobAbility;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.witches.EvilWitchMob;
import necesse.entity.projectile.EvilWitchGreatswordWaveProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.ColumnObject;
import necesse.level.maps.CollisionFilter;

public class EvilWitchNecroticGreatswordMob
extends EvilWitchMob {
    private long chargeAttackStartTime;
    private long chargeAttackFireTime;
    private Mob chargeAttackTarget;
    public final TargetedMobAbility startChargeAttackAbility;
    public final CoordinateMobAbility fireChargeAttackAbility;

    public EvilWitchNecroticGreatswordMob() {
        this.setSpeed(50.0f);
        this.setFriction(3.0f);
        this.startChargeAttackAbility = this.registerAbility(new TargetedMobAbility(){

            @Override
            protected void run(Mob target) {
                int chargeTime = 4000;
                EvilWitchNecroticGreatswordMob.this.attackAnimTime = chargeTime + 500;
                EvilWitchNecroticGreatswordMob.this.attackCooldown = chargeTime + 1000;
                EvilWitchNecroticGreatswordMob.this.chargeAttackStartTime = EvilWitchNecroticGreatswordMob.this.getTime();
                EvilWitchNecroticGreatswordMob.this.chargeAttackFireTime = EvilWitchNecroticGreatswordMob.this.chargeAttackStartTime + (long)chargeTime;
                EvilWitchNecroticGreatswordMob.this.chargeAttackTarget = target;
                EvilWitchNecroticGreatswordMob.this.startAttackCooldown();
                if (target != null) {
                    EvilWitchNecroticGreatswordMob.this.showAttack(target.getX(), target.getY(), true);
                } else {
                    EvilWitchNecroticGreatswordMob.this.showAttack(EvilWitchNecroticGreatswordMob.this.getX() + 100, EvilWitchNecroticGreatswordMob.this.getY(), true);
                }
            }
        });
        this.fireChargeAttackAbility = this.registerAbility(new CoordinateMobAbility(){

            @Override
            protected void run(int x, int y) {
                EvilWitchNecroticGreatswordMob.this.chargeAttackStartTime = 0L;
                EvilWitchNecroticGreatswordMob.this.chargeAttackFireTime = 0L;
                EvilWitchNecroticGreatswordMob.this.attackAnimTime = 300;
                EvilWitchNecroticGreatswordMob.this.attackCooldown = 1000;
                EvilWitchNecroticGreatswordMob.this.shootAbilityProjectile(x, y);
                EvilWitchNecroticGreatswordMob.this.ai.blackboard.submitEvent("confuseAfterAttack", new AIEvent());
            }
        });
    }

    @Override
    public void init() {
        super.init();
        ConfusedPlayerChaserWandererAI<EvilWitchNecroticGreatswordMob> aiNode = new ConfusedPlayerChaserWandererAI<EvilWitchNecroticGreatswordMob>(null, 576, 480, 40000, true, false){

            @Override
            public boolean canHitTarget(EvilWitchNecroticGreatswordMob mob, float fromX, float fromY, Mob target) {
                CollisionFilter collisionFilter = new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock || tp.object().object instanceof ColumnObject);
                collisionFilter = mob.modifyChasingCollisionFilter(collisionFilter, target);
                return !mob.getLevel().collides(new Line2D.Float(fromX, fromY, target.x, target.y), collisionFilter);
            }

            @Override
            public boolean attackTarget(EvilWitchNecroticGreatswordMob mob, Mob target) {
                if (!mob.canAttack() || mob.isAccelerating() || mob.hasCurrentMovement()) {
                    return false;
                }
                EvilWitchNecroticGreatswordMob.this.startChargeAttackAbility.runAndSend(target);
                return true;
            }
        };
        aiNode.playerChaserAI.chaserAINode.moveSearchPositionRange = 256;
        this.ai = new BehaviourTreeAI<EvilWitchNecroticGreatswordMob>(this, aiNode);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.chargeAttackStartTime != 0L && this.chargeAttackTarget != null) {
            this.setFacingDir(this.chargeAttackTarget.x - this.x, this.chargeAttackTarget.y - this.y);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.chargeAttackStartTime != 0L) {
            long timeToNextAttack;
            if (this.chargeAttackTarget != null) {
                this.setFacingDir(this.chargeAttackTarget.x - this.x, this.chargeAttackTarget.y - this.y);
            }
            if ((timeToNextAttack = this.chargeAttackFireTime - this.getTime()) <= 0L && this.chargeAttackTarget != null && this.isSamePlace(this.chargeAttackTarget)) {
                CollisionFilter collisionFilter = new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
                collisionFilter = this.modifyChasingCollisionFilter(collisionFilter, this.chargeAttackTarget);
                if (!this.getLevel().collides(new Line2D.Float(this.x, this.y, this.chargeAttackTarget.x, this.chargeAttackTarget.y), collisionFilter)) {
                    this.fireChargeAttackAbility.runAndSend(this.chargeAttackTarget.getX(), this.chargeAttackTarget.getY());
                }
            }
        }
    }

    public void shootAbilityProjectile(int x, int y) {
        if (this.isServer()) {
            GameRandom random = new GameRandom(this.attackSeed);
            GameDamage damage = new GameDamage(30.0f);
            float speed = 80.0f;
            int range = 480;
            EvilWitchGreatswordWaveProjectile projectile = new EvilWitchGreatswordWaveProjectile(this.getLevel(), this, this.getX(), this.getY(), x, y, damage, speed, range);
            projectile.resetUniqueID(random);
            projectile.moveDist(10.0);
            this.getLevel().entityManager.projectiles.add(projectile);
            this.ai.blackboard.submitEvent("chaserMovePosition", new AIEvent());
        }
        if (this.isClient()) {
            SoundManager.playSound(GameResources.swing2, (SoundEffect)SoundEffect.effect(this));
        }
        this.showAttack(x, y, true);
    }

    @Override
    protected void setupWitchDrawOptions(HumanDrawOptions humanDrawOptions) {
        float animProgress = this.getAttackAnimProgress();
        if (this.isAttacking) {
            InventoryItem showItem = new InventoryItem("necroticgreatsword");
            if (this.chargeAttackStartTime != 0L) {
                long totalAttackTime = this.chargeAttackFireTime - this.chargeAttackStartTime;
                long timeToNextAttack = this.chargeAttackFireTime - this.getTime();
                float progress = 1.0f - GameMath.limit((float)timeToNextAttack / (float)totalAttackTime, 0.0f, 1.0f);
                showItem.getGndData().setBoolean("charging", true);
                showItem.getGndData().setFloat("chargePercent", progress);
            } else {
                showItem.getGndData().setBoolean("shouldFire", true);
            }
            humanDrawOptions.itemAttack(showItem, null, animProgress, this.attackDir.x, this.attackDir.y);
        }
    }
}

