/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class AscendedDashBuff
extends Buff {
    public AscendedDashBuff() {
        this.shouldSave = true;
        this.isVisible = false;
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(3.0f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        for (int i = 0; i < 5; ++i) {
            buff.owner.getLevel().entityManager.addParticle(buff.owner.x + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), buff.owner.y + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedShadeParticle.sprite(0, 0, 12)).movesConstant(0.0f, -10.0f).ignoreLight(true).height(10.0f).givesLight(200.0f, 0.5f).sizeFades(12, 24).lifeTime(500);
        }
    }
}

