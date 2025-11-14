/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.AfterimageParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;

public class KineticBootsActiveBuff
extends Buff
implements MovementTickBuff {
    public KineticBootsActiveBuff() {
        this.shouldSave = false;
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(20.0f));
        buff.setModifier(BuffModifiers.SPEED, Float.valueOf(0.8f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        Mob owner = buff.owner;
        if ((owner.dx != 0.0f || owner.dy != 0.0f) && !owner.isRiding() && owner.isPlayer) {
            owner.getLevel().entityManager.addParticle(new AfterimageParticle(owner.getLevel(), (PlayerMob)owner), Particle.GType.COSMETIC);
        }
    }

    @Override
    public void tickMovement(ActiveBuff buff, float delta) {
        Mob owner = buff.owner;
        if (owner.isClient() && (owner.dx != 0.0f || owner.dy != 0.0f)) {
            float speed = owner.getCurrentSpeed() * delta / 250.0f;
            GNDItemMap gndData = buff.getGndData();
            float soundBuffer = gndData.getFloat("soundBuffer") + Math.min(speed, 80.0f * delta / 250.0f);
            if (soundBuffer >= 45.0f) {
                soundBuffer -= 45.0f;
                SoundManager.playSound(GameResources.run, (SoundEffect)SoundEffect.effect(owner).pitch(1.2f));
                SoundManager.playSound(GameResources.laserBlast2, (SoundEffect)SoundEffect.effect(owner).volume(0.5f).pitch(2.0f));
            }
            gndData.setFloat("soundBuffer", soundBuffer);
        }
    }
}

