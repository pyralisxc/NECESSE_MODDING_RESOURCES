/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.AuraTrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.WaterAuraBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class WaterAuraTrinketBuff
extends AuraTrinketBuff {
    public WaterAuraTrinketBuff() {
        super(320);
    }

    @Override
    protected String getTooltipName() {
        return "ringofwatertip";
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void tickEffect(ActiveBuff buff, Mob owner) {
    }

    @Override
    protected Buff getAuraBuff(Mob mob) {
        return new WaterAuraBuff();
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        return tooltips;
    }
}

