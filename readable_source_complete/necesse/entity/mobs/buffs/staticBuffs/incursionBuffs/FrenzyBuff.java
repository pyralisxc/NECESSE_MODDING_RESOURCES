/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import java.awt.Color;
import java.awt.Rectangle;
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

public class FrenzyBuff
extends Buff {
    public FrenzyBuff() {
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        float speedModifier = 0.5f;
        if (buff.owner != null && buff.owner.isBoss()) {
            speedModifier = 0.25f;
        }
        buff.setModifier(BuffModifiers.ATTACK_SPEED, Float.valueOf(speedModifier));
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(speedModifier));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            Rectangle selectBox = owner.getSelectBox();
            double heightAndWidth = Math.floor((float)(selectBox.width + selectBox.height) / 100.0f);
            double particleIterations = 1.0;
            if (heightAndWidth >= 1.0) {
                particleIterations = heightAndWidth;
            }
            int i = 0;
            while ((double)i < particleIterations) {
                int xInterval = GameRandom.globalRandom.getIntBetween(-selectBox.width / 2, selectBox.width / 2);
                int yInterval = GameRandom.globalRandom.getIntBetween(-selectBox.height / 2, selectBox.height / 2);
                owner.getLevel().entityManager.addParticle(owner.x + (float)xInterval, owner.y - 32.0f + (float)yInterval, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.particles.sprite(0, 0, 8)).color(new Color(182, 39, 20)).fadesAlphaTimeToCustomAlpha(100, 100, 0.65f).size(new ParticleOption.DrawModifier(){

                    @Override
                    public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                        options.size(10, 10);
                    }
                }).lifeTime(500);
                ++i;
            }
        }
    }
}

