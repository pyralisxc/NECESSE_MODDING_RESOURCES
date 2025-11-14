/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
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
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RavenBeakSpearProjectile
extends Projectile {
    public RavenBeakSpearProjectile() {
    }

    public RavenBeakSpearProjectile(Level level, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, Mob owner, int attackHeight) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.setDamage(damage);
        this.setOwner(owner);
        this.setDistance(distance);
        this.height = attackHeight;
        this.piercing = 10;
    }

    @Override
    public void init() {
        super.init();
        this.piercing = 1;
        this.height = 16.0f;
        this.setWidth(45.0f, true);
        this.isSolid = true;
        this.particleRandomOffset = 5.0f;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    protected void spawnDeathParticles() {
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        float alpha = this.getFadeAlphaTime(250, 250);
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y - this.getHeight()) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle() + 47.0f, this.texture.getWidth() / 2, this.texture.getHeight() / 2).alpha(alpha).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

