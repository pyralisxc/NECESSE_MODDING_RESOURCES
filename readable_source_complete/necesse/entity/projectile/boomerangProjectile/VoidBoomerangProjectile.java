/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.boomerangProjectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.boomerangProjectile.BoomerangProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidBoomerangProjectile
extends BoomerangProjectile {
    @Override
    public void init() {
        super.init();
        this.setWidth(10.0f);
        this.trailOffset = 0.0f;
        this.height = 18.0f;
        this.bouncing = 100;
    }

    @Override
    public Color getParticleColor() {
        return new Color(83, 0, 165);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(50, 0, 102), 20.0f, 500, 18.0f);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y) - this.texture.getHeight() / 2;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, this.texture.getHeight() / 2).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), this.texture.getHeight() / 2);
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.voidBoomerang).volume(0.3f);
    }
}

