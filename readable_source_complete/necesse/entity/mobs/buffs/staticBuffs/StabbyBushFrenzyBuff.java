/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

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

public class StabbyBushFrenzyBuff
extends Buff {
    public StabbyBushFrenzyBuff() {
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.2f));
        buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(15.0f));
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 8;
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible()) {
            Mob owner = buff.owner;
            Rectangle selectBox = owner.getSelectBox();
            int particleMultiplier = Math.round((float)buff.getStacks() * 0.5f);
            for (int i = 0; i < particleMultiplier; ++i) {
                int xInterval = GameRandom.globalRandom.getIntBetween(-selectBox.width / 4, selectBox.width / 4);
                int yInterval = GameRandom.globalRandom.getIntBetween(-selectBox.height / 3, selectBox.height / 3);
                owner.getLevel().entityManager.addParticle(owner.x + (float)xInterval, owner.y + 8.0f + (float)yInterval, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.bubbleParticle.sprite(0, 0, 12)).height(32.0f).movesConstant((float)xInterval / 4.0f, -1.5f).color(new Color(236, 33, 2, 87)).fadesAlphaTimeToCustomAlpha(100, 100, 0.65f).size(new ParticleOption.DrawModifier(){

                    @Override
                    public void modify(SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) {
                        options.size(8, 8);
                    }
                }).lifeTime(500);
            }
        }
    }
}

