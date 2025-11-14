/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.axeToolItem;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class AxeToolItem
extends ToolDamageItem {
    public AxeToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.setItemCategory("equipment", "tools", "axes");
        this.keyWords.add("axe");
        this.toolType = ToolType.AXE;
        this.animAttacks = 2;
        this.width = 10.0f;
    }

    @Override
    protected void addToolTooltips(ListGameTooltips tooltips) {
        tooltips.add(Localization.translate("itemtooltip", "axetip"));
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "axe");
    }
}

