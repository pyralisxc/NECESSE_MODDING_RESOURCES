/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.NecroticPoisonBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class NecroticSlowBuff
extends Buff {
    public NecroticSlowBuff() {
        this.shouldSave = true;
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SLOW, Float.valueOf(0.25f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.isVisible()) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.getIntBetween(-12, 12), owner.y + (float)GameRandom.globalRandom.getIntBetween(-12, 12), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).movesConstant(owner.dx / 2.0f + (float)GameRandom.globalRandom.getIntBetween(-3, 3), owner.dy / 2.0f + (float)GameRandom.globalRandom.getIntBetween(3, 16)).color(NecroticPoisonBuff.getNecroticParticleColor()).heightMoves(20.0f, 0.0f);
        }
    }
}

