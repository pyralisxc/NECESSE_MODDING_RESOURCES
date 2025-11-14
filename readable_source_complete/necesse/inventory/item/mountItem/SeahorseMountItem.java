/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.mountItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.mountItem.MountItem;

public class SeahorseMountItem
extends MountItem {
    public SeahorseMountItem() {
        super("seahorse");
        this.rarity = Item.Rarity.RARE;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "calloftheseatip"));
        return tooltips;
    }
}

