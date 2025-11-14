/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.pathProjectile;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.fireworks.FireworksExplosion;
import necesse.entity.particle.fireworks.FireworksPath;
import necesse.entity.projectile.pathProjectile.PathProjectile;
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

public class CrimsonSkyArrowPathProjectile
extends PathProjectile {
    public float maxHeightAtPercent = 0.2f;
    public float maxHeight = 300.0f;
    public float startX;
    public float startY;
    public float targetX;
    public float targetY;
    public static FireworksExplosion piercerPopExplosion = new FireworksExplosion(FireworksExplosion.popPath);

    public CrimsonSkyArrowPathProjectile() {
    }

    public CrimsonSkyArrowPathProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.startX = x;
        this.startY = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.targetX = targetX;
        this.targetY = targetY;
        float targetDeltaX = targetX - x;
        float targetDeltaY = targetY - y;
        this.distance = (int)Math.sqrt(targetDeltaX * targetDeltaX + targetDeltaY * targetDeltaY);
        this.setDamage(damage);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.piercing = 0;
        this.isSolid = false;
        this.canBreakObjects = false;
        this.setWidth(6.0f, false);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startX);
        writer.putNextFloat(this.startY);
        writer.putNextFloat(this.targetX);
        writer.putNextFloat(this.targetY);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startX = reader.getNextFloat();
        this.startY = reader.getNextFloat();
        this.targetX = reader.getNextFloat();
        this.targetY = reader.getNextFloat();
    }

    @Override
    public Point2D.Float getPosition(double dist) {
        float distPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        Point2D.Float direction = GameMath.normalize(this.targetX - this.startX, this.targetY - this.startY);
        float currentX = this.startX + direction.x * distPerc * (float)this.distance;
        float currentY = this.startY + direction.y * distPerc * (float)this.distance;
        if (distPerc < this.maxHeightAtPercent) {
            float currentProgressTowardsMaxHeight = distPerc / this.maxHeightAtPercent;
            float currentHeight = this.maxHeight * currentProgressTowardsMaxHeight;
            return new Point2D.Float(currentX, currentY - currentHeight);
        }
        float currentProgressTowardsGround = (distPerc - this.maxHeightAtPercent) / (1.0f - this.maxHeightAtPercent);
        float currentHeight = this.maxHeight * currentProgressTowardsGround;
        return new Point2D.Float(currentX, currentY - this.maxHeight + currentHeight);
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        float targetY;
        float targetX;
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            targetX = mob.x;
            targetY = mob.y;
        } else {
            targetX = x;
            targetY = y;
        }
        int range = 70;
        if (!this.isServer()) {
            FireworksExplosion explosion = new FireworksExplosion(FireworksPath.sphere(GameRandom.globalRandom.getIntBetween(range - 10, range)));
            explosion.colorGetter = (particle, progress, random) -> ParticleOption.randomizeColor(310.0f, 0.5f, 0.4f, 20.0f, 0.1f, 0.1f);
            explosion.trailChance = 0.5f;
            explosion.particles = 50;
            explosion.lifetime = 500;
            explosion.popOptions = piercerPopExplosion;
            explosion.particleLightHue = 310.0f;
            explosion.explosionSound = (pos, height, random) -> SoundManager.playSound(GameResources.fireworkExplosion, (SoundEffect)SoundEffect.effect(pos.x, pos.y).pitch(random.getOneOf(Float.valueOf(0.95f), Float.valueOf(1.0f), Float.valueOf(1.05f)).floatValue()).volume(0.6f).falloffDistance(1500));
            explosion.spawnExplosion(this.getLevel(), targetX, targetY, this.getHeight(), GameRandom.globalRandom);
        }
        if (!this.isClient()) {
            Rectangle targetBox = new Rectangle((int)targetX - range, (int)targetY - range, range * 2, range * 2);
            this.streamTargets(this.getOwner(), targetBox).filter(m -> this.canHit((Mob)m) && m.getDistance(targetX, targetY) <= (float)range).forEach(m -> m.isServerHit(this.getDamage(), m.x - x, m.y - y, this.knockback, this));
        }
    }

    @Override
    public void applyDamage(Mob mob, float x, float y) {
    }

    @Override
    public Color getParticleColor() {
        return new Color(108, 37, 92);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), new Color(154, 8, 8), 12.0f, 200, this.getHeight());
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
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }

    static {
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.particles = 1;
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.lifetime = 200;
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.minSize = 6;
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.maxSize = 10;
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.trailChance = 0.0f;
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.popChance = 0.0f;
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.colorGetter = (particle, progress, random) -> ParticleOption.randomizeColor(310.0f, 0.8f, 0.7f, 20.0f, 0.1f, 0.1f);
        CrimsonSkyArrowPathProjectile.piercerPopExplosion.explosionSound = null;
    }
}

