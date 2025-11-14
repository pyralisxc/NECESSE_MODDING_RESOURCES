/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.AscendedBoltProjectile;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedStaffBeamProjectile
extends FollowingProjectile {
    private float particleBuffer;
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    private SoundPlayer sound;
    protected float fullSpeed = 0.0f;

    public AscendedStaffBeamProjectile() {
    }

    public AscendedStaffBeamProjectile(float startX, float startY, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = startX;
        this.y = startY;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.fullSpeed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.fullSpeed);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.fullSpeed = reader.getNextFloat();
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(60.0f, true);
        this.turnSpeed = 50.0f;
        this.particleSpeedMod = 0.03f;
        this.piercing = 100;
        this.isSolid = false;
        this.stopsAtTarget = true;
        this.givesLight = true;
        this.lightSaturation = 171.0f;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.tickDebrisProjectiles();
        this.globalTick();
    }

    private void tickDebrisProjectiles() {
        Mob owner = this.getOwner();
        if (owner != null && owner.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION) && this.getLevel().tickManager().getTotalTicks() % 4L != 0L) {
            return;
        }
        AscendedBoltProjectile projectile = new AscendedBoltProjectile(this.getLevel(), this.x, this.y + 16.0f, GameRandom.globalRandom.getFloatBetween(0.0f, 360.0f), 300.0f, 100, this.getDamage().modFinalMultiplier(0.1f), this.getOwner());
        projectile.getUniqueID(GameRandom.globalRandom);
        this.getLevel().entityManager.projectiles.add(projectile);
    }

    protected void globalTick() {
        Mob owner;
        double distFromTarget = this.targetPos != null ? this.targetPos.distance(new Point2D.Float(this.x, this.y)) : (this.target != null ? new Point2D.Float(this.x, this.y).distance(new Point2D.Float(this.target.x, this.target.y)) : new Point2D.Float(this.x, this.y).distance(new Point2D.Float(this.targetX, this.targetY)));
        float newSpeed = (float)GameMath.limit(distFromTarget * (double)1.2f, 0.0, (double)this.fullSpeed);
        if (this.speed != newSpeed) {
            this.speed = newSpeed;
        }
        this.hitCooldowns.hitCooldown = (owner = this.getOwner()) != null && owner.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION) ? 1000 : 250;
    }

    @Override
    public float getOriginalSpeed() {
        return this.fullSpeed;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.sound == null || this.sound.isDone()) {
            this.sound = SoundManager.playSound(GameResources.laserBeam1, (SoundEffect)SoundEffect.effect(this).falloffDistance(1000).volume(0.0f));
            if (this.sound != null) {
                this.sound.fadeIn(1.0f);
                this.sound.effect.volume(0.5f);
            }
        }
        if (this.sound != null) {
            this.sound.refreshLooping(1.0f);
        }
        this.globalTick();
        this.getLevel().entityManager.addParticle(this, (float)GameRandom.globalRandom.getIntBetween(-20, 20), 0.0f, Particle.GType.COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).ignoreLight(true).sizeFades(20, 40).movesConstant(GameRandom.globalRandom.getIntBetween(-20, 20), GameRandom.globalRandom.getIntBetween(-20, -40)).height(16.0f).lifeTime(500);
        float particlesPerSecond = 80.0f;
        this.particleBuffer += particlesPerSecond / 20.0f;
        while (this.particleBuffer >= 1.0f) {
            this.particleBuffer -= 1.0f;
            GameRandom random = GameRandom.globalRandom;
            AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
            float distance = 75.0f;
            this.getLevel().entityManager.addParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5), this.y + GameMath.cos(currentAngle.get().floatValue()) * distance + (float)random.getIntBetween(-5, 5) * 0.85f, this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(random.getIntBetween(0, 4), 0, 12)).height(16.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
                float distY = distance * lifePercent * 0.85f;
                pos.x = this.x + GameMath.sin(angle) * (distance * lifePercent);
                pos.y = this.y + GameMath.cos(angle) * distY * 0.85f;
            }).color((options, lifeTime, timeAlive, lifePercent) -> {
                float clampedLifePercent = Math.max(0.0f, Math.min(1.0f, lifePercent));
                options.color(new Color(255, (int)(255.0f - 255.0f * clampedLifePercent), (int)(255.0f - 24.0f * clampedLifePercent)));
            }).ignoreLight(true).lifeTime(1000).sizeFades(50, 24);
        }
    }

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2 + 16;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight();
        int anim = GameUtils.getAnim(this.getWorldEntity().getTime(), 2, 200);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(anim, 0, 32, 640).light(light.minLevelCopy(150.0f)).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().light(light.minLevelCopy(150.0f)).pos(drawX - this.shadowTexture.getWidth() / 2 + 16, drawY + this.texture.getHeight() - this.shadowTexture.getHeight() / 2 - 16);
        tileList.add(tm -> shadowOptions.draw());
    }
}

