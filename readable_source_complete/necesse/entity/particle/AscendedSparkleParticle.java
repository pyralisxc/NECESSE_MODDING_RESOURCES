/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;

public class AscendedSparkleParticle
extends Particle {
    private final GameTexture animTexture = GameResources.ascendedSparklesParticles;
    private final int spriteRes;

    public AscendedSparkleParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
        this.spriteRes = 22;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int sprite;
        int drawX = camera.getDrawX(this.x) - this.spriteRes / 2;
        int drawY = camera.getDrawY(this.y) - this.spriteRes / 2 + 4;
        int sprites = this.animTexture.getWidth() / this.spriteRes;
        int timePerSprite = (int)this.lifeTime / 10;
        int n = sprite = (float)this.getRemainingLifeTime() / (float)this.lifeTime > 0.5f ? (int)((long)sprites * (this.getRemainingLifeTime() / this.lifeTime)) : sprites - (int)(this.getRemainingLifeTime() / (long)timePerSprite);
        if (sprite >= 0) {
            float alpha = Math.min(1.0f, this.getLifeCyclePercent() * 1.5f);
            TextureDrawOptionsEnd options = this.animTexture.initDraw().sprite(sprite, 0, this.spriteRes).alpha(alpha).pos(drawX, drawY);
            tileList.add(tm -> options.draw());
        }
    }
}

