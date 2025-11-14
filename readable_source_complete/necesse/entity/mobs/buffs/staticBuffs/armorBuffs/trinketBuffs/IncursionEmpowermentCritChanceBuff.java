/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class IncursionEmpowermentCritChanceBuff
extends Buff {
    public IncursionEmpowermentCritChanceBuff() {
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.4f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible() && GameRandom.globalRandom.getChance(0.5)) {
            Mob owner = buff.owner;
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 5.0), owner.y + 23.0f + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.powerupParticle.sprite(0, 0, 8)).color(ThemeColorRegistry.EMERALD.getRandomColor()).dontRotate().height(32.0f).movesConstant(0.0f, -4.0f).fadesAlphaTimeToCustomAlpha(200, 200, 0.9f).size(new ParticleOption.DrawModifier(){

                @Override
                public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                    options.size(12, 12);
                }
            }).lifeTime(1000);
        }
    }
}

