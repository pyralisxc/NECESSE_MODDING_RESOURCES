/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.boomerangProjectile.SpinningProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class StarVeilProjectile
extends SpinningProjectile {
    private int spriteIndex;

    public StarVeilProjectile() {
    }

    public StarVeilProjectile(float x, float y, float angle, GameDamage damage, float projectileSpeed, Mob owner) {
        this.x = x;
        this.y = y;
        this.setAngle(angle);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(1500);
        this.speed = projectileSpeed;
    }

    public StarVeilProjectile(float x, float y, float targetX, float targetY, GameDamage damage, float projectileSpeed, Mob owner) {
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(1500);
        this.speed = projectileSpeed;
    }

    @Override
    public void init() {
        super.init();
        this.height = 18.0f;
        this.piercing = 1;
        this.isSolid = false;
        this.givesLight = true;
        this.trailOffset = 0.0f;
        this.spriteIndex = GameRandom.globalRandom.getIntBetween(0, 4);
    }

    @Override
    public Color getParticleColor() {
        return new Color(184, 174, 255);
    }

    @Override
    protected void modifySpinningParticle(ParticleOption particle) {
        particle.lifeTime(200);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(184, 174, 255), 12.0f, 1000, 18.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - 15;
        int drawY = camera.getDrawY(this.y) - 15;
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.spriteIndex, 0, 30).light(light).rotate(this.getAngle() / 2.0f, 15, 15).pos(drawX, drawY - (int)this.getHeight()).alpha(this.getAngle());
        topList.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

