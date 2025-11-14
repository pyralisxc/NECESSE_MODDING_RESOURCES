/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.RicochetableProjectile;
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

public class CarapaceDaggerProjectile
extends FollowingProjectile
implements RicochetableProjectile {
    public CarapaceDaggerProjectile() {
    }

    public CarapaceDaggerProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance, GameDamage damage, int knockback) {
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
        this.setWidth(10.0f);
        this.turnSpeed = 0.3f;
        this.height = 18.0f;
        this.spawnTime = this.getWorldEntity().getTime();
        this.heightBasedOnDistance = true;
        this.trailOffset = 0.0f;
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(187, 187, 187), 14.0f, 250, 18.0f);
    }

    @Override
    public Color getParticleColor() {
        return new Color(95, 82, 44);
    }

    @Override
    public void updateTarget() {
        if (this.traveledDistance > 50.0f) {
            this.findTarget(m -> this.getOwner().isHostile || m.isHostile, 0.0f, 250.0f);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getWorldEntity().getTime() - this.spawnTime, this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getWorldEntity().getTime() - this.spawnTime, this.texture.getHeight() / 2);
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.carapaceDagger).volume(0.55f);
    }
}

