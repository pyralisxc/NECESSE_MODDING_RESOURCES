/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobBeforeHitEvent
 *  necesse.entity.mobs.MobWasHitEvent
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Periapt;

import aphorea.utils.AphColors;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FrozenPeriaptBuff
extends TrinketBuff {
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});

    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.buffManager.hasBuff("freeze")) {
            buff.owner.buffManager.removeBuff("freeze", true);
        }
        if (buff.owner.buffManager.hasBuff("frostslow")) {
            buff.owner.buffManager.removeBuff("frostslow", true);
        }
    }

    public void onBeforeAttacked(ActiveBuff buff, MobBeforeHitEvent event) {
        if (event.target.buffManager.hasBuff(BuffRegistry.Debuffs.FREEZING)) {
            event.damage = event.damage.setDamage(event.damage.damage * 1.2f);
        }
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        if (!event.wasPrevented && event.attacker.getAttackOwner().isPlayer) {
            Mob owner = event.attacker.getAttackOwner();
            if (event.damageType.equals(DamageTypeRegistry.MELEE) || event.damageType.equals(DamageTypeRegistry.RANGED) || owner.buffManager.hasBuff("frozenperiaptactive")) {
                if (!event.target.buffManager.hasBuff(BuffRegistry.Debuffs.FREEZING)) {
                    event.target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FREEZING, event.target, 3000, event.attacker), false);
                }
                for (int i = 0; i < 20; ++i) {
                    int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                    float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                    owner.getLevel().entityManager.addParticle((Entity)event.target, this.particleTypeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(AphColors.ice).heightMoves(10.0f, 30.0f).lifeTime(500);
                }
            }
        }
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"frozenperiapt"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"frozenperiapt2"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"frozenperiapt3"));
        return tooltips;
    }
}

