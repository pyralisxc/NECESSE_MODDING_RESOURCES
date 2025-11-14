/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.gameNetworkData.GNDItemMap
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
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.inventory.InventoryItem
 */
package aphorea.buffs.TrinketsActive;

import aphorea.AphDependencies;
import aphorea.utils.AphColors;
import necesse.engine.network.gameNetworkData.GNDItemMap;
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
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;

public class BloodyPeriaptActiveBuff
extends Buff {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
    public boolean doLifeSteal;
    public boolean hasRPGMod;

    public BloodyPeriaptActiveBuff() {
        this.isVisible = false;
        this.canCancel = false;
        this.shouldSave = true;
        this.doLifeSteal = false;
        this.hasRPGMod = AphDependencies.checkRPGMod();
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.addModifier(BuffModifiers.SPEED, (Object)Float.valueOf(0.5f));
        buff.addModifier(BuffModifiers.ATTACK_SPEED, (Object)Float.valueOf(0.3f));
    }

    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
        String itemID = item.item.getStringID();
        if (itemID.equals("bloodbolt") || itemID.equals("bloodvolley")) {
            this.doLifeSteal = true;
        } else if (this.doLifeSteal) {
            this.doLifeSteal = false;
        }
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.damageType.equals(DamageTypeRegistry.MAGIC) && event.target.isHostile) {
            int heal;
            Mob owner = event.attacker.getAttackOwner();
            if (this.doLifeSteal && (heal = (int)Math.ceil((float)event.damage * (this.hasRPGMod ? 0.002f : 0.02f))) > 0) {
                if (owner.isServer()) {
                    MobHealthChangeEvent healEvent = new MobHealthChangeEvent(owner, heal);
                    owner.getLevel().entityManager.events.add((LevelEvent)healEvent);
                }
                for (int i = 0; i < 20; ++i) {
                    int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    owner.getLevel().entityManager.addParticle((Entity)owner, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(AphColors.blood).heightMoves(10.0f, 30.0f).lifeTime(500);
                }
            }
        }
    }
}

