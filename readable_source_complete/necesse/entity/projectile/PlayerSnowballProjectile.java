/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.Shape;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ProjectileHitStuckParticle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class PlayerSnowballProjectile
extends Projectile {
    private int sprite;

    public PlayerSnowballProjectile() {
    }

    public PlayerSnowballProjectile(float x, float y, float targetX, float targetY, GameDamage damage, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = 100.0f;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(400);
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(10.0f);
        this.height = 18.0f;
        this.spawnTime = this.getWorldEntity().getTime();
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0f;
        if (this.texture != null) {
            this.sprite = new GameRandom(this.getUniqueID()).nextInt(this.texture.getWidth() / 32);
        }
    }

    @Override
    protected Stream<Mob> streamTargets(Mob owner, Shape hitBounds) {
        return Stream.concat(this.getLevel().entityManager.mobs.streamInRegionsShape(hitBounds, 1), GameUtils.streamNetworkClients(this.getLevel()).filter(c -> !c.isDead() && c.hasSpawned()).map(sc -> sc.playerMob)).filter(mob -> mob != owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        if (!mob.canTakeDamage()) {
            return false;
        }
        Mob mounted = mob.getRider();
        return mounted == null || mounted.canTakeDamage();
    }

    @Override
    public void applyDamage(Mob mob, float x, float y) {
        if (mob.isPlayer) {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SNOW_COVERED_DEBUFF, mob, 8000, null), true);
        } else {
            mob.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SNOW_COVERED_SLOW_DEBUFF, mob, 8000, null), true);
        }
    }

    @Override
    public void startMobHitCooldown(Mob target) {
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (this.isClient() && this.traveledDistance < (float)this.distance) {
            this.getLevel().entityManager.addParticle(new ProjectileHitStuckParticle(mob, this, x, y, mob == null ? 2.0f : 6.0f, 8000L){

                @Override
                public void addDrawables(Mob target, float x, float y, float angle, List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                    int fadeTime;
                    GameLight light = level.getLightLevel(this);
                    float alpha = 1.0f;
                    long lifeCycleTime = this.getLifeCycleTime();
                    if (lifeCycleTime >= this.lifeTime - (long)(fadeTime = 500)) {
                        alpha = Math.abs((float)(lifeCycleTime - (this.lifeTime - (long)fadeTime)) / (float)fadeTime - 1.0f);
                    }
                    int textureRes = 32;
                    int halfTextureRes = textureRes / 2;
                    int drawX = camera.getDrawX(x) - halfTextureRes;
                    int drawY = camera.getDrawY(y) - halfTextureRes;
                    GameTextureSection cutTexture = new GameTextureSection(GameResources.cutSnowballParticles).sprite(PlayerSnowballProjectile.this.sprite, 0, textureRes);
                    final TextureDrawOptionsEnd options = cutTexture.initDraw().alpha(alpha).light(light).rotate(PlayerSnowballProjectile.this.getAngle() - 90.0f).pos(drawX, drawY - (int)PlayerSnowballProjectile.this.getHeight());
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
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(227, 241, 240), 12.0f, 150, 18.0f);
    }

    @Override
    public Color getParticleColor() {
        return new Color(227, 241, 240);
    }

    @Override
    public float getParticleChance() {
        return super.getParticleChance() * 0.5f;
    }

    @Override
    protected int getExtraSpinningParticles() {
        return 0;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int textureRes = 32;
        int halfTextureRes = textureRes / 2;
        int drawX = camera.getDrawX(this.x) - halfTextureRes;
        int drawY = camera.getDrawY(this.y) - halfTextureRes;
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.rotateBasedOnTime()).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }

    public float rotateBasedOnTime() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }

    @Override
    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.snowBallHit, (SoundEffect)SoundEffect.effect(x, y).volume(0.1f).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
    }
}

