/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.axeToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.axeToolItem.AxeToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class CustomAxeToolItem
extends AxeToolItem {
    public CustomAxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.attackAnimTime.setBaseValue(attackAnimTime);
        this.toolDps.setBaseValue(toolDps);
        this.toolTier.setBaseValue(toolTier);
        this.attackDamage.setBaseValue(attackDamage);
        this.attackRange.setBaseValue(attackRange);
        this.knockback.setBaseValue(knockback);
        this.enchantCost.setUpgradedValue(1.0f, 1200);
    }

    public CustomAxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, OneOfLootItems lootTableCategory, Item.Rarity rarity) {
        this(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, lootTableCategory);
        this.rarity = rarity;
    }

    public CustomAxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, OneOfLootItems lootTableCategory, Item.Rarity rarity, int addedRange) {
        this(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, lootTableCategory);
        this.rarity = rarity;
        this.addedRange = addedRange;
    }
}

