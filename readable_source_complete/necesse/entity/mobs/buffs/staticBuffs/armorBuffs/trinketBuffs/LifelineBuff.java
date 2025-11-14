/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketLifelineEvent;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.MobBeforeDamageOverTimeTakenEvent;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.level.maps.Level;

public class LifelineBuff
extends TrinketBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        eventSubscriber.subscribeEvent(MobBeforeDamageOverTimeTakenEvent.class, event -> {
            if (this.runLifeLineLogic(buff, event.getExpectedHealth())) {
                event.prevent();
            }
        });
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "lifeline"));
        return tooltips;
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(buff, event);
        if (this.runLifeLineLogic(buff, event.getExpectedHealth())) {
            event.prevent();
        }
    }

    protected boolean runLifeLineLogic(ActiveBuff buff, int expectedHealth) {
        Level level = buff.owner.getLevel();
        if (level.isServer() && !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.LIFELINE_COOLDOWN.getID()) && expectedHealth <= 0) {
            buff.owner.setHealth(Math.max(10, buff.owner.getMaxHealth() / 4));
            buff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.LIFELINE_COOLDOWN, buff.owner, 300.0f, null), true);
            level.getServer().network.sendToClientsWithEntity(new PacketLifelineEvent(buff.owner.getUniqueID()), buff.owner);
            return true;
        }
        return false;
    }
}

