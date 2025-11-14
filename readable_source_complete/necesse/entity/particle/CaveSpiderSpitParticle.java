/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.GiantCaveSpiderMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CaveSpiderSpitParticle
extends Particle {
    public GiantCaveSpiderMob.Variant variant;
    public int sprite = GameRandom.globalRandom.nextInt(4);

    public CaveSpiderSpitParticle(Level level, float x, float y, long lifeTime, GiantCaveSpiderMob.Variant variant) {
        super(level, x, y, lifeTime);
        this.variant = variant;
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
        int drawX = camera.getDrawX(this.getX()) - 48;
        int drawY = camera.getDrawY(this.getY()) - 48;
        long remainingLifeTime = this.getRemainingLifeTime();
        float alpha = 1.0f;
        if (remainingLifeTime < 500L) {
            alpha = Math.max(0.0f, (float)remainingLifeTime / 500.0f);
        }
        TextureDrawOptionsEnd options = this.variant.texture.get().body.initDraw().sprite(4 + this.sprite, 4, 96).light(light).alpha(alpha).pos(drawX, drawY);
        tileList.add(tm -> options.draw());
    }
}

