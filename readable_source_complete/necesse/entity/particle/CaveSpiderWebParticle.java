/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CaveSpiderWebParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(4);

    public CaveSpiderWebParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX()) - 48;
        int drawY = camera.getDrawY(this.getY()) - 48;
        float life = this.getLifeCyclePercent();
        float alpha = 1.0f;
        if (life >= 0.9f) {
            alpha = Math.abs(GameMath.limit((life - 0.9f) * 10.0f, 0.0f, 1.0f) - 1.0f);
        }
        TextureDrawOptionsEnd options = MobRegistry.Textures.giantCaveSpider.body.initDraw().sprite(this.sprite, 4, 96).light(light).alpha(alpha).pos(drawX, drawY);
        tileList.add(tm -> options.draw());
    }
}

