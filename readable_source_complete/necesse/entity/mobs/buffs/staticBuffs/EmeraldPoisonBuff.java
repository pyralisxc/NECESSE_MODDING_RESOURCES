/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;

public class EmeraldPoisonBuff
extends Buff {
    public EmeraldPoisonBuff() {
        this.shouldSave = false;
        this.isImportant = true;
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        GameRandom random = GameRandom.globalRandom;
        if (owner.isVisible()) {
            float colorMod = random.getFloatBetween(0.7f, 1.0f);
            owner.getLevel().entityManager.addParticle(owner.x + (float)(random.nextGaussian() * 6.0), owner.y + (float)(random.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(random.nextInt(5), 0, 12)).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).sizeFades(24, 48).fadesAlpha(0.2f, 0.2f).height(16.0f).color(ThemeColorRegistry.EMERALD.getRandomColor());
        }
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(5.0f));
    }
}

