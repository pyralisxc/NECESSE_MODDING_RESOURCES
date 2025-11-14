/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EmpressAcidParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(3);

    public EmpressAcidParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void init() {
        this.spawnParticles();
    }

    private void spawnParticles() {
        for (int i = 0; i < 10; ++i) {
            this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 16.0f, this.y + GameRandom.globalRandom.floatGaussian() * 12.0f, Particle.GType.IMPORTANT_COSMETIC).color(new Color(166, 204, 52)).sizeFades(5, 15).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).movesFrictionAngle(GameRandom.globalRandom.getIntBetween(0, 360), 50.0f, 0.5f).givesLight(75.0f, 0.5f);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX()) - 16;
        int drawY = camera.getDrawY(this.getY()) - 16;
        float life = this.getLifeCyclePercent();
        long remainingLifeTime = this.getRemainingLifeTime();
        float alpha = Math.max(0.0f, (float)remainingLifeTime / 500.0f);
        TextureDrawOptionsEnd options = GameResources.empressAcid.initDraw().sprite(this.sprite, 0, 32).pos(drawX, drawY).light(light).alpha(alpha);
        tileList.add(tm -> options.draw());
    }

    public void despawnNow() {
        if (this.getRemainingLifeTime() > 500L) {
            this.lifeTime = 500L;
            this.spawnTime = this.getWorldEntity().getLocalTime();
        }
    }
}

