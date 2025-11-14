/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.GameLog;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MouseBeamLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.leaves.ChaserAINode;
import necesse.entity.mobs.attackHandler.MouseBeamAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.laserProjectile.ChargeBeamProjectile;
import necesse.gfx.GameResources;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;

public class ChargeBeamAttackHandler
extends MouseBeamAttackHandler {
    public long timeAtFullCharge;
    private boolean playedReadySound;

    public ChargeBeamAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, int updateInterval, int attackSeed, MouseBeamLevelEvent event, long fullChargeTime) {
        super(attackerMob, slot, updateInterval, attackSeed, event);
        this.timeAtFullCharge = attackerMob.getTime() + fullChargeTime;
    }

    @Override
    public boolean canAIStillHitTarget(Mob target) {
        return ChaserAINode.isTargetHitboxWithinRange(this.attackerMob, this.attackerMob.x, this.attackerMob.y, target, this.event.getDistance());
    }

    @Override
    public void onUpdate() {
        long timeUntilFullCharge;
        super.onUpdate();
        if (!this.playedReadySound && this.attackerMob.isClient() && (timeUntilFullCharge = this.timeAtFullCharge - this.event.getTime()) <= 0L) {
            SoundManager.playSound(GameResources.hoverboots, (SoundEffect)SoundEffect.effect(this.attackerMob).volume(2.0f).pitch(1.2f));
            this.playedReadySound = true;
            int particles = 10;
            int minForce = 30;
            int maxForce = 50;
            ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
            float anglePerParticle = 360.0f / (float)particles;
            for (int i = 0; i < particles; ++i) {
                int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(minForce, maxForce);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(minForce, maxForce) * 0.8f;
                Color color1 = new Color(199, 40, 34);
                Color color2 = new Color(222, 42, 75);
                Color color3 = new Color(220, 37, 92);
                this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(GameRandom.globalRandom.getOneOf(color1, color2, color3)).heightMoves(0.0f, 30.0f).lifeTime(500);
            }
        }
        if (!this.attackerMob.isPlayer && (timeUntilFullCharge = this.timeAtFullCharge - this.event.getTime()) <= 0L) {
            this.attackerMob.endAttackHandler(true);
        }
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        super.onEndAttack(bySelf);
        if (bySelf) {
            long timeUntilFullCharge = this.timeAtFullCharge - this.event.getTime();
            if (timeUntilFullCharge <= 100L) {
                if (!this.attackerMob.isPlayer) {
                    this.attackerMob.startGenericCooldown("chargeBeam", 250L);
                }
                Point2D.Float dir = GameMath.getAngleDir(this.event.currentAngle);
                if (this.item.item instanceof ProjectileToolItem) {
                    ProjectileToolItem toolItem = (ProjectileToolItem)this.item.item;
                    int attackRange = toolItem.getAttackRange(this.item);
                    float velocity = toolItem.getProjectileVelocity(this.item, this.attackerMob);
                    GameDamage damage = toolItem.getAttackDamage(this.item);
                    int knockback = toolItem.getKnockback(this.item, this.attackerMob);
                    ChargeBeamProjectile projectile = new ChargeBeamProjectile(this.attackerMob.getLevel(), this.attackerMob, this.attackerMob.x, this.attackerMob.y, this.attackerMob.x + dir.x * 1000.0f, this.attackerMob.y + dir.y * 1000.0f, velocity, attackRange, damage, knockback);
                    projectile.resetUniqueID(new GameRandom(this.seed).nextSeeded(23));
                    this.attackerMob.addAndSendAttackerProjectile(projectile);
                } else {
                    GameLog.warn.println(this.attackerMob + " tried to fire charge beam with invalid item: " + this.item);
                }
            } else if (!this.attackerMob.isServer()) {
                SoundManager.playSound(GameResources.fadedeath1, (SoundEffect)SoundEffect.effect(this.attackerMob).volume(0.3f).pitch(1.8f));
            }
        }
    }
}

