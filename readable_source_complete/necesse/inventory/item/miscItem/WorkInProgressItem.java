/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class WorkInProgressItem
extends Item {
    public WorkInProgressItem() {
        super(100);
        this.rarity = Item.Rarity.UNIQUE;
        this.worldDrawSize = 32;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("misc", "workinprogress");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("misc", "workinprogresstip"));
        return tooltips;
    }
}

