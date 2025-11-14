/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class SoldierFrenzyBuff
extends Buff {
    public SoldierFrenzyBuff() {
        this.isImportant = true;
        this.isVisible = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        float speedModifier = 0.05f;
        buff.setModifier(BuffModifiers.ATTACK_SPEED, Float.valueOf(speedModifier));
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(speedModifier));
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 3;
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible() && GameRandom.globalRandom.getChance(0.5)) {
            Mob owner = buff.owner;
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 5.0), owner.y + 23.0f + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.particles.sprite(0, 0, 8)).color(new Color(182, 57, 12)).dontRotate().height(32.0f).movesConstant(0.0f, -4.0f).fadesAlphaTimeToCustomAlpha(200, 200, 0.9f).size(new ParticleOption.DrawModifier(){

                @Override
                public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                    options.size(12, 12);
                }
            }).lifeTime(500);
        }
    }
}

