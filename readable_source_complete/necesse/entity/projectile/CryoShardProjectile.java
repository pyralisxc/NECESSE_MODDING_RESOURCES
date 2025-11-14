/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CryoShardProjectile
extends Projectile {
    public GameTextureSection shardTexture;

    public CryoShardProjectile() {
    }

    public CryoShardProjectile(float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDistance(distance);
        this.setDamage(damage);
        this.knockback = knockback;
        this.setOwner(owner);
    }

    @Override
    public void init() {
        super.init();
        this.isSolid = false;
        this.setWidth(10.0f);
        this.shardTexture = MobRegistry.Textures.cryoQueen == null ? null : new GameTextureSection(MobRegistry.Textures.cryoQueen).sprite(GameRandom.globalRandom.nextInt(5), 16, 32);
    }

    @Override
    public Color getParticleColor() {
        return new Color(64, 151, 234);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 12.0f, 200, this.getHeight());
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.shardTexture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.shardTexture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.shardTexture.initDraw().light(light).rotate(this.getAngle(), this.shardTexture.getWidth() / 2, this.shardTexture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public float getAngle() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }
}

