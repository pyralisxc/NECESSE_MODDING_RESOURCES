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

public class StormingChargeUpParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(4);
    private float alpha = 0.0f;

    public StormingChargeUpParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    private float getChargeUpAlpha() {
        return (float)this.getLifeCycleTime() / 2000.0f;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX());
        int drawY = camera.getDrawY(this.getY());
        long remainingLifeTime = this.getRemainingLifeTime();
        this.alpha = remainingLifeTime < 175L ? Math.max(0.0f, (float)remainingLifeTime / 175.0f) : (this.getChargeUpAlpha() != 1.0f ? this.getChargeUpAlpha() : 1.0f);
        TextureDrawOptionsEnd options = GameResources.stormingParticleTexture.initDraw().light(light).alpha(this.alpha).posMiddle(drawX, drawY);
        tileList.add(tm -> options.draw());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (GameRandom.globalRandom.getChance(this.alpha)) {
            float rndX = this.x + (float)(GameRandom.globalRandom.nextGaussian() * 15.0);
            float rndY = this.y + 40.0f + (float)(GameRandom.globalRandom.nextGaussian() * 15.0);
            this.getLevel().entityManager.addParticle(rndX, rndY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.particles.sprite(0, 0, 8)).color(new Color(130, 196, 196, 189)).height(46.0f).givesLight((int)(this.alpha * 150.0f)).movesConstant(0.0f, -2.0f).fadesAlphaTimeToCustomAlpha(250, 250, 1.0f).size((options, lifeTime, timeAlive, lifePercent) -> options.size(8)).lifeTime(1000);
        }
    }
}

