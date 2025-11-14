/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;

public abstract class AuraBuff
extends Buff {
    public float particlesPerSecond = 20.0f;
    public int particleLifeTime = 2000;
    public int particleMaxHeight = 10;
    public int particleRadius = 20;

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public abstract Color getParticleColor();

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        GNDItemMap gndData = buff.getGndData();
        float particleBuffer = gndData.getFloat("particleBuffer");
        particleBuffer += this.particlesPerSecond / 20.0f;
        while (particleBuffer >= 1.0f) {
            particleBuffer -= 1.0f;
            this.spawnAuraParticle(buff);
        }
        gndData.setFloat("particleBuffer", particleBuffer);
    }

    public void spawnAuraParticle(ActiveBuff buff) {
        float height = GameRandom.globalRandom.getFloatBetween(0.0f, this.particleMaxHeight);
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
        ParticleOption particle = buff.owner.getLevel().entityManager.addParticle(buff.owner.x + GameMath.sin(currentAngle.get().floatValue()) * (float)this.particleRadius, buff.owner.y + GameMath.cos(currentAngle.get().floatValue()) * (float)this.particleRadius * 0.75f, Particle.GType.CRITICAL).color(this.getParticleColor()).heightMoves(height, height + 20.0f).lifeTime(this.particleLifeTime).sizeFades(12, 16);
        AtomicReference<Float> shouldRemoveBuffer = new AtomicReference<Float>(Float.valueOf(1.0f));
        particle.moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            if (buff.owner.removed() || buff.isRemoved()) {
                float nextRemoveBuffer = ((Float)shouldRemoveBuffer.get()).floatValue() - delta / 250.0f * 2.0f;
                shouldRemoveBuffer.set(Float.valueOf(nextRemoveBuffer));
                if (nextRemoveBuffer <= 0.0f) {
                    particle.remove();
                }
            }
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 150.0f / 250.0f), Float::sum).floatValue();
            float radius = (float)this.particleRadius * GameMath.lerp(lifePercent, 0.5f, 1.0f);
            float distY = radius * 0.75f;
            pos.x = buff.owner.x + GameMath.sin(angle) * radius;
            pos.y = buff.owner.y + GameMath.cos(angle) * distY * 0.75f;
        });
        particle.modify((options, lifeTime, timeAlive, lifePercent) -> {
            float finalWidth = (float)options.getWidth() * ((Float)shouldRemoveBuffer.get()).floatValue();
            float finalHeight = (float)options.getHeight() * ((Float)shouldRemoveBuffer.get()).floatValue();
            options.size((int)finalWidth, (int)finalHeight);
        });
    }
}

