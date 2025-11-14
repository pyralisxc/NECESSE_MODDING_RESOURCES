/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.pickaxeToolItem;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class PickaxeToolItem
extends ToolDamageItem {
    public PickaxeToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "tools", "pickaxes");
        this.keyWords.add("pickaxe");
        this.toolType = ToolType.PICKAXE;
        this.animAttacks = 2;
        this.width = 10.0f;
    }

    @Override
    protected void addToolTooltips(ListGameTooltips tooltips) {
        tooltips.add(Localization.translate("itemtooltip", "pickaxetip"));
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "pickaxe");
    }
}

