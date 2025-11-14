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

public class AmethystStaffGlyphCircleParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(4);
    private float alpha = 0.0f;

    public AmethystStaffGlyphCircleParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    private float getChargeUpAlpha() {
        return (float)this.getLifeCycleTime() / (float)this.lifeTime;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX());
        int drawY = camera.getDrawY(this.getY());
        long remainingLifeTime = this.getRemainingLifeTime();
        this.alpha = remainingLifeTime < 175L ? Math.max(0.0f, (float)remainingLifeTime / 175.0f) : (this.getChargeUpAlpha() != 1.0f ? this.getChargeUpAlpha() : 1.0f);
        TextureDrawOptionsEnd options = GameResources.amethystGlyphCircleParticle.initDraw().light(light).alpha(this.alpha).pos(drawX, drawY).posMiddle(drawX, drawY);
        tileList.add(tm -> options.draw());
    }
}

