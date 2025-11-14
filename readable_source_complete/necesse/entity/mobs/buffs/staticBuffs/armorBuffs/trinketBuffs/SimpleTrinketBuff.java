/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.util.Arrays;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SimpleTrinketBuff
extends TrinketBuff {
    private GameMessage[] tooltips;
    private ModifierValue<?>[] modifiers;

    public SimpleTrinketBuff(GameMessage[] tooltips, ModifierValue<?> ... modifiers) {
        this.tooltips = tooltips;
        this.modifiers = modifiers;
    }

    public SimpleTrinketBuff(GameMessage tooltip, ModifierValue<?> ... modifiers) {
        this(new GameMessage[]{tooltip}, modifiers);
    }

    public SimpleTrinketBuff(String[] tooltipKeys, ModifierValue<?> ... modifiers) {
        this((GameMessage[])Arrays.stream(tooltipKeys).map(key -> new LocalMessage("itemtooltip", (String)key)).toArray(LocalMessage[]::new), modifiers);
    }

    public SimpleTrinketBuff(String tooltipKey, ModifierValue<?> ... modifiers) {
        this(new String[]{tooltipKey}, modifiers);
    }

    public SimpleTrinketBuff(ModifierValue<?> ... modifiers) {
        this((GameMessage[])null, modifiers);
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        for (ModifierValue<?> modifier : this.modifiers) {
            modifier.apply(buff);
        }
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        if (this.tooltips == null) {
            for (ModifierValue<?> modifier : this.modifiers) {
                ModifierTooltip tooltip = modifier.getTooltip();
                if (tooltip == null) continue;
                tooltips.add(tooltip.toTooltip(false));
            }
        } else {
            for (GameMessage tooltip : this.tooltips) {
                tooltips.add(tooltip.translate());
            }
        }
        return tooltips;
    }
}

