/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.matItem.MatItem
 */
package aphorea.items.vanillaitemtypes;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.MatItem;

public class AphMatItem
extends MatItem {
    public AphMatItem(int stackSize, Item.Rarity rarity) {
        super(stackSize, rarity, new String[0]);
    }

    public AphMatItem(int stackSize, String ... globalIngredients) {
        super(stackSize, globalIngredients);
    }

    public AphMatItem(int stackSize, Item.Rarity rarity, String ... globalIngredients) {
        super(stackSize, rarity, globalIngredients);
    }

    public AphMatItem(int stackSize, Item.Rarity rarity, String tooltipKey) {
        super(stackSize, rarity, tooltipKey);
    }

    public AphMatItem(int stackSize, Item.Rarity rarity, String tooltipKey, String ... globalIngredients) {
        super(stackSize, rarity, tooltipKey, globalIngredients);
    }

    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

