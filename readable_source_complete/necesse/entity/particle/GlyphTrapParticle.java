/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.tween.Easings;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GlyphTrapParticle
extends Particle {
    public int chargeTime = 100;
    public int fadeOutTime = 750;
    public GameTexture texture;
    public Color color;
    public float colorHue;
    public float alpha;

    public GlyphTrapParticle(Level level, float x, float y, GameTexture texture, Color color, float colorHue) {
        super(level, x, y, 1000L);
        this.texture = texture;
        this.color = color;
        this.colorHue = colorHue;
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < 20; ++i) {
            float length = i <= 4 ? GameRandom.globalRandom.floatGaussian() * 8.0f : GameRandom.globalRandom.floatGaussian() * 8.0f + 32.0f;
            length = GameMath.limit(length, -46.0f, 46.0f);
            float angle = GameRandom.globalRandom.getFloatBetween(0.0f, 359.0f);
            float rndX = this.x + GameMath.sin(angle) * length;
            float rndY = this.y + GameMath.cos(angle) * length;
            this.getLevel().entityManager.addParticle(rndX, rndY, Particle.GType.IMPORTANT_COSMETIC).color(this.color).dontRotate().sprite(GameResources.puffParticles.sprite(0, 0, 12)).movesConstant(0.0f, -2.0f).fadesAlphaTimeToCustomAlpha(50, 500, 1.0f).size((options, lifeTime, timeAlive, lifePercent) -> options.size(8)).lifeTime(750 + GameRandom.globalRandom.getIntBetween(-100, 100));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y, this.colorHue, 0.75f, (int)(150.0f * (1.0f - this.getLifeCyclePercent())));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX());
        int drawY = camera.getDrawY(this.getY());
        this.alpha = 1.0f;
        if (this.getLifeCycleTime() <= (long)this.chargeTime) {
            this.alpha = (float)this.getLifeCycleTime() / (float)this.chargeTime;
        }
        if (this.getLifeCycleTime() >= this.lifeTime - (long)this.fadeOutTime) {
            this.alpha = (float)(1.0 - Easings.ExpoOut.ease((double)(this.getLifeCycleTime() - this.lifeTime + (long)this.fadeOutTime) / (double)this.fadeOutTime));
        }
        TextureDrawOptionsEnd options = this.texture.initDraw().light(light).pos(drawX, drawY).color(this.color).alpha(this.alpha).posMiddle(drawX, drawY);
        tileList.add(tm -> options.draw());
    }
}

