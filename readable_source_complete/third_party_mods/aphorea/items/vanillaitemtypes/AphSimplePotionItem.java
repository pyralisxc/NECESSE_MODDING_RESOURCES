/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem
 */
package aphorea.items.vanillaitemtypes;

import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.SimplePotionItem;

public abstract class AphSimplePotionItem
extends SimplePotionItem {
    public AphSimplePotionItem(int stackSize, Item.Rarity rarity, String buffStringID, int buffDurationSeconds, String ... tooltipKeys) {
        super(stackSize, rarity, buffStringID, buffDurationSeconds, tooltipKeys);
    }

    public AphSimplePotionItem(int stackSize, Item.Rarity rarity, String buffStringID, int buffDurationSeconds, GameMessage ... tooltips) {
        super(stackSize, rarity, buffStringID, buffDurationSeconds, tooltips);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

