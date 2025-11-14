/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class DryadHauntedBuff
extends Buff {
    public DryadHauntedBuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 10;
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(GameRandom.globalRandom.nextFloat() * 360.0f));
            float distance = 20.0f;
            owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin(currentAngle.get().floatValue()) * distance, owner.y + GameMath.cos(currentAngle.get().floatValue()) * distance * 0.75f, Particle.GType.CRITICAL).color(new Color(30, 177, 143)).height(0.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 50.0f / 250.0f), Float::sum).floatValue();
                float distY = distance * 0.75f;
                pos.x = owner.x + GameMath.sin(angle) * distance;
                pos.y = owner.y + GameMath.cos(angle) * distY * 0.75f;
            }).lifeTime(500).sizeFades(16, 24);
        }
    }
}

