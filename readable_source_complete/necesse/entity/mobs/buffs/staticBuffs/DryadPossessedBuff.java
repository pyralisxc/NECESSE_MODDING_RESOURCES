/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class DryadPossessedBuff
extends Buff {
    public DryadPossessedBuff() {
        this.shouldSave = false;
        this.isVisible = true;
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.GROUNDED, true);
        buff.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(5.0f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        GameRandom random = GameRandom.globalRandom;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
        float distance = 25.0f;
        Level level = buff.owner.getLevel();
        float x = buff.owner.x;
        float y = buff.owner.y;
        level.entityManager.addTopParticle(x + GameMath.sin(currentAngle.get().floatValue()) * distance, y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(random.nextInt(5), 0, 12)).color(new Color(30, 177, 143)).givesLight(247.0f, 0.3f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 15.0f / 250.0f), Float::sum).floatValue();
            pos.x = x + GameMath.sin(angle) * distance;
            pos.y = y + (x - pos.x) - angle / 10.0f + GameMath.cos(angle) * distance;
        }).lifeTime(1000).sizeFades(12, 24);
        level.entityManager.addTopParticle(x + GameMath.sin(currentAngle.get().floatValue()) * distance, y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.puffParticles.sprite(random.nextInt(5), 0, 12)).color(new Color(30, 177, 143)).givesLight(247.0f, 0.3f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
            float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 15.0f / 250.0f), Float::sum).floatValue();
            pos.x = x + GameMath.sin(angle) * distance;
            pos.y = y - (x - pos.x) - angle / 10.0f + GameMath.cos(angle) * distance;
        }).lifeTime(1000).sizeFades(12, 24);
    }
}

