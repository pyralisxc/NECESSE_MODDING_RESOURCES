/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobTexture;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class SlimeEggProjectile
extends Projectile {
    protected int sprite = GameRandom.globalRandom.nextInt(4);

    public SlimeEggProjectile() {
    }

    public SlimeEggProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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

    public SlimeEggProjectile(Level level, Mob owner, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this(level, owner, owner.x, owner.y, targetX, targetY, speed, distance, damage, knockback);
    }

    @Override
    public void init() {
        super.init();
        this.spawnTime = this.getWorldEntity().getTime();
        this.isSolid = false;
        this.canHitMobs = false;
        this.trailOffset = 0.0f;
    }

    @Override
    public float tickMovement(float delta) {
        float out = super.tickMovement(delta);
        float travelPerc = GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f);
        float travelPercInv = Math.abs(travelPerc - 1.0f);
        float heightF = GameMath.sin(travelPerc * 180.0f);
        this.height = (int)(heightF * 200.0f + 70.0f * travelPercInv);
        return out;
    }

    @Override
    public Color getParticleColor() {
        return new Color(177, 121, 31);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        super.doHitLogic(mob, object, x, y);
        if (!this.isServer()) {
            return;
        }
        Mob owner = this.getOwner();
        if (owner != null && !owner.removed()) {
            Mob slimeSpawn = MobRegistry.getMob("warriorslime", this.getLevel());
            slimeSpawn.isSummoned = true;
            slimeSpawn.canDespawn = false;
            if (!slimeSpawn.collidesWith(this.getLevel(), (int)x, (int)y)) {
                this.getLevel().entityManager.addMob(slimeSpawn, (int)x, (int)y);
            }
        }
    }

    @Override
    protected void spawnDeathParticles() {
        int particles = 20;
        float anglePerParticle = 360.0f / (float)particles;
        for (int i = 0; i < particles; ++i) {
            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
            int startRange = GameRandom.globalRandom.getIntBetween(0, 10);
            float startX = this.x + (float)Math.sin(Math.toRadians(angle)) * (float)startRange;
            float startY = this.y + (float)Math.cos(Math.toRadians(angle)) * (float)startRange * 0.6f;
            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(40, 60);
            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(40, 60) * 0.6f;
            this.getLevel().entityManager.addParticle(startX, startY, i % 4 == 0 ? Particle.GType.IMPORTANT_COSMETIC : Particle.GType.COSMETIC).movesFriction(dx, dy, 0.8f).colorRandom(36.0f, 0.7f, 0.6f, 10.0f, 0.1f, 0.1f).heightMoves(0.0f, 50.0f).lifeTime(1500);
        }
        SoundManager.playSound(GameResources.slimeSplash2, (SoundEffect)SoundEffect.effect(this).pitch(0.9f).volume(0.5f));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.x) - 32;
        int drawY = camera.getDrawY(this.y) - 44;
        float angle = (float)(this.getWorldEntity().getTime() - this.spawnTime) / 1.5f;
        if (this.dx < 0.0f) {
            angle = -angle;
        }
        MobTexture texture = MobRegistry.Textures.warriorSlime;
        TextureDrawOptionsEnd options = texture.body.initDraw().sprite(1, this.sprite, 64).light(light).rotate(angle, 32, 44).pos(drawX, drawY - (int)this.getHeight());
        float shadowAlpha = Math.abs(GameMath.limit(this.height / 400.0f, 0.0f, 1.0f) - 1.0f);
        int shadowX = camera.getDrawX(this.x) - 32;
        int shadowY = camera.getDrawY(this.y) - 54;
        TextureDrawOptionsEnd shadowOptions = texture.shadow.initDraw().sprite(1, this.sprite, 64).light(light).rotate(angle, 32, 54).alpha(shadowAlpha).pos(shadowX, shadowY);
        topList.add(tm -> {
            shadowOptions.draw();
            options.draw();
        });
    }
}

