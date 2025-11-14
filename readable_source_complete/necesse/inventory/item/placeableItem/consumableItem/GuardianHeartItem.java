/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.consumableItem.HealthUpgradeItem;

public class GuardianHeartItem
extends HealthUpgradeItem {
    public GuardianHeartItem() {
        super(1);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "guardianhearttip"));
        return tooltips;
    }
}

