/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class BloodstoneRingBuff
extends TrinketBuff {
    public BloodstoneRingBuff() {
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.ARMOR_FLAT, -20);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.updateActiveBuff(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.updateActiveBuff(buff);
    }

    public void updateActiveBuff(ActiveBuff buff) {
        float healthPercent = buff.owner.getHealthPercent();
        if (healthPercent < 0.5f && buff.owner.buffManager.getBuffDurationLeftSeconds(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF) <= 1.0f) {
            ActiveBuff activeBuff = new ActiveBuff(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF, buff.owner, 4.0f, null);
            buff.owner.buffManager.addBuff(activeBuff, true);
        } else if (healthPercent >= 0.5f && buff.owner.buffManager.hasBuff(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF)) {
            buff.owner.buffManager.removeBuff(BuffRegistry.BLOODSTONE_RING_REGEN_ACTIVE_BUFF, true);
        }
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "bloodstoneringtip"), 400);
        return tooltips;
    }
}

