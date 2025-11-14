/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class OnFireBuff
extends Buff {
    public OnFireBuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.FIRE_DAMAGE_FLAT, Float.valueOf(2.0f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), owner.y + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).flameColor().givesLight(0.0f, 0.5f).height(16.0f);
        }
    }

    @Override
    public Attacker getSource(Attacker source) {
        return new FireAttacker(source);
    }

    public static class FireAttacker
    implements Attacker {
        Attacker owner;

        public FireAttacker(Attacker owner) {
            this.owner = owner;
        }

        @Override
        public GameMessage getAttackerName() {
            if (this.owner != null) {
                return this.owner.getAttackerName();
            }
            return new LocalMessage("deaths", "firename");
        }

        @Override
        public DeathMessageTable getDeathMessages() {
            return this.getDeathMessages("fire", 3);
        }

        @Override
        public Mob getFirstAttackOwner() {
            if (this.owner != null) {
                return this.owner.getAttackOwner();
            }
            return null;
        }

        @Override
        public boolean isTrapAttacker() {
            if (this.owner != null) {
                return this.owner.isTrapAttacker();
            }
            return Attacker.super.isTrapAttacker();
        }
    }
}

