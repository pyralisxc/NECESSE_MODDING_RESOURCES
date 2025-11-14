/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class ElectricOrbParticle
extends Particle {
    protected ParticleTypeSwitcher extraParticlesTypes = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

    public ElectricOrbParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        long remainingLifeTime = this.getRemainingLifeTime();
        int lifetime = remainingLifeTime < 500L ? (int)remainingLifeTime : 1000;
        float rotationSpeed = 0.0f;
        Color color = new Color(95, 205, 228);
        float distance = 100.0f;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
        Particle.GType priority = this.extraParticlesTypes.next();
        this.getLevel().entityManager.addParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance, priority).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).color(color).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * rotationSpeed / 250.0f), Float::sum).floatValue();
            float distY = distance * 0.75f;
            pos.x = this.x + GameMath.sin(angle) * distance;
            pos.y = this.y + GameMath.cos(angle) * distY;
        }).ignoreLight(true).givesLight(190.0f, 0.9f).lifeTime(lifetime).sizeFades(16, 24);
        float particleX = this.x + GameRandom.globalRandom.floatGaussian() * 50.0f;
        float particleY = this.y + GameRandom.globalRandom.floatGaussian() * 35.0f;
        this.getLevel().entityManager.addParticle(particleX, particleY + 20.0f, priority).movesFriction(this.x - particleX, this.y - particleY, 0.8f).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).fadesAlpha(0.1f, 0.1f).color(new Color(95, 205, 228)).givesLight(190.0f, 0.9f).ignoreLight(true).height(20.0f).lifeTime(1000);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int drawX = camera.getDrawX(this.getX()) - 32;
        int drawY = camera.getDrawY(this.getY()) - 32;
        int index = GameUtils.getAnim(this.getWorldEntity().getTime(), 8, 400);
        TextureDrawOptionsEnd options = GameResources.electricOrb.initDraw().sprite(index, 0, 64, 64).pos(drawX, drawY);
        if (options != null) {
            tileList.add(tm -> options.draw());
        }
    }

    public void despawnNow() {
        if (this.getRemainingLifeTime() > 500L) {
            this.lifeTime = 500L;
            this.spawnTime = this.getWorldEntity().getLocalTime();
        }
    }
}

