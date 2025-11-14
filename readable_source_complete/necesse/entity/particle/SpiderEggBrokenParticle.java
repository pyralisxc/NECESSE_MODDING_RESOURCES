/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderEggBrokenParticle
extends Particle {
    public GameTexture texture;

    public SpiderEggBrokenParticle(Level level, float x, float y, long lifeTime, GameTexture texture) {
        super(level, x, y, lifeTime);
        this.texture = texture;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX()) - 32;
        int drawY = camera.getDrawY(this.getY()) - 40;
        float life = this.getLifeCyclePercent();
        float alpha = 1.0f;
        if (life >= 0.9f) {
            alpha = Math.abs(GameMath.limit((life - 0.9f) * 10.0f, 0.0f, 1.0f) - 1.0f);
        }
        TextureDrawOptionsEnd options = this.texture.initDraw().sprite(0, 0, 64).light(light).alpha(alpha).pos(drawX, drawY);
        tileList.add(tm -> options.draw());
    }
}

