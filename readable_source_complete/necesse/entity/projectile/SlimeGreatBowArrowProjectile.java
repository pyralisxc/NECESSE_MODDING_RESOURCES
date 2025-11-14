/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.SlimeGreatbowEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksExplosion;
import necesse.entity.particle.fireworks.FireworksPath;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SlimeGreatBowArrowProjectile
extends Projectile {
    protected boolean isFallingProjectile;
    protected Point2D.Float targetPoints;
    protected GameDamage damage;
    protected float eventResilienceGain;

    public SlimeGreatBowArrowProjectile() {
    }

    public SlimeGreatBowArrowProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, float eventResilienceGain, int knockback, Point2D.Float targetPoints, boolean isFallingProjectile) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.damage = damage;
        this.eventResilienceGain = eventResilienceGain;
        this.knockback = knockback;
        this.targetPoints = targetPoints;
        this.isFallingProjectile = isFallingProjectile;
    }

    @Override
    public void init() {
        super.init();
        this.height = 40.0f;
        this.piercing = 0;
        this.isSolid = false;
        this.heightBasedOnDistance = true;
        this.trailOffset = -15.0f;
        this.removeIfOutOfBounds = false;
        this.canBreakObjects = false;
        this.setWidth(6.0f, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isFallingProjectile);
        if (this.targetPoints != null) {
            writer.putNextBoolean(true);
            writer.putNextFloat(this.targetPoints.x);
            writer.putNextFloat(this.targetPoints.y);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isFallingProjectile = reader.getNextBoolean();
        this.targetPoints = reader.getNextBoolean() ? new Point2D.Float(reader.getNextFloat(), reader.getNextFloat()) : null;
    }

    @Override
    public boolean canHit(Mob mob) {
        return false;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isFallingProjectile) {
            SlimeGreatBowArrowProjectile projectile = new SlimeGreatBowArrowProjectile(this.getLevel(), this.getOwner(), this.targetPoints.x, this.targetPoints.y - (float)this.distance, this.targetPoints.x, this.targetPoints.y, this.speed, this.distance, this.damage, this.eventResilienceGain, this.knockback, null, true);
            projectile.getUniqueID(new GameRandom(this.getUniqueID()).nextSeeded(68));
            this.getLevel().entityManager.projectiles.addHidden(projectile);
        } else {
            float targetY;
            float targetX;
            if (mob != null) {
                targetX = mob.x;
                targetY = mob.y;
            } else {
                targetX = x;
                targetY = y;
            }
            int lifetime = 600;
            int range = 85;
            if (!this.isServer()) {
                SoundManager.playSound(GameResources.blunthit, (SoundEffect)SoundEffect.effect(this).volume(0.6f));
                SoundManager.playSound(GameResources.slimeSplash3, (SoundEffect)SoundEffect.effect(this).falloffDistance(1000).volume(0.8f));
                FireworksExplosion explosion = new FireworksExplosion(FireworksPath.sphere(GameRandom.globalRandom.getIntBetween(range - 10, range)));
                explosion.colorGetter = (particle, progress, random) -> ParticleOption.randomizeColor(175.0f, 0.6f, 0.8f, 2.0f, 0.0f, 0.0f);
                explosion.trailChance = 0.0f;
                explosion.particles = 40;
                explosion.lifetime = lifetime;
                explosion.popOptions = null;
                explosion.particleLightHue = 0.0f;
                explosion.explosionSound = null;
                explosion.spawnExplosion(this.getLevel(), targetX, targetY, this.getHeight(), GameRandom.globalRandom);
            }
            this.getLevel().entityManager.events.addHidden(new SlimeGreatbowEvent(this.getOwner(), (int)x, (int)y, new GameRandom(this.getUniqueID()), this.getDamage(), this.eventResilienceGain, this.knockback));
        }
    }

    @Override
    public Color getParticleColor() {
        return new Color(41, 128, 138);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(70, 178, 170), 16.0f, 200, this.getHeight());
        trail.drawOnTop = true;
        return trail;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        topList.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }
}

