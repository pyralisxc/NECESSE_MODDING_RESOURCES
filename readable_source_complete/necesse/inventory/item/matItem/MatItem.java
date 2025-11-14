/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.matItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class MatItem
extends Item {
    protected String tooltipKey = null;
    protected int tooltipMaxLength = -1;

    public MatItem(int stackSize, String ... globalIngredients) {
        super(stackSize);
        this.dropsAsMatDeathPenalty = true;
        this.setItemCategory("materials");
        this.setItemCategory(ItemCategory.craftingManager, "materials");
        this.keyWords.add("material");
        this.addGlobalIngredient(globalIngredients);
    }

    public MatItem(int stackSize, Item.Rarity rarity, String ... globalIngredients) {
        this(stackSize, globalIngredients);
        this.rarity = rarity;
    }

    public MatItem(int stackSize, Item.Rarity rarity, String tooltipKey) {
        this(stackSize, rarity, new String[0]);
        this.tooltipKey = tooltipKey;
    }

    public MatItem(int stackSize, Item.Rarity rarity, String tooltipKey, String ... globalIngredients) {
        this(stackSize, rarity, globalIngredients);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (this.tooltipKey != null) {
            tooltips.add(Localization.translate("itemtooltip", this.tooltipKey), this.tooltipMaxLength);
        }
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "material");
    }
}

