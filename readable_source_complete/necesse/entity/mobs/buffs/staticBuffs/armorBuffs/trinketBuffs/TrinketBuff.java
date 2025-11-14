/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public abstract class TrinketBuff
extends ArmorBuff {
    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        GNDItem gndTrinketItem = ab.getGndData().getItem("trinketItem");
        if (gndTrinketItem instanceof GNDItemInventoryItem) {
            GNDItemInventoryItem gndInventoryItem = (GNDItemInventoryItem)gndTrinketItem;
            InventoryItem trinketInventoryItem = gndInventoryItem.invItem;
            if (trinketInventoryItem != null && trinketInventoryItem.item instanceof TrinketItem) {
                tooltips.addAll(this.getTrinketTooltip((TrinketItem)trinketInventoryItem.item, trinketInventoryItem, ab.owner.isPlayer ? (PlayerMob)ab.owner : null));
            }
        }
        return tooltips;
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        return new ListGameTooltips();
    }
}

