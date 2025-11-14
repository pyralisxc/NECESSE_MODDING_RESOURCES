/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class HydroPumpParticle
extends Particle {
    private final Color color;
    private final int fadeInTime;
    private final int stayTime;
    private final int fadeOutTime;

    public HydroPumpParticle(Level level, float x, float y, Color color, int fadeInTime, int stayTime, int fadeOutTime) {
        super(level, x, y, fadeInTime + stayTime + fadeInTime);
        this.color = color;
        this.fadeInTime = fadeInTime;
        this.stayTime = stayTime;
        this.fadeOutTime = fadeOutTime;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX()) - 24;
        int drawY = camera.getDrawY(this.getY()) - 24;
        int height = 0;
        long lifeCycleTime = this.getLifeCycleTime();
        int spriteY = 0;
        float alpha = 1.0f;
        if (lifeCycleTime < (long)this.fadeInTime) {
            alpha = (float)lifeCycleTime / (float)this.fadeInTime;
        } else if ((lifeCycleTime -= (long)this.fadeInTime) >= (long)this.stayTime) {
            alpha = (lifeCycleTime -= (long)this.stayTime) < (long)this.fadeOutTime ? Math.abs((float)lifeCycleTime / (float)this.fadeOutTime - 1.0f) : 0.0f;
        }
        int sprite = GameUtils.getAnim(lifeCycleTime, 5, 400);
        TextureDrawOptionsEnd options = GameResources.hydroPumpParticles.initDraw().sprite(sprite, spriteY, 48, 96).colorLight(this.color, light).alpha(alpha).pos(drawX, drawY - height);
        tileList.add(tm -> options.draw());
    }
}

