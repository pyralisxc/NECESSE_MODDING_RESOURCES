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

public class CompanionLocketBuff
extends TrinketBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    public void updateModifiers(ActiveBuff buff) {
        int maxStacks = buff.owner.buffManager.getModifier(BuffModifiers.MAX_SUMMONS);
        int usedStacks = buff.owner.buffManager.getStacks(BuffRegistry.SUMMONED_MOB);
        if (maxStacks != buff.getGndData().getInt("lastMaxStacks") || usedStacks != buff.getGndData().getInt("lastUsedStacks")) {
            float damageModifier = 0.0f;
            float speedModifier = 0.0f;
            for (int i = 0; i < maxStacks; ++i) {
                if (i < usedStacks) continue;
                float damageDelta = Math.max(1.0f - 0.1f * (float)i, 0.0f);
                float speedDelta = Math.max(0.2f - 0.02f * (float)i, 0.0f);
                if (damageDelta <= 0.0f && speedDelta <= 0.0f) break;
                damageModifier += damageDelta;
                speedModifier += speedDelta;
            }
            buff.setModifier(BuffModifiers.SUMMON_DAMAGE, Float.valueOf(damageModifier));
            buff.setModifier(BuffModifiers.SUMMONS_SPEED, Float.valueOf(speedModifier));
            buff.getGndData().setInt("lastMaxStacks", maxStacks);
            buff.getGndData().setInt("lastUsedStacks", usedStacks);
        }
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "companionlockettip"));
        return tooltips;
    }
}

