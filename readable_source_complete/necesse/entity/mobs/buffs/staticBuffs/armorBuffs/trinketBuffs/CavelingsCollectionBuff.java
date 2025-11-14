/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.item.trinketItem.TrinketItem;

public class CavelingsCollectionBuff
extends TrinketBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
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
        float value = 0.003f * (float)this.getUsedSlots(buff);
        if (value < 0.005f) {
            return;
        }
        buff.setModifier(BuffModifiers.ALL_DAMAGE, Float.valueOf(value));
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "cavelingscollectiontip"), 400);
        return tooltips;
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltip = new ListGameTooltips();
        tooltip.add(Localization.translate("bufftooltip", "cavelingscollectiontip", "value", this.getUsedSlotsString(ab)), 400);
        return tooltip;
    }

    private int getUsedSlots(ActiveBuff ab) {
        if (ab.owner instanceof PlayerMob) {
            PlayerInventoryManager inv = ((PlayerMob)ab.owner).getInv();
            return inv.main.getUsedSlots();
        }
        return 0;
    }

    protected String getUsedSlotsString(ActiveBuff activeBuff) {
        float number = GameMath.roundToNearest((float)this.getUsedSlots(activeBuff) * 0.3f, 1);
        return String.format("%.0f", Float.valueOf(number));
    }
}

