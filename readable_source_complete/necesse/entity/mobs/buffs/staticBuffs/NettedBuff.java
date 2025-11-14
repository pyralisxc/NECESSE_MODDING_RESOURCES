/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.NettedParticle;
import necesse.entity.particle.Particle;

public class NettedBuff
extends Buff {
    public NettedBuff() {
        this.shouldSave = false;
        this.isVisible = true;
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.GROUNDED, true);
        buff.setModifier(BuffModifiers.FRICTION, Float.valueOf(0.0f));
        buff.setModifier(BuffModifiers.KNOCKBACK_INCOMING_MOD, Float.valueOf(0.1f));
        Mob owner = buff.owner;
        if (owner.isVisible() && owner.getLevel() != null) {
            owner.getLevel().entityManager.addParticle(new NettedParticle(owner, buff.getDurationLeft()), true, Particle.GType.CRITICAL);
        }
    }
}

