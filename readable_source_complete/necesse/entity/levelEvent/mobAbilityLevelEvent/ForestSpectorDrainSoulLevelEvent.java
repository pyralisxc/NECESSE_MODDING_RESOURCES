/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class ForestSpectorDrainSoulLevelEvent
extends LevelEvent {
    public Mob startMob;
    public Mob endMob;
    public long startTime;
    public int particlesSpawned = 0;
    public int particleCount = 8;
    public int eventLifeTime = 500;
    ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC);

    public ForestSpectorDrainSoulLevelEvent() {
    }

    public ForestSpectorDrainSoulLevelEvent(Mob startMob, Mob endMob) {
        super(true);
        this.startMob = startMob;
        this.endMob = endMob;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.startMob.getUniqueID());
        writer.putNextInt(this.endMob.getUniqueID());
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startMob = GameUtils.getLevelMob(reader.getNextInt(), this.level);
        this.endMob = GameUtils.getLevelMob(reader.getNextInt(), this.level);
    }

    @Override
    public void init() {
        super.init();
        if (this.startMob == null || this.endMob == null || this.isServer()) {
            this.over();
        }
        this.startTime = this.getLocalTime();
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        long timeSinceStart = this.getLocalTime() - this.startTime;
        if (timeSinceStart > (long)this.eventLifeTime) {
            this.over();
            return;
        }
        float lifePercent = (float)timeSinceStart / (float)this.eventLifeTime;
        float expectedParticlesSpawned = lifePercent * (float)this.particleCount;
        while ((float)this.particlesSpawned < expectedParticlesSpawned) {
            ++this.particlesSpawned;
            float endMobRandomOffset = GameRandom.globalRandom.getIntBetween(-16, 16);
            float startMobRandomOffset = GameRandom.globalRandom.getIntBetween(-5, 5);
            this.level.entityManager.addParticle(this.startMob.x, this.startMob.y, this.particleTypeSwitcher.next()).moves((pos, particleDelta, lifeTime, timeAlive, particleLifePercent) -> {
                pos.x = GameMath.lerp(particleLifePercent, this.startMob.x + startMobRandomOffset, this.endMob.x + endMobRandomOffset);
                pos.y = GameMath.lerp(particleLifePercent, this.startMob.y + startMobRandomOffset + 48.0f, this.endMob.y + endMobRandomOffset + 48.0f);
            }).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).lifeTime(1500).size(new ParticleOption.DrawModifier(){

                @Override
                public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                    int size = (int)(18.0f * Math.abs(lifePercent));
                    options.size(size);
                }
            }).fadesAlpha(0.0f, 0.25f).givesLight().color(new Color(101, 248, 181)).height(64.0f);
        }
    }
}

