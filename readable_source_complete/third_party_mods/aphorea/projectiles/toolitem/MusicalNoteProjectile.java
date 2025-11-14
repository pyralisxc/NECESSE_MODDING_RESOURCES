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
package aphorea.projectiles.toolitem;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;
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

public class MusicalNoteProjectile
extends Projectile {
    private int type;

    public MusicalNoteProjectile() {
    }

    public MusicalNoteProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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

    public void init() {
        super.init();
        this.setWidth(8.0f);
        this.height = 18.0f;
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0f;
        GameRandom gameRandom = new GameRandom((long)this.getUniqueID());
        this.type = gameRandom.nextInt(this.texture.getWidth() / 32);
        this.bouncing = 2;
    }

    public Trail getTrail() {
        return null;
    }

    protected Color getWallHitColor() {
        return null;
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel((Entity)this);
        int drawX = camera.getDrawX(this.x) - 16;
        int drawY = camera.getDrawY(this.y);
        TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.type, 0, 32).light(light).pos(drawX, drawY - (int)this.getHeight());
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
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getWidth() / 6, 2);
    }

    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, float angle, int centerX, int centerY) {
        this.addShadowDrawables(list, drawX, drawY, light, o -> o);
    }

    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, float angle, int centerY) {
        this.addShadowDrawables(list, drawX, drawY, light, o -> o);
    }

    protected void addShadowDrawables(OrderableDrawables list, int drawX, int drawY, GameLight light, Function<TextureDrawOptionsEnd, TextureDrawOptionsEnd> modifier) {
        this.addShadowDrawables(list, this.shadowTexture.initDraw().sprite(this.type, 0, 32), drawX, drawY, light, modifier);
    }

    public float getHeight() {
        float frequency = 0.05f;
        float amplitude = 10.0f;
        float wave = (float)Math.cos(this.traveledDistance * frequency);
        return this.height + wave * amplitude;
    }
}

