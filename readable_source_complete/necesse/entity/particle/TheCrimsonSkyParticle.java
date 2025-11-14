/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

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

public class TheCrimsonSkyParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(4);

    public TheCrimsonSkyParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    public void despawnNow() {
        if (this.getRemainingLifeTime() > 500L) {
            this.lifeTime = 500L;
            this.spawnTime = this.getWorldEntity().getLocalTime();
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX());
        int drawY = camera.getDrawY(this.getY());
        long remainingLifeTime = this.getRemainingLifeTime();
        float alpha = 1.0f;
        if (remainingLifeTime < 500L) {
            alpha = Math.max(0.0f, (float)remainingLifeTime / 500.0f);
        }
        TextureDrawOptionsEnd options = GameResources.theCrimsonSkyBloodPool.initDraw().light(light).alpha(alpha).posMiddle(drawX, drawY);
        tileList.add(tm -> options.draw());
    }
}

