/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.projectile.Projectile
 */
package aphorea.levelevents;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;

public abstract class ProjectileHitboxEffectEvent
extends HitboxEffectEvent {
    public ProjectileHitboxEffectEvent() {
    }

    public ProjectileHitboxEffectEvent(Mob owner, GameRandom uniqueIDRandom) {
        super(owner, uniqueIDRandom);
    }

    public void clientTick() {
        super.clientTick();
        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleProjectileHits(hitBox);
        }
    }

    public void serverTick() {
        super.serverTick();
        Shape hitBox = this.getHitBox();
        if (hitBox != null) {
            this.handleProjectileHits(hitBox);
        }
    }

    protected abstract void onProjectileHit(Projectile var1);

    protected void handleProjectileHits(Shape hitbox) {
        this.handleProjectileHits(Collections.singleton(hitbox));
    }

    protected void handleProjectileHits(Iterable<Shape> hitboxes) {
        if (this.handlingClient != null) {
            this.streamProjectiles(this.getHitboxesBounds(hitboxes)).filter(p -> this.canHit(p.getOwner())).filter(p -> this.anyHitboxIntersectsProjectile(hitboxes, (Projectile)p)).filter(p -> {
                Mob owner = p.getOwner();
                if (owner == null) {
                    return true;
                }
                if (owner == this.getAttackOwner()) {
                    return false;
                }
                return owner.canBeTargeted(this.getAttackOwner(), this.getAttackOwner().isPlayer ? ((PlayerMob)this.getAttackOwner()).getNetworkClient() : null);
            }).forEach(this::onProjectileHit);
        }
    }

    protected Stream<Projectile> streamProjectiles(Shape hitbox) {
        return this.level.entityManager.projectiles.streamInRegionsShape(hitbox, 1);
    }

    protected boolean anyHitboxIntersectsProjectile(Iterable<Shape> hitBoxes, Projectile projectile) {
        Shape hitBox;
        Rectangle targetHitbox = projectile.getHitbox();
        Iterator<Shape> var4 = hitBoxes.iterator();
        do {
            if (var4.hasNext()) continue;
            return false;
        } while (!(hitBox = var4.next()).intersects(targetHitbox));
        return true;
    }
}

