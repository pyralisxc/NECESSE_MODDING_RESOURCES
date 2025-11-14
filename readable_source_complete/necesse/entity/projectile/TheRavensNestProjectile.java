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

public class TheRavensNestProjectile
extends Projectile {
    public static int HEIGHT_BOUNCE = 7;
    public static int DISTANCE_PER_BOUNCE = 250;
    protected GameRandom heightRandom = new GameRandom();

    public TheRavensNestProjectile() {
    }

    public TheRavensNestProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        this.height = 18.0f;
        this.piercing = 5;
        this.setWidth(60.0f, true);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(236, 229, 246, 182), 6.0f, 500, this.getHeight());
    }

    @Override
    public float getHeight() {
        float traveledDistance = this.traveledDistance + this.heightRandom.seeded(this.getUniqueID()).nextFloat() * (float)DISTANCE_PER_BOUNCE;
        float progress = traveledDistance % (float)DISTANCE_PER_BOUNCE / (float)DISTANCE_PER_BOUNCE;
        return this.height + (float)Math.sin((double)progress * Math.PI * 2.0) * (float)HEIGHT_BOUNCE;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        float alpha = this.getFadeAlphaTime(200, 200);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        boolean flipY = this.dy > 0.0f;
        final TextureDrawOptionsEnd options = this.texture.initDraw().mirror(false, flipY).light(light).alpha(alpha).rotate(this.getAngle() - 90.0f, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        TextureDrawOptionsEnd shadowOptions = this.shadowTexture.initDraw().mirror(false, flipY).light(light).alpha(alpha).rotate(this.getAngle() - 90.0f, this.shadowTexture.getWidth() / 2, this.shadowTexture.getHeight() / 2).pos(drawX, drawY);
        tileList.add(tickManager1 -> shadowOptions.draw());
    }

    @Override
    protected SoundSettings getMoveSound() {
        return new SoundSettings(GameResources.wind1).volume(0.1f);
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return null;
    }
}

