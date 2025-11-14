/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
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
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
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

public class AscendedBeamProjectile
extends FollowingProjectile {
    private boolean isTransformedVersion;
    private float particleBuffer;
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);
    private SoundPlayer sound;

    public AscendedBeamProjectile() {
    }

    public AscendedBeamProjectile(float x, float y, Mob target, float speed, int distance, GameDamage damage, int knockback, boolean isTransformedVersion, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(target.x, target.y);
        this.target = target;
        this.speed = speed;
        this.isTransformedVersion = isTransformedVersion;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextBoolean(this.isTransformedVersion);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.isTransformedVersion = reader.getNextBoolean();
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(50.0f, true);
        this.turnSpeed = 0.13f;
        this.particleSpeedMod = 0.03f;
        this.piercing = 200;
        this.isSolid = false;
        this.givesLight = true;
        this.lightSaturation = 171.0f;
    }

    @Override
    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return this.getTurnSpeed(delta) * this.invDynamicTurnSpeedMod(targetX, targetY, (float)this.getTurnRadius() / 1.5f);
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
    public void serverTick() {
        super.serverTick();
        if (this.isTransformedVersion) {
            this.tickDebrisProjectiles();
        }
    }

    @Override
    public void updateTarget() {
        if (this.target == null) {
            this.findTarget(m -> m.isPlayer, 0.0f, 200.0f);
        }
    }

    private void tickDebrisProjectiles() {
        AscendedWizardMob owner = this.getAscendedWizardOwner();
        AscendedBoltProjectile projectile = new AscendedBoltProjectile(this.getLevel(), this.x + this.dx * 24.0f, this.y + this.dy * 24.0f, GameRandom.globalRandom.getFloatBetween(0.0f, 360.0f), 300.0f, 100, this.getDamage(), owner);
        projectile.getUniqueID(GameRandom.globalRandom);
        this.getLevel().entityManager.projectiles.add(projectile);
        if (owner != null) {
            owner.spawnedProjectiles.add(projectile);
        }
    }

    private AscendedWizardMob getAscendedWizardOwner() {
        if (this.getOwner() instanceof AscendedWizardMob) {
            return (AscendedWizardMob)this.getOwner();
        }
        return null;
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

