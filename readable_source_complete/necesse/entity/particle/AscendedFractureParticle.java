/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
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
import necesse.level.maps.light.GameLight;

public class AscendedFractureParticle
extends Particle {
    public AscendedFractureParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void init() {
        this.spawnParticles();
    }

    private void spawnParticles() {
        int particleCount = 15;
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        for (int j = 0; j < particleCount; ++j) {
            this.getLevel().entityManager.addParticle(this.x + (float)random.getIntBetween(-16, 16), this.y + (float)random.getIntBetween(-16, 16), typeSwitcher.next()).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(10, 30).ignoreLight(true).movesFriction(0.0f, -150 + random.getIntBetween(0, 25), 2.0f).lifeTime(250);
        }
        SoundManager.playSound(GameResources.stomp, (SoundEffect)SoundEffect.effect(this).volume(0.8f).pitch(GameRandom.globalRandom.getFloatBetween(1.9f, 2.1f)));
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.getRemainingLifeTime() > 250L) {
            GameRandom random = GameRandom.globalRandom;
            ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
            this.getLevel().entityManager.addParticle(this.x + (float)random.getIntBetween(-16, 16), this.y + (float)random.getIntBetween(-16, 16), typeSwitcher.next()).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(5, 10).ignoreLight(true).movesFriction(0.0f, -50 + random.getIntBetween(0, 12), 2.0f).lifeTime(250);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getX() / 32, this.getY() / 32);
        int drawX = camera.getDrawX(this.getX()) - 16;
        int drawY = camera.getDrawY(this.getY()) - 16;
        long remainingLifeTime = this.getRemainingLifeTime();
        int sprite = GameMath.floor((double)remainingLifeTime / (double)this.lifeTime * 4.0);
        float alpha = GameMath.limit((float)remainingLifeTime / 250.0f, 0.0f, 1.0f);
        TextureDrawOptionsEnd options = GameResources.ascendedFractureParticle.initDraw().sprite(sprite, 0, 64).light(light.minLevelCopy(150.0f)).alpha(alpha).pos(drawX, drawY);
        tileList.add(tm -> options.draw());
    }

    public void despawnNow() {
        if (this.getRemainingLifeTime() > 250L) {
            this.lifeTime = 250L;
            this.spawnTime = this.getLocalTime();
        }
    }
}

