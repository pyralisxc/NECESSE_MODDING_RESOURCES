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

public class SmallGroundWebParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(4);

    public SmallGroundWebParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX()) - 16;
        int drawY = camera.getDrawY(this.getY()) - 16;
        float life = this.getLifeCyclePercent();
        long remainingLifeTime = this.getRemainingLifeTime();
        float alpha = Math.max(0.0f, (float)remainingLifeTime / 500.0f);
        TextureDrawOptionsEnd options = GameResources.webParticles.initDraw().sprite(this.sprite, 0, 32).color(new Color(204, 195, 177)).light(light).pos(drawX, drawY).alpha(alpha);
        tileList.add(tm -> options.draw());
    }
}

