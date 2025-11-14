/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.FrozenMobImmuneBuff;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTextureSection;

public class FrozenEnemyHitLevelEvent
extends MobAbilityLevelEvent {
    public FrozenEnemyHitLevelEvent() {
    }

    public FrozenEnemyHitLevelEvent(Mob owner) {
        super(owner, GameRandom.globalRandom);
    }

    @Override
    public void init() {
        super.init();
        if (this.owner != null && this.isClient()) {
            SoundManager.playSound(GameResources.iceHit, (SoundEffect)SoundEffect.effect(this.owner).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
            for (int i = 0; i < 1; ++i) {
                Rectangle selectBox = this.owner.getSelectBox();
                GameTextureSection sprite = FrozenMobImmuneBuff.getAppropriateRandomDebrisSprite(selectBox.width, selectBox.height);
                this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), sprite, this.owner.x, this.owner.y, 20.0f, 0.0f, 0.0f), Particle.GType.IMPORTANT_COSMETIC);
            }
            ActiveBuff buff = this.owner.buffManager.getBuff(BuffRegistry.FROZEN_MOB_IMMUNE);
            if (buff != null) {
                buff.getGndData().setLong("shakeStartTime", this.owner.getLocalTime());
            }
        }
        this.over();
    }
}

