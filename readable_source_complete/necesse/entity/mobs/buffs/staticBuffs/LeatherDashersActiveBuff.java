/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class LeatherDashersActiveBuff
extends Buff
implements MovementTickBuff {
    public LeatherDashersActiveBuff() {
        this.shouldSave = false;
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(10.0f));
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(0.25f));
    }

    @Override
    public void tickMovement(ActiveBuff buff, float delta) {
        Mob owner = buff.owner;
        if (owner.isClient() && (owner.dx != 0.0f || owner.dy != 0.0f)) {
            float speed = owner.getCurrentSpeed() * delta / 250.0f;
            GNDItemMap gndData = buff.getGndData();
            float particleBuffer = gndData.getFloat("particleBuffer") + speed;
            if (particleBuffer >= 15.0f) {
                particleBuffer -= 15.0f;
                float xOffset = GameRandom.globalRandom.floatGaussian() * 2.0f;
                float yOffset = GameRandom.globalRandom.floatGaussian() * 2.0f;
                boolean alternate = gndData.getBoolean("particleAlternate");
                gndData.setBoolean("particleAlternate", !alternate);
                int dir = owner.getDir();
                if (dir == 0 || dir == 2) {
                    xOffset += alternate ? 4.0f : -4.0f;
                } else {
                    yOffset += alternate ? 4.0f : -4.0f;
                }
                owner.getLevel().entityManager.addParticle(owner.x + xOffset, owner.y + yOffset - 2.0f, Particle.GType.IMPORTANT_COSMETIC).color(new Color(217, 166, 125)).sizeFadesInAndOut(10, 16, 50, 200).movesConstant(owner.dx / 10.0f, owner.dy / 10.0f).lifeTime(300).height(2.0f);
            }
            gndData.setFloat("particleBuffer", particleBuffer);
            float soundBuffer = gndData.getFloat("soundBuffer") + Math.min(speed, 80.0f * delta / 250.0f);
            if (soundBuffer >= 45.0f) {
                soundBuffer -= 45.0f;
                SoundManager.playSound(GameResources.run, (SoundEffect)SoundEffect.effect(owner).volume(0.8f).pitch(1.8f));
            }
            gndData.setFloat("soundBuffer", soundBuffer);
        }
    }
}

