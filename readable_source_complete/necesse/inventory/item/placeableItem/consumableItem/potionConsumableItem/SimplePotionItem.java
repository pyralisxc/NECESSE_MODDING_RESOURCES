/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.PotionConsumableItem;

public class SimplePotionItem
extends PotionConsumableItem {
    private GameMessage[] tooltips;

    public SimplePotionItem(int stackSize, Item.Rarity rarity, String buffStringID, int buffDurationSeconds, GameMessage ... tooltips) {
        super(stackSize, buffStringID, buffDurationSeconds);
        this.rarity = rarity;
        this.tooltips = tooltips;
    }

    public SimplePotionItem(int stackSize, Item.Rarity rarity, String buffStringID, int buffDurationSeconds, String ... tooltipKeys) {
        this(stackSize, rarity, buffStringID, buffDurationSeconds, SimplePotionItem.toTooltips(tooltipKeys));
    }

    private static GameMessage[] toTooltips(String ... tooltipKeys) {
        GameMessage[] out = new GameMessage[tooltipKeys.length];
        for (int i = 0; i < tooltipKeys.length; ++i) {
            out[i] = new LocalMessage("itemtooltip", tooltipKeys[i]);
        }
        return out;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        for (GameMessage tooltip : this.tooltips) {
            if (tooltip == null) continue;
            tooltips.add(tooltip.translate());
        }
        tooltips.add(this.getDurationMessage());
        return tooltips;
    }

    @Override
    public boolean isPotion() {
        return true;
    }

    @Override
    public void setSpoilTime(InventoryItem item, long spoilTime) {
        super.setSpoilTime(item, spoilTime);
    }

    @Override
    public PotionConsumableItem overridePotion(String potionStringID) {
        return super.overridePotion(potionStringID);
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "potion");
    }
}

