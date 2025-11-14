/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.boomerangProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.ReaperMob;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ReaperScytheProjectile
extends BoomerangProjectile {
    private ReaperMob reaperMob;

    public ReaperScytheProjectile() {
    }

    public ReaperScytheProjectile(ReaperMob owner, int x, int y, int targetX, int targetY, GameDamage damage, int speed, int distance) {
        this.reaperMob = owner;
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.setDamage(damage);
        this.speed = speed;
        this.setDistance(distance);
    }

    @Override
    public void init() {
        super.init();
        this.setWidth(120.0f, true);
        this.isCircularHitbox = true;
        this.height = 18.0f;
        this.isSolid = false;
        this.piercing = 10000;
    }

    @Override
    public Color getParticleColor() {
        return null;
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        GameTexture texture = MobRegistry.Textures.reaper;
        GameTexture glowTexture = MobRegistry.Textures.reaperGlow;
        int drawX = camera.getDrawX(this.x) - 64;
        int drawY = camera.getDrawY(this.y) - 64;
        int height = (int)this.getHeight();
        float angle = -this.getAngle();
        TextureDrawOptionsEnd options = texture.initDraw().sprite(0, 4, 128).light(light).rotate(angle, 64, 64);
        int minLight = 100;
        TextureDrawOptionsEnd glow = glowTexture.initDraw().sprite(0, 4, 128).light(light.minLevelCopy(minLight)).rotate(angle, 64, 64).pos(drawX, drawY - height);
        TextureDrawOptionsEnd main = options.copy().pos(drawX, drawY - height);
        TextureDrawOptionsEnd shadow1 = options.copy().alpha(0.6f).rotate(angle + 60.0f, 64, 64).pos(drawX, drawY - height);
        TextureDrawOptionsEnd shadow2 = options.copy().alpha(0.3f).rotate(angle + 120.0f, 64, 64).pos(drawX, drawY - height);
        topList.add(tm -> {
            shadow2.draw();
            shadow1.draw();
            main.draw();
            glow.draw();
        });
    }

    @Override
    public float getAngle() {
        return super.getAngle();
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.reaperScythe);
    }

    @Override
    protected SoundSettings getMoveSound() {
        return null;
    }

    @Override
    public void remove() {
        if (!this.removed() && this.reaperMob != null) {
            this.reaperMob.setHasScythe(true);
        }
        super.remove();
    }
}

