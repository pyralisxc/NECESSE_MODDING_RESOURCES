/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Rectangle;
import java.util.HashSet;
import necesse.engine.network.NetworkClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.CollisionFilter;

public class ProjectileHitboxMob
extends Mob {
    public Projectile projectile;

    public ProjectileHitboxMob() {
        super(1);
        this.setArmor(0);
        this.setSpeed(0.0f);
        this.setFriction(1000.0f);
        this.setKnockbackModifier(0.0f);
        this.shouldSave = false;
        this.isStatic = true;
        this.selectBox = new Rectangle();
        this.collision = new Rectangle();
    }

    @Override
    public boolean shouldSendSpawnPacket() {
        return false;
    }

    @Override
    public void tickMovement(float delta) {
        if (this.projectile != null) {
            this.x = this.projectile.x;
            this.y = this.projectile.y;
        }
        super.tickMovement(delta);
    }

    @Override
    protected void checkCollision() {
    }

    @Override
    protected void calcAcceleration(float speed, float friction, float moveX, float moveY, float delta) {
    }

    @Override
    protected void tickCollisionMovement(float delta, Mob rider) {
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.projectile == null || this.projectile.removed()) {
            this.remove();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.projectile == null || this.projectile.removed()) {
            this.remove();
        }
    }

    @Override
    public Rectangle getHitBox(int x, int y) {
        if (this.projectile != null) {
            return this.projectile.getHitbox();
        }
        return new Rectangle();
    }

    @Override
    public boolean canBeTargeted(Mob attacker, NetworkClient attackerClient) {
        return super.canBeTargeted(attacker, attackerClient);
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        if (this.projectile != null) {
            if (attacker == this.projectile) {
                return false;
            }
            Mob owner = this.projectile.getOwner();
            if (owner != null) {
                return owner.canBeHit(attacker);
            }
        }
        return super.canBeHit(attacker);
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public boolean canPushMob(Mob other) {
        return false;
    }

    @Override
    public boolean isHealthBarVisible() {
        return false;
    }

    @Override
    public boolean canTakeDamage() {
        return true;
    }

    @Override
    public boolean countDamageDealt() {
        return false;
    }

    @Override
    public void playHurtSound() {
    }

    @Override
    public void playHitDeathSound() {
    }

    @Override
    public void playDeathSound() {
    }

    @Override
    public void spawnDamageText(int damage, int size, boolean isCrit) {
    }

    @Override
    protected void onDeath(Attacker attacker, HashSet<Attacker> attackers) {
        super.onDeath(attacker, attackers);
        if (this.projectile != null && !this.projectile.removed()) {
            this.projectile.remove();
            if (attacker instanceof Projectile) {
                Mob hitboxMob;
                Projectile attackerProjectile = (Projectile)attacker;
                if (attackerProjectile.hasHitbox && (hitboxMob = attackerProjectile.getHitboxMob()) != null && !hitboxMob.removed() && this.projectile.canHit(hitboxMob)) {
                    this.projectile.onHit(hitboxMob, null, attackerProjectile.x, attackerProjectile.y, false, null);
                }
            }
        }
    }
}

