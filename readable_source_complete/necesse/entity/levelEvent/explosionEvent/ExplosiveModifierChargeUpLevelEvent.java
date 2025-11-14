/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class ExplosiveModifierChargeUpLevelEvent
extends LevelEvent {
    long startTime;
    float chargeUpDuration;
    int x;
    int y;
    boolean mainParticleSpawned;
    boolean hasPlayedFuseSound;

    public ExplosiveModifierChargeUpLevelEvent() {
    }

    public ExplosiveModifierChargeUpLevelEvent(int x, int y, float duration) {
        this.x = x;
        this.y = y;
        this.chargeUpDuration = duration;
    }

    @Override
    public void init() {
        super.init();
        this.startTime = this.level.getTime();
        this.mainParticleSpawned = false;
        if (this.isServer()) {
            this.over();
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.chargeUpDuration);
        writer.putNextLong(this.startTime);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.chargeUpDuration = reader.getNextFloat();
        this.startTime = reader.getNextLong();
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!this.hasPlayedFuseSound) {
            SoundManager.playSound(GameResources.fireworkFuse, (SoundEffect)SoundEffect.effect(this.x, this.y));
            this.hasPlayedFuseSound = true;
        }
        if ((float)this.level.getTime() < (float)this.startTime + this.chargeUpDuration) {
            if (!this.mainParticleSpawned) {
                float height = 25.0f;
                this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.CRITICAL).sprite(GameResources.explosiveModifierChargeUp.sprite(0, 0, 32)).rotation(new ParticleOption.FloatGetter(){

                    @Override
                    public float get(int lifeTime, int timeAlive, float lifePercent) {
                        return (float)timeAlive * lifePercent + 0.25f;
                    }
                }).givesLight(75.0f, 0.5f).fadesAlphaTime(1500, 250).lifeTime(1750).height(height).size(new ParticleOption.DrawModifier(){

                    @Override
                    public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                        options.size(32, 32);
                    }
                });
                this.mainParticleSpawned = true;
            }
            for (int i = 0; i < 3; ++i) {
                int angle = GameRandom.globalRandom.nextInt(360);
                Point2D.Float dir = GameMath.getAngleDir(angle);
                float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
                float startX = (float)this.x + dir.x * range;
                float startY = this.y + 4;
                float endHeight = 29.0f;
                float startHeight = endHeight + dir.y * range;
                int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
                float speed = dir.x * range * 250.0f / (float)lifeTime;
                Color color1 = new Color(156, 51, 39);
                Color color2 = new Color(191, 90, 62);
                Color color3 = new Color(233, 134, 39);
                Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
                this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().givesLight(75.0f, 0.5f).heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).fadesAlphaTime(100, 50).lifeTime(lifeTime);
            }
        } else {
            this.over();
        }
    }
}

