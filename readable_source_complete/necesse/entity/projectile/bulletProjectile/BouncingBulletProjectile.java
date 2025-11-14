/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.bulletProjectile;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.bulletProjectile.BulletProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.LevelObjectHit;

public class BouncingBulletProjectile
extends BulletProjectile {
    public BouncingBulletProjectile() {
    }

    public BouncingBulletProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        super(x, y, targetX, targetY, speed, distance, damage, knockback, owner);
    }

    @Override
    public void init() {
        super.init();
        this.bouncing = 20;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob == null) {
            int offset = new GameRandom(this.getUniqueID() + this.bounced * 1337).getIntBetween(-5, 5);
            this.setAngle(this.getAngle() + (float)offset);
        }
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(56, 53, 172), 22.0f, 100, this.getHeight());
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    @Override
    protected Color getWallHitColor() {
        return new Color(56, 53, 172);
    }

    @Override
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, 220.0f, this.lightSaturation);
    }
}

