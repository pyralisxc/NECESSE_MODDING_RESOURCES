/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public class AlchemicalInterferenceBuff
extends Buff {
    public AlchemicalInterferenceBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            owner.getLevel().entityManager.addParticle(owner.x + (float)(GameRandom.globalRandom.nextGaussian() * 5.0), owner.y + 23.0f + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).color(new Color(33, 89, 15)).height(46.0f).fadesAlphaTimeToCustomAlpha(50, 50, 0.55f).size(new ParticleOption.DrawModifier(){

                @Override
                public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                    options.size(10, 10);
                }
            }).lifeTime(500);
        }
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }
}

