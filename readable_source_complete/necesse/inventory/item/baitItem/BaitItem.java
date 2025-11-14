/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.baitItem;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class BaitItem
extends Item {
    public boolean sinks;
    public int fishingPower;

    public BaitItem(boolean sinks, int fishingPower) {
        super(500);
        this.sinks = sinks;
        this.fishingPower = fishingPower;
        this.setItemCategory("equipment", "bait");
        this.keyWords.add("bait");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "baittip"));
        tooltips.add(Localization.translate("itemtooltip", "fishingpower", "value", this.fishingPower + "%"));
        return tooltips;
    }

    @Override
    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        if (this.sinks) {
            return Math.max(super.getSinkingRate(entity, currentSinking), TickManager.getTickDelta(60.0f));
        }
        return super.getSinkingRate(entity, currentSinking);
    }

    @Override
    public float getMaxSinking(ItemPickupEntity entity) {
        if (this.sinks) {
            return 1.0f;
        }
        return super.getMaxSinking(entity);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "bait");
    }
}

