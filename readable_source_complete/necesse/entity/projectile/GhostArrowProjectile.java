/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
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

public class GhostArrowProjectile
extends Projectile {
    protected final int textureWidth = 18;
    protected final int textureHeight = 32;

    @Override
    public void init() {
        super.init();
        this.maxMovePerTick = 16;
        this.height = 18.0f;
        this.heightBasedOnDistance = true;
        this.bouncing = 0;
        this.givesLight = true;
        this.trailParticles = 0.0;
    }

    @Override
    public Color getParticleColor() {
        return new Color(220, 235, 255, 80);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), this.getParticleColor(), 8.0f, 300, this.getHeight());
    }

    @Override
    public float getParticleChance() {
        return 0.7f;
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
        int drawX = camera.getDrawX(this.x) - 9;
        int drawY = camera.getDrawY(this.y);
        int spriteX = GameUtils.getAnim(this.getWorldEntity().getTime(), 7, 500);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(spriteX, 0, 18, 32).rotate(this.getAngle(), 9, 0).light(light).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

