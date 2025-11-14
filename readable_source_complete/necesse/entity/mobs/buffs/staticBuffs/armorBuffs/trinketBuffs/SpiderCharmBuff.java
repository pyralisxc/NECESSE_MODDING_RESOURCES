/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SpiderCharmBuff
extends TrinketBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (!event.wasPrevented && buff.owner.isServer()) {
            Mob attackOwner;
            Mob mob = attackOwner = event.attacker != null ? event.attacker.getAttackOwner() : null;
            if (attackOwner != null) {
                attackOwner.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_CHARM_POISON, attackOwner, 5.0f, (Attacker)buff.owner), true);
            }
        }
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "spidercharm"));
        return tooltips;
    }
}

