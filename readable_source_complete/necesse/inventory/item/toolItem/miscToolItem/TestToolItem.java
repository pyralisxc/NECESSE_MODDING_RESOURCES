/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.miscToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;

public class TestToolItem
extends ToolDamageItem {
    public TestToolItem() {
        super(0, null);
        this.setItemCategory("equipment", "tools", "misc");
        this.toolType = ToolType.ALL;
        this.attackAnimTime.setBaseValue(100);
        this.toolDps.setBaseValue(10000);
        this.toolTier.setBaseValue(1.0E8f);
        this.attackDamage.setBaseValue(0.0f);
        this.attackRange.setBaseValue(200);
        this.knockback.setBaseValue(500);
        this.animInverted = true;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("Dev Cheat Tool");
    }

    @Override
    protected void addToolTooltips(ListGameTooltips tooltips) {
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add("NOT OBTAINABLE");
        return tooltips;
    }
}

