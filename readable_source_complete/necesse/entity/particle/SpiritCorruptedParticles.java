/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class SpiritCorruptedParticles
extends Particle {
    protected ParticleTypeSwitcher extraParticlesTypes = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
    protected int lifetime;
    protected Mob mob;

    public SpiritCorruptedParticles(Level level, Mob mob, float x, float y, int lifetime) {
        super(level, x, y, lifetime);
        this.lifetime = lifetime;
        this.mob = mob;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Particle.GType priority = this.extraParticlesTypes.next();
        int width = 50;
        this.getLevel().entityManager.addParticle(this.x + (float)GameRandom.globalRandom.getIntBetween(-width, width), this.y + (float)GameRandom.globalRandom.getIntBetween(-width, width) + 30.0f, priority).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(new Color(92, 208, 174)).height(60.0f).movesConstant(GameRandom.globalRandom.getIntBetween(-2, 2), -5.0f).ignoreLight(true).givesLight(190.0f, 0.9f).lifeTime(this.lifetime).sizeFades(4, 8);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }
}

