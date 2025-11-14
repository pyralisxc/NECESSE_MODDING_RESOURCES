/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SapphireRevolverProjectile
extends Projectile {
    protected int spriteX;

    public SapphireRevolverProjectile() {
    }

    public SapphireRevolverProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this();
        this.setLevel(owner.getLevel());
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
        this.spriteX = GameRandom.globalRandom.getIntBetween(0, 2);
    }

    @Override
    public void init() {
        super.init();
        this.particleSpeedMod = 0.03f;
        this.piercing = 2;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.spriteX);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.spriteX = reader.getNextInt();
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (mob != null) {
            if (this.amountHit == this.piercing) {
                this.attachTextureToTarget(mob, x, y);
            }
        } else if (this.bounced == this.getTotalBouncing()) {
            this.attachTextureToTarget(null, x, y);
        }
    }

    private void attachTextureToTarget(Mob mob, float x, float y) {
        this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, GameRandom.globalRandom.getIntBetween(10, 20), 5000L){

            @Override
            public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                int fadeTime;
                GameLight light = level.getLightLevel(this);
                int drawX = camera.getDrawX(x) - 2;
                int drawY = camera.getDrawY(y - SapphireRevolverProjectile.this.height) - 2;
                float alpha = 1.0f;
                long lifeCycleTime = this.getLifeCycleTime();
                if (lifeCycleTime >= this.lifeTime - (long)(fadeTime = 1000)) {
                    alpha = Math.abs((float)(lifeCycleTime - (this.lifeTime - (long)fadeTime)) / (float)fadeTime - 1.0f);
                }
                int cut = target == null ? 8 : 0;
                final TextureDrawOptionsEnd options = SapphireRevolverProjectile.this.texture.initDraw().light(light).rotate(SapphireRevolverProjectile.this.getAngle(), 2, 2).alpha(alpha).pos(drawX, drawY);
                EntityDrawable drawable = new EntityDrawable(this){

                    @Override
                    public void draw(TickManager tickManager) {
                        options.draw();
                    }
                };
                if (target != null) {
                    topList.add(drawable);
                } else {
                    list.add(drawable);
                }
            }
        }, Particle.GType.IMPORTANT_COSMETIC);
    }

    @Override
    public void onMaxMoveTick() {
        if (this.isClient()) {
            this.spawnSpinningParticle();
        }
    }

    @Override
    public float tickMovement(float delta) {
        if (!this.isServer()) {
            Color color = ThemeColorRegistry.SAPPHIRE.getRandomColor();
            float dxParticle = this.dy * ((float)this.distance / this.traveledDistance) + this.dx * 10.0f;
            float dyParticle = this.dx * ((float)this.distance / this.traveledDistance) + this.dy * 10.0f;
            this.getLevel().entityManager.addParticle(this.x, this.y - this.height, Particle.GType.IMPORTANT_COSMETIC).lifeTime(1000).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(22, 11).color(color).givesLight(198.0f, 0.3f).movesFriction(dxParticle, -dyParticle, 0.8f);
            this.getLevel().entityManager.addParticle(this.x, this.y - this.height, Particle.GType.IMPORTANT_COSMETIC).lifeTime(1000).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 22)).sizeFades(22, 11).color(color).givesLight(198.0f, 0.3f).movesFriction(-dxParticle, dyParticle, 0.8f);
        }
        return super.tickMovement(delta);
    }

    @Override
    public Trail getTrail() {
        Trail trail = new Trail(this, this.getLevel(), ThemeColorRegistry.SAPPHIRE.getRandomColor(), 22.0f, 1000, this.height);
        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
        return trail;
    }

    @Override
    protected Color getWallHitColor() {
        return ThemeColorRegistry.SAPPHIRE.getRandomColor();
    }

    @Override
    public void refreshParticleLight() {
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, this.getWallHitColor(), this.lightSaturation);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 8;
        int drawY = camera.getDrawY(this.y) - 16;
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.spriteX, 0, 18, 32).light(light).rotate(this.getAngle(), 8, 18).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 18);
    }
}

