/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class FrenzyOrbBuff
extends TrinketBuff {
    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "frenzyorbtip"));
        return tooltips;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        this.updateBuff(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        this.updateBuff(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        this.updateBuff(buff);
    }

    private void updateBuff(ActiveBuff buff) {
        float current = buff.getModifier(BuffModifiers.ALL_DAMAGE).floatValue();
        float next = FrenzyOrbBuff.getAttackBonusPerc((float)buff.owner.getHealth() / (float)buff.owner.getMaxHealth(), 0.1f) * 0.4f;
        if (current != (next = GameMath.toDecimals(next, 2))) {
            buff.setModifier(BuffModifiers.ALL_DAMAGE, Float.valueOf(next));
            buff.forceManagerUpdate();
        }
    }

    public static float getAttackBonusPerc(float healthPercent, float offset) {
        if (offset != 0.0f) {
            healthPercent = (offset - healthPercent) / (offset - 1.0f);
        }
        healthPercent = GameMath.limit(healthPercent, 0.0f, 1.0f);
        return Math.abs(healthPercent - 1.0f);
    }
}

