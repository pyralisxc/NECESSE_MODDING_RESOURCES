/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CirclingFishParticle
extends Particle {
    private boolean hMirror;
    private boolean vMirror;
    private GameTexture animTexture;
    private int spriteRes;

    public CirclingFishParticle(Level level, float x, float y, long lifeTime, GameTexture animTexture, int spriteRes) {
        super(level, x, y, lifeTime);
        this.animTexture = animTexture;
        this.spriteRes = spriteRes;
        this.hMirror = GameRandom.globalRandom.nextBoolean();
        this.vMirror = GameRandom.globalRandom.nextBoolean();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int timePerSprite;
        long remaining;
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.spriteRes / 2;
        int drawY = camera.getDrawY(this.y) - this.spriteRes / 2 + 4;
        int sprites = this.animTexture.getWidth() / this.spriteRes;
        int sprite = sprites - (int)((remaining = this.getRemainingLifeTime()) / (long)(timePerSprite = 125));
        if (sprite >= 0) {
            float alpha = Math.min(1.0f, this.getLifeCyclePercent() * 1.5f);
            TextureDrawOptionsEnd options = this.animTexture.initDraw().sprite(sprite, 0, this.spriteRes).mirror(this.hMirror, this.vMirror).light(light).alpha(alpha).pos(drawX, drawY);
            tileList.add(tm -> options.draw());
        }
    }
}

