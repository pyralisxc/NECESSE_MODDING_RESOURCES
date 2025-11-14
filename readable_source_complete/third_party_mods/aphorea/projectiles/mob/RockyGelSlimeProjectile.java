/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.EntityDrawable
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.projectiles.mob;

import aphorea.utils.AphColors;
import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RockyGelSlimeProjectile
extends Projectile {
    private long spawnTime;
    private int sprite;

    public RockyGelSlimeProjectile() {
    }

    public RockyGelSlimeProjectile(Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
        this.knockback = knockback;
    }

    public void init() {
        super.init();
        this.setWidth(10.0f);
        this.height = 18.0f;
        this.spawnTime = this.getWorldEntity().getTime();
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0f;
        if (this.texture != null) {
            this.sprite = new GameRandom((long)this.getUniqueID()).nextInt(this.texture.getWidth() / 32);
        }
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), AphColors.rock, 16.0f, 150, 18.0f);
    }

    public Color getParticleColor() {
        return AphColors.rock;
    }

    public float getParticleChance() {
        return super.getParticleChance() * 0.5f;
    }

    protected int getExtraSpinningParticles() {
        return 0;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.removed()) {
            GameLight light = level.getLightLevel((Entity)this);
            int textureRes = 32;
            int halfTextureRes = textureRes / 2;
            int drawX = camera.getDrawX(this.x) - halfTextureRes;
            int drawY = camera.getDrawY(this.y) - halfTextureRes;
            TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY - (int)this.getHeight());
            list.add((LevelSortedDrawable)new EntityDrawable((Entity)this, (TextureDrawOptions)options){
                final /* synthetic */ TextureDrawOptions val$options;
                {
                    this.val$options = textureDrawOptions;
                    super(arg0);
                }

                public void draw(TickManager tickManager) {
                    this.val$options.draw();
                }
            });
            TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().sprite(this.sprite, 0, textureRes).light(light).rotate(this.getAngle(), halfTextureRes, halfTextureRes).pos(drawX, drawY);
            tileList.add(arg_0 -> RockyGelSlimeProjectile.lambda$addDrawables$0((TextureDrawOptions)shadowOptions, arg_0));
        }
    }

    public float getAngle() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }

    protected void playHitSound(float x, float y) {
    }

    private static /* synthetic */ void lambda$addDrawables$0(TextureDrawOptions shadowOptions, TickManager tm) {
        shadowOptions.draw();
    }
}

