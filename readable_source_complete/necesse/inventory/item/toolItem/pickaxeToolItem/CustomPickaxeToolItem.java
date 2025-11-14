/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.pickaxeToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.pickaxeToolItem.PickaxeToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;

public class CustomPickaxeToolItem
extends PickaxeToolItem {
    public CustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.attackAnimTime.setBaseValue(attackAnimTime);
        this.toolDps.setBaseValue(toolDps);
        this.toolTier.setBaseValue(toolTier);
        this.attackDamage.setBaseValue(attackDamage);
        this.attackRange.setBaseValue(attackRange);
        this.knockback.setBaseValue(knockback);
        this.enchantCost.setUpgradedValue(1.0f, 1200);
    }

    public CustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, OneOfLootItems lootTableCategory, Item.Rarity rarity) {
        this(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, lootTableCategory);
        this.rarity = rarity;
    }

    public CustomPickaxeToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, OneOfLootItems lootTableCategory, Item.Rarity rarity, int addedRange) {
        this(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost, lootTableCategory);
        this.rarity = rarity;
        this.addedRange = addedRange;
    }
}

