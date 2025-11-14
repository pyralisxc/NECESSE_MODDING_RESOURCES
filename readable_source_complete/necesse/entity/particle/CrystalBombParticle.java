/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
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

public class CrystalBombParticle
extends Particle {
    public int sprite = GameRandom.globalRandom.nextInt(3);

    public CrystalBombParticle(Level level, float x, float y, long lifeTime) {
        super(level, x, y, lifeTime);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(this.getX()) - GameResources.crystalBomb.getWidth() / 2;
        int drawY = camera.getDrawY(this.getY()) - GameResources.crystalBomb.getHeight() / 2;
        float life = this.getLifeCyclePercent();
        long remainingLifeTime = this.getRemainingLifeTime();
        float alpha = Math.max(0.0f, (float)remainingLifeTime / 500.0f);
        TextureDrawOptionsEnd options = GameResources.crystalBomb.initDraw().sprite(0, 0, 42, 58).pos(drawX, drawY).light(light).alpha(alpha);
        tileList.add(tm -> options.draw());
    }

    public void despawnNow() {
        this.lifeTime = 0L;
        this.spawnTime = this.getWorldEntity().getLocalTime();
        GameRandom random = GameRandom.globalRandom;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        float anglePerParticle = 36.0f;
        for (int i = 0; i < 10; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 50.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 50.0f;
            this.getLevel().entityManager.addParticle(this.x, this.y, typeSwitcher.next()).sprite(GameResources.pearlescentShardParticles.sprite(random.nextInt(4), 0, 18, 24)).sizeFades(22, 44).movesFriction(dx * random.getFloatBetween(1.0f, 2.0f), dy * random.getFloatBetween(1.0f, 2.0f), 0.8f).heightMoves(0.0f, -30.0f).lifeTime(500);
        }
    }
}

