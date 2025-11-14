/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RubyStaffGlyphParticle
extends Particle {
    private float alpha = 0.0f;
    private final Mob owner;

    public RubyStaffGlyphParticle(Level level, Mob owner, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
        this.owner = owner;
    }

    private float getChargeUpAlpha() {
        return (float)this.getLifeCycleTime() / (float)this.lifeTime;
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        if (this.owner != null) {
            this.x = this.owner.x;
            this.y = this.owner.y;
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX());
        int drawY = camera.getDrawY(this.getY());
        long remainingLifeTime = this.getRemainingLifeTime();
        this.alpha = remainingLifeTime < 175L ? Math.max(0.0f, (float)remainingLifeTime / 175.0f) : (this.getChargeUpAlpha() != 1.0f ? this.getChargeUpAlpha() : 1.0f);
        TextureDrawOptionsEnd options = GameResources.rubyGlyphParticle.initDraw().light(light).alpha(this.alpha).pos(drawX, drawY).posMiddle(drawX, drawY);
        tileList.add(tm -> options.draw());
    }

    @Override
    public void clientTick() {
        super.clientTick();
        float rndX = this.x + (float)(GameRandom.globalRandom.nextGaussian() * 40.0);
        float rndY = this.y + 40.0f + (float)(GameRandom.globalRandom.nextGaussian() * 40.0);
        this.getLevel().entityManager.addParticle(rndX, rndY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(new Color(241, 99, 130, 255)).height(46.0f).dontRotate().givesLight((int)(this.alpha * 150.0f)).movesConstant(0.0f, -2.0f).fadesAlphaTimeToCustomAlpha(250, 250, 1.0f).size((options, lifeTime, timeAlive, lifePercent) -> options.size(8)).lifeTime(750);
    }
}

