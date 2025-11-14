/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.shovelToolItem;

import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.shovelToolItem.ShovelToolItem;

public class CustomShovelToolItem
extends ShovelToolItem {
    public CustomShovelToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost) {
        super(enchantCost);
        this.attackAnimTime.setBaseValue(attackAnimTime);
        this.toolDps.setBaseValue(toolDps);
        this.toolTier.setBaseValue(toolTier);
        this.attackDamage.setBaseValue(attackDamage);
        this.attackRange.setBaseValue(attackRange);
        this.knockback.setBaseValue(knockback);
        this.enchantCost.setUpgradedValue(1.0f, 1200);
    }

    public CustomShovelToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, Item.Rarity rarity) {
        this(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost);
        this.rarity = rarity;
    }

    public CustomShovelToolItem(int attackAnimTime, int toolDps, float toolTier, int attackDamage, int attackRange, int knockback, int enchantCost, Item.Rarity rarity, int addedRange) {
        this(attackAnimTime, toolDps, toolTier, attackDamage, attackRange, knockback, enchantCost);
        this.rarity = rarity;
        this.addedRange = addedRange;
    }
}

