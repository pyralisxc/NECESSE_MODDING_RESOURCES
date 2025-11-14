/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.entity.levelEvent.explosionEvent.BoneSpikeMobExplosionLevelEvent;
import necesse.entity.mobs.ExplosiveSpikeMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EarthSpikeMob
extends ExplosiveSpikeMob {
    public EarthSpikeMob() {
    }

    public EarthSpikeMob(Mob mobOwner, GameDamage damage, long startCrackingTime) {
        super(mobOwner, damage, startCrackingTime);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void spawnExplosionEvent() {
        BoneSpikeMobExplosionLevelEvent event = new BoneSpikeMobExplosionLevelEvent(this.x, this.y, 150, this.damage, false, 0.0f, this.mobOwner);
        this.getLevel().entityManager.events.add(event);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 6; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.bigEarthSpike, i, 3, 32, this.x, this.y, 10.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(EarthSpikeMob.getTileCoordinate(x), EarthSpikeMob.getTileCoordinate(y));
        GameTexture texture = MobRegistry.Textures.bigEarthSpike;
        Point2D.Float shake = new Point2D.Float();
        if (this.crackShake != null) {
            shake = this.crackShake.getCurrentShake(this.crackAnimationStartTime, this.getTime());
        }
        int drawX = camera.getDrawX((float)x + shake.x);
        int drawY = camera.getDrawY((float)y + shake.y);
        float endY = this.getEndY(texture) - 32.0f;
        final TextureDrawOptionsEnd boneSpikeOptions = texture.initDraw().section(0, texture.getWidth() / 2, 0, (int)endY).light(light).pos(drawX - texture.getWidth() / 4, drawY - (int)endY);
        final TextureDrawOptionsEnd spikeOverlayOptions = texture.initDraw().section(texture.getWidth() / 2, texture.getWidth() * 2, 0, (int)endY).colorLight(new Color(159, 222, 201), light.minLevelCopy(150.0f * this.overlayAlpha)).alpha(this.overlayAlpha).pos(drawX - texture.getWidth() / 4, drawY - (int)endY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                boneSpikeOptions.draw();
                spikeOverlayOptions.draw();
            }
        });
    }
}

