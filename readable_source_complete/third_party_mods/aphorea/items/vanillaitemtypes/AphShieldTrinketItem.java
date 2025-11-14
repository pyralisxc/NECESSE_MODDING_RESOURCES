/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.enchants.Enchantable
 *  necesse.inventory.enchants.EquipmentItemEnchant
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.trinketItem.ShieldTrinketItem
 *  necesse.inventory.lootTable.presets.TrinketsLootTable
 */
package aphorea.items.vanillaitemtypes;

import aphorea.registry.AphEnchantments;
import java.util.HashSet;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

public abstract class AphShieldTrinketItem
extends ShieldTrinketItem {
    public final boolean healingEnchantments;

    public AphShieldTrinketItem(Item.Rarity rarity, int armorValue, float minSlowModifier, int msToDepleteStamina, float staminaUsageOnBlock, int damageTakenPercent, float angleCoverage, int enchantCost, boolean healingEnchantments) {
        super(rarity, armorValue, minSlowModifier, msToDepleteStamina, staminaUsageOnBlock, damageTakenPercent, angleCoverage, enchantCost, TrinketsLootTable.trinkets);
        this.healingEnchantments = healingEnchantments;
    }

    public AphShieldTrinketItem(Item.Rarity rarity, int armorValue, float minSlowModifier, int msToDepleteStamina, float staminaUsageOnBlock, int damageTakenPercent, float angleCoverage, int enchantCost) {
        this(rarity, armorValue, minSlowModifier, msToDepleteStamina, staminaUsageOnBlock, damageTakenPercent, angleCoverage, enchantCost, false);
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public EquipmentItemEnchant getRandomEnchantment(GameRandom random, InventoryItem item) {
        return (EquipmentItemEnchant)Enchantable.getRandomEnchantment((GameRandom)random, this.getValidEnchantmentIDs(item), (int)this.getEnchantmentID(item), EquipmentItemEnchant.class);
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        HashSet<Integer> enchantments = new HashSet<Integer>(super.getValidEnchantmentIDs(item));
        if (this.healingEnchantments) {
            enchantments.addAll(AphEnchantments.healingEquipmentEnchantments);
        }
        return enchantments;
    }
}

