/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.laserProjectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class ChargeBeamProjectile
extends Projectile {
    public ChargeBeamProjectile() {
    }

    public ChargeBeamProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        if (!this.isServer()) {
            SoundManager.playSound(GameResources.electricExplosion, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(1.0f, 1.2f)).falloffDistance(1400));
        }
        this.setWidth(16.0f, false);
        this.givesLight = true;
        this.height = 14.0f;
        this.bouncing = 10;
        this.piercing = 1000;
        this.maxMovePerTick = 24;
    }

    @Override
    protected void spawnSpinningParticle() {
        Point2D.Float perp = GameMath.getPerpendicularDir(this.dx, this.dy);
        this.modifySpinningParticle(this.getLevel().entityManager.addParticle(this.x + this.dx * this.particleDirOffset, this.y + this.dy * this.particleDirOffset, this.spinningTypeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 3), 0, 22)).movesFriction(perp.x * 20.0f, perp.y * 20.0f, 2.0f).sizeFades(28, 32).color(this.getParticleColor()).height(this.height));
        this.modifySpinningParticle(this.getLevel().entityManager.addParticle(this.x + this.dx * this.particleDirOffset, this.y + this.dy * this.particleDirOffset, this.spinningTypeSwitcher.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.getIntBetween(0, 3), 0, 22)).movesFriction(-perp.x * 20.0f, -perp.y * 20.0f, 2.0f).sizeFades(28, 32).color(this.getParticleColor()).height(this.height));
    }

    @Override
    protected int getExtraSpinningParticles() {
        return super.getExtraSpinningParticles() + 3;
    }

    @Override
    public Color getParticleColor() {
        Color color1 = new Color(199, 40, 34);
        Color color2 = new Color(222, 42, 75);
        Color color3 = new Color(220, 37, 92);
        return GameRandom.globalRandom.getOneOf(color1, color2, color3);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(222, 42, 75), 5.0f, 500, this.height);
    }

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    @Override
    protected Color getWallHitColor() {
        return null;
    }
}

