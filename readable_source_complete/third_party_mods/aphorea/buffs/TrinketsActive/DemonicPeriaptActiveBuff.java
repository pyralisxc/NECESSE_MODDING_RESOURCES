/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobWasHitEvent
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.entity.particle.Particle$GType
 */
package aphorea.buffs.TrinketsActive;

import aphorea.AphDependencies;
import aphorea.utils.AphColors;
import java.awt.Color;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class DemonicPeriaptActiveBuff
extends Buff {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
    public boolean hasRPGMod;

    public DemonicPeriaptActiveBuff() {
        this.isVisible = false;
        this.canCancel = false;
        this.shouldSave = true;
        this.hasRPGMod = AphDependencies.checkRPGMod();
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SPEED, (Object)Float.valueOf(0.5f));
        buff.addModifier(BuffModifiers.ATTACK_SPEED, (Object)Float.valueOf(0.3f));
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.target.isHostile) {
            int heal;
            Mob owner = event.attacker.getAttackOwner();
            if (event.damageType.equals(DamageTypeRegistry.MAGIC) && (heal = (int)Math.ceil((float)event.damage * (this.hasRPGMod ? 0.002f : 0.02f))) > 0) {
                if (owner.isServer()) {
                    MobHealthChangeEvent healEvent = new MobHealthChangeEvent(owner, heal);
                    owner.getLevel().entityManager.events.add((LevelEvent)healEvent);
                }
                for (int i = 0; i < 20; ++i) {
                    int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    owner.getLevel().entityManager.addParticle((Entity)owner, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])AphColors.paletteDemonic)).heightMoves(10.0f, 30.0f).lifeTime(500);
                }
            }
        }
    }
}

