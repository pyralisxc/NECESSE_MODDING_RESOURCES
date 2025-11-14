/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RandomSpinningParticle
extends Particle {
    private final int spriteX;
    private final int spriteY;
    protected int height;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final LinkedList<SpinningParticleData> particles = new LinkedList();

    public RandomSpinningParticle(Level level, int spriteX, int spriteY, Color color, float x, float y, float dx, float dy, int startHeight, int lifeTime) {
        super(level, x, y, lifeTime);
        this.spriteX = spriteX;
        this.spriteY = spriteY;
        this.height = startHeight;
        this.red = (float)color.getRed() / 255.0f;
        this.green = (float)color.getGreen() / 255.0f;
        this.blue = (float)color.getBlue() / 255.0f;
        this.alpha = (float)color.getAlpha() / 255.0f;
        this.dx = dx;
        this.dy = dy;
        this.addParticle(0);
    }

    public RandomSpinningParticle(Level level, int spriteX, int spriteY, float x, float y, float dx, float dy, int startHeight, int lifeTime) {
        this(level, spriteX, spriteY, new Color(1.0f, 1.0f, 1.0f), x, y, dx, dy, startHeight, lifeTime);
    }

    public RandomSpinningParticle(Level level, int spriteX, int spriteY, float x, float y, float dx, float dy, int startHeight) {
        this(level, spriteX, spriteY, x, y, dx, dy, startHeight, 1000);
    }

    public RandomSpinningParticle(Level level, Color color, float x, float y, float dx, float dy, int startHeight, int lifeTime) {
        this(level, 0, 1, color, x, y, dx, dy, startHeight, lifeTime);
    }

    public RandomSpinningParticle(Level level, Color color, float x, float y, float dx, float dy, int startHeight) {
        this(level, 0, 1, color, x, y, dx, dy, startHeight, 1000);
    }

    public RandomSpinningParticle(Level level, int spriteX, int spriteY, float x, float y, int startHeight) {
        this(level, spriteX, spriteY, x, y, 0.0f, 0.0f, startHeight);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RandomSpinningParticle addParticle(int xOffset, int yOffset) {
        LinkedList<SpinningParticleData> linkedList = this.particles;
        synchronized (linkedList) {
            this.particles.add(new SpinningParticleData(xOffset, yOffset));
        }
        return this;
    }

    public RandomSpinningParticle addParticle(int randomOffset, float offsetXMultiplier, float offsetYMultiplier) {
        int xOffset = randomOffset == 0 ? 0 : (int)((float)GameRandom.globalRandom.getIntBetween(-randomOffset, randomOffset) * offsetXMultiplier);
        int yOffset = randomOffset == 0 ? 0 : (int)((float)GameRandom.globalRandom.getIntBetween(-randomOffset, randomOffset) * offsetYMultiplier);
        return this.addParticle(xOffset, yOffset);
    }

    public RandomSpinningParticle addParticle(int randomOffset) {
        return this.addParticle(randomOffset, 1.0f, 1.0f);
    }

    public RandomSpinningParticle addParticles(int count, int randomOffset, float offsetXMultiplier, float offsetYMultiplier) {
        for (int i = 0; i < count; ++i) {
            this.addParticle(randomOffset, offsetXMultiplier, offsetYMultiplier);
        }
        return this;
    }

    public RandomSpinningParticle addParticles(int count, int randomOffset) {
        return this.addParticles(count, randomOffset, 1.0f, 1.0f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RandomSpinningParticle clearParticles() {
        LinkedList<SpinningParticleData> linkedList = this.particles;
        synchronized (linkedList) {
            this.particles.clear();
        }
        return this;
    }

    public int getHeight(float life) {
        return this.height;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float life = this.getLifeCyclePercent();
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.getX());
        int drawY = camera.getDrawY(this.getY()) - this.getHeight(life);
        final SharedTextureDrawOptions options = new SharedTextureDrawOptions(GameResources.generatedParticlesTexture);
        LinkedList<SpinningParticleData> linkedList = this.particles;
        synchronized (linkedList) {
            for (SpinningParticleData data : this.particles) {
                data.addDrawOptions(options, drawX, drawY, life, light);
            }
        }
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                Performance.record((PerformanceTimerManager)tickManager, "spinParticle", () -> options.draw());
            }
        });
    }

    public class SpinningParticleData {
        private final int xOffset;
        private final int yOffset;
        private final int rotateOffset;
        private float rotateSpeed;
        private final float sizeMod;

        public SpinningParticleData(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.rotateSpeed = GameRandom.globalRandom.nextFloat() * 100.0f + 100.0f;
            if (GameRandom.globalRandom.nextBoolean()) {
                this.rotateSpeed = -this.rotateSpeed;
            }
            this.rotateOffset = GameRandom.globalRandom.nextInt(360);
            this.sizeMod = GameRandom.globalRandom.nextFloat() + 0.8f;
        }

        public void addDrawOptions(SharedTextureDrawOptions options, int x, int y, float lifeCyclePercent, GameLight light) {
            int drawX = x + this.xOffset;
            int drawY = y + this.yOffset;
            float rotate = lifeCyclePercent * this.rotateSpeed + (float)this.rotateOffset;
            float lifeInv = Math.abs(lifeCyclePercent - 1.0f) + 0.2f;
            int width = (int)(10.0f * this.sizeMod * lifeInv);
            int height = (int)(10.0f * this.sizeMod * lifeInv);
            options.add(GameResources.particles.sprite(RandomSpinningParticle.this.spriteX, RandomSpinningParticle.this.spriteY, 8)).colorLight(RandomSpinningParticle.this.red, RandomSpinningParticle.this.green, RandomSpinningParticle.this.blue, RandomSpinningParticle.this.alpha, light).rotate(rotate, width / 2, height / 2).size(width, height).pos(drawX - width / 2, drawY - height / 2);
        }
    }
}

