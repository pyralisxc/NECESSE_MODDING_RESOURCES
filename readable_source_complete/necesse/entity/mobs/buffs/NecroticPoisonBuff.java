/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class NecroticPoisonBuff
extends Buff {
    public NecroticPoisonBuff() {
        this.shouldSave = true;
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(10.0f));
    }

    public static Color getNecroticParticleColor() {
        return GameRandom.globalRandom.getOneOf(new Color(55, 22, 55), new Color(88, 22, 88), new Color(11, 22, 33), new Color(41, 122, 53), new Color(11, 88, 33));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.isVisible()) {
            owner.getLevel().entityManager.addParticle(owner.x + (float)GameRandom.globalRandom.getIntBetween(-12, 12), owner.y + (float)GameRandom.globalRandom.getIntBetween(-12, 12), Particle.GType.COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFadesInAndOut(4, 12, 0.3f).movesConstant(owner.dx / 2.0f + (float)GameRandom.globalRandom.getIntBetween(-3, 3), owner.dy / 2.0f + (float)GameRandom.globalRandom.getIntBetween(3, 3)).color(NecroticPoisonBuff.getNecroticParticleColor()).heightMoves(10.0f, GameRandom.globalRandom.getIntBetween(30, 40));
        }
    }
}

