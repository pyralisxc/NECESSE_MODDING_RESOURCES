/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SnowballProjectile
extends Projectile {
    private int sprite;

    public SnowballProjectile() {
    }

    public SnowballProjectile(float x, float y, float targetX, float targetY, GameDamage damage, Mob owner) {
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
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(155, 155, 155), 16.0f, 150, 18.0f);
    }

    @Override
    public Color getParticleColor() {
        return new Color(155, 155, 155);
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
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
        tileList.add(tm -> shadowOptions.draw());
    }

    @Override
    public float getAngle() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }

    @Override
    protected void playHitSound(float x, float y) {
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.snowBall);
    }
}

