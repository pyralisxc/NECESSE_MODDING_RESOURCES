/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AncestorFollowingMob
extends AttackingFollowingMob {
    public int lifeTime = 15000;

    public AncestorFollowingMob() {
        super(10);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Level level = this.getLevel();
        if (level.tickManager().getTotalTicks() % 2L == 0L) {
            level.entityManager.addParticle(this.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), this.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.COSMETIC).movesConstant(this.dx / 10.0f, this.dy / 10.0f).color(new Color(0, 222, 218)).alpha(0.5f).sizeFades(6, 10).givesLight(200.0f, 0.6f).height(16.0f);
        }
        level.lightManager.refreshParticleLightFloat(this.x, this.y, 200.0f, 0.6f, 10);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime -= 50;
        if (this.lifeTime <= 0) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameRandom random = GameRandom.globalRandom;
        float anglePerParticle = 36.0f;
        for (int i = 0; i < 10; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 20.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 20.0f;
            this.getLevel().entityManager.addParticle(this, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(random.nextInt(5), 0, 12)).sizeFades(12, 24).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).color(new Color(0, 222, 218)).alpha(0.5f).heightMoves(0.0f, 30.0f).lifeTime(1500);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath3).volume(0.5f).basePitch(1.5f);
    }

    protected TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_baby_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        int dir = this.getDir();
        return shadowTexture.initDraw().sprite(dir, 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(this).volume(0.3f));
        }
    }
}

