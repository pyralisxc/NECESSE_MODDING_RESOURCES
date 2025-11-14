/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
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

public class GlacialBoomerangProjectile
extends FollowingProjectile {
    public GlacialBoomerangProjectile() {
    }

    public GlacialBoomerangProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(damage);
        this.knockback = knockback;
    }

    @Override
    public void init() {
        super.init();
        this.isBoomerang = true;
        this.bouncing = 10;
        this.turnSpeed = 0.5f;
        this.height = 18.0f;
        this.setWidth(8.0f);
        this.spawnTime = this.getWorldEntity().getTime();
        this.trailOffset = 0.0f;
    }

    @Override
    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return super.getTurnSpeed(targetX, targetY, delta);
    }

    @Override
    public Color getParticleColor() {
        return new Color(109, 137, 222);
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
    protected void modifySpinningParticle(ParticleOption particle) {
        particle.lifeTime(1000);
    }

    @Override
    public void updateTarget() {
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        float rotate = this.getWorldEntity().getTime() - this.spawnTime;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(rotate, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getHeight() / 2);
    }

    @Override
    protected void playHitSound(float x, float y) {
        SoundManager.playSound(GameResources.jinglehit, (SoundEffect)SoundEffect.effect(x, y));
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.glacialBoomerang).volume(0.2f);
    }

    @Override
    protected SoundSettings getMoveSound() {
        return new SoundSettings(GameResources.glacialBoomerangHold).volume(0.05f);
    }
}

