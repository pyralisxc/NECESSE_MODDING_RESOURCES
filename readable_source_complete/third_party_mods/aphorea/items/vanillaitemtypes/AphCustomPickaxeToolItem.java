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
 *  necesse.inventory.item.toolItem.pickaxeToolItem.CustomPickaxeToolItem
 *  necesse.inventory.lootTable.presets.ToolsLootTable
 */
package aphorea.items.vanillaitemtypes;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.pickaxeToolItem.CustomPickaxeToolItem;
import necesse.inventory.lootTable.presets.ToolsLootTable;

public class AphCustomPickaxeToolItem
extends CustomPickaxeToolItem {
    public AphCustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost) {
        super(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, ToolsLootTable.tools);
    }

    public AphCustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, Item.Rarity rarity) {
        super(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, ToolsLootTable.tools, rarity);
    }

    public AphCustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, Item.Rarity rarity, int addedRange) {
        super(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, ToolsLootTable.tools, rarity, addedRange);
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

