/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.QueenSpiderSpitEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.QueenSpiderMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class QueenSpiderSpitProjectile
extends Projectile {
    public QueenSpiderSpitProjectile() {
    }

    public QueenSpiderSpitProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setDistance(distance);
        this.setOwner(owner);
    }

    public QueenSpiderSpitProjectile(Level level, Mob owner, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this(level, owner, owner.x, owner.y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
        this.isSolid = false;
        this.canHitMobs = false;
        this.trailOffset = 0.0f;
        this.particleRandomOffset = 1.0f;
        this.particleDirOffset = 0.0f;
    }

    @Override
    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float travelPercInv = Math.abs(travelPerc - 1.0f);
        float heightF = GameMath.sin(travelPerc * 180.0f);
        this.height = (int)(heightF * 100.0f + 50.0f * travelPercInv);
        return out;
    }

    @Override
    public Color getParticleColor() {
        return new Color(182, 60, 53);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 2;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        Mob owner = this.getOwner();
        if (owner != null && !owner.removed()) {
            QueenSpiderSpitEvent event = new QueenSpiderSpitEvent(owner, (int)x, (int)y, GameRandom.globalRandom, this.getDamage(), QueenSpiderMob.SPIT_LINGER_SECONDS);
            this.getLevel().entityManager.events.add(event);
        }
    }

    @Override
    protected void spawnDeathParticles() {
        Color particleColor = this.getParticleColor();
        if (particleColor != null) {
            for (int i = 0; i < 10; ++i) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir(angle);
                this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.getIntBetween(20, 50) * dir.x, (float)GameRandom.globalRandom.getIntBetween(20, 50) * dir.y).color(this.getParticleColor()).height(this.getHeight());
            }
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        float shadowAlpha = Math.abs(GameMath.limit(this.height / 250.0f, 0.0f, 1.0f) - 1.0f);
        int shadowX = camera.getDrawX(this.x) - this.shadowTexture.getWidth() / 2;
        int shadowY = camera.getDrawY(this.y) - this.shadowTexture.getHeight() / 2;
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().light(light).rotate(this.angle).alpha(shadowAlpha).pos(shadowX, shadowY);
        topList.add(tm -> shadowOptions.draw());
    }
}

