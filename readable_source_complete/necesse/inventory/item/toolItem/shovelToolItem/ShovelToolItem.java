/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.shovelToolItem;

import necesse.engine.localization.Localization;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.presets.ToolsLootTable;

public class ShovelToolItem
extends ToolDamageItem {
    public ShovelToolItem(int enchantCost) {
        super(enchantCost, ToolsLootTable.tools);
        this.setItemCategory("equipment", "tools", "shovels");
        this.keyWords.add("shovel");
        this.toolType = ToolType.SHOVEL;
        this.animInverted = true;
        this.animAttacks = 2;
        this.width = 10.0f;
    }

    @Override
    protected void addToolTooltips(ListGameTooltips tooltips) {
        tooltips.add(Localization.translate("itemtooltip", "shoveltip"));
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "shovel");
    }
}

