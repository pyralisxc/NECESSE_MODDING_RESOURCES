/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class BloodGrimoireParticleLevelEvent
extends LevelEvent {
    public Mob startMob;
    public Mob endMob;
    public long startTime;
    public int particlesSpawned = 0;
    public int particleCount = 4;
    public int eventLifeTime = 500;
    ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC);

    public BloodGrimoireParticleLevelEvent() {
    }

    public BloodGrimoireParticleLevelEvent(Mob startMob, Mob endMob) {
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
            float particleStartX = this.startMob.x;
            float particleStartY = this.startMob.y;
            this.level.entityManager.addParticle(particleStartX, particleStartY, this.particleTypeSwitcher.next()).moves((pos, particleDelta, lifeTime, timeAlive, particleLifePercent) -> {
                pos.x = GameMath.lerp(particleLifePercent, particleStartX, this.endMob.x);
                pos.y = GameMath.lerp(particleLifePercent, particleStartY, this.endMob.y);
            }).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).lifeTime(1200).color(new Color(115, 0, 0)).height(16.0f);
        }
    }
}

