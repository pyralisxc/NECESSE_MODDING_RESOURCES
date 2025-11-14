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
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class WebWeaverWebParticle
extends Particle {
    private final long startupDelay;
    private final long startTime;
    public boolean startupDone = false;
    private int currentIndex = 0;
    protected ParticleTypeSwitcher extraParticlesTypes = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);

    public WebWeaverWebParticle(Level level, float x, float y, long lifeTime, long startupDelay) {
        super(level, x, y, lifeTime);
        this.startTime = this.getLevel().getWorldEntity().getLocalTime();
        this.startupDelay = startupDelay;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!this.startupDone) {
            if (this.startupDelay + this.startTime < this.getLevel().getWorldEntity().getLocalTime()) {
                this.startupDone = true;
                return;
            }
            this.currentIndex = (int)((this.getLevel().getWorldEntity().getLocalTime() - this.startTime) / (this.startupDelay / 8L));
        }
        this.spawnParticles();
    }

    private void spawnParticles() {
        float distance;
        Color color;
        float rotationSpeed;
        int particles;
        int lifetime;
        long remainingLifeTime = this.getRemainingLifeTime();
        int n = lifetime = remainingLifeTime < 500L ? (int)remainingLifeTime : 1000;
        if (this.startupDone) {
            particles = 1;
            rotationSpeed = 0.0f;
            color = new Color(255, 246, 79);
            distance = GameRandom.globalRandom.nextInt(115);
        } else {
            particles = 10;
            rotationSpeed = 15.0f;
            color = new Color(166, 204, 52);
            distance = 16 * this.currentIndex;
        }
        for (int i = 0; i < particles; ++i) {
            AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
            this.getLevel().entityManager.addParticle(this.x + GameMath.sin(currentAngle.get().floatValue()) * distance, this.y + GameMath.cos(currentAngle.get().floatValue()) * distance, this.extraParticlesTypes.next()).sprite(GameResources.magicSparkParticles.sprite(GameRandom.globalRandom.nextInt(3), 0, 22)).color(color).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * rotationSpeed / 250.0f), Float::sum).floatValue();
                float distY = distance * 0.75f;
                pos.x = this.x + GameMath.sin(angle) * distance;
                pos.y = this.y + GameMath.cos(angle) * distY;
            }).givesLight(75.0f, 0.5f).lifeTime(lifetime).sizeFades(16, 24);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd options;
        int drawX = camera.getDrawX(this.getX()) - 116;
        int drawY = camera.getDrawY(this.getY()) - 88;
        long remainingLifeTime = this.getRemainingLifeTime();
        float alpha = 1.0f;
        if (remainingLifeTime < 500L) {
            alpha = Math.max(0.0f, (float)remainingLifeTime / 500.0f);
        }
        if ((options = this.startupDone ? GameResources.spideriteStaffWeb.initDraw().sprite(0, 0, 232, 176).alpha(alpha).pos(drawX, drawY) : GameResources.spideriteStaffWeb.initDraw().sprite(1 + this.currentIndex, 0, 232, 176).alpha(alpha).pos(drawX, drawY)) != null) {
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

