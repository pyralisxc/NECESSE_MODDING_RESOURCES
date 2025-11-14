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
 *  necesse.inventory.item.trinketItem.SimpleTrinketItem
 *  necesse.inventory.lootTable.presets.ToolsLootTable
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
import necesse.inventory.item.trinketItem.SimpleTrinketItem;
import necesse.inventory.lootTable.presets.ToolsLootTable;

public class AphSimpleTrinketItem
extends SimpleTrinketItem {
    public final boolean healingEnchantments;

    public AphSimpleTrinketItem(Item.Rarity rarity, String[] buffStringIDs, int enchantCost, boolean healingEnchantments) {
        super(rarity, buffStringIDs, enchantCost, ToolsLootTable.tools);
        this.healingEnchantments = healingEnchantments;
    }

    public AphSimpleTrinketItem(Item.Rarity rarity, String[] buffStringIDs, int enchantCost) {
        this(rarity, buffStringIDs, enchantCost, false);
    }

    public AphSimpleTrinketItem(Item.Rarity rarity, String buffStringID, int enchantCost, boolean healingEnchantments) {
        this(rarity, new String[]{buffStringID}, enchantCost, healingEnchantments);
    }

    public AphSimpleTrinketItem(Item.Rarity rarity, String buffStringID, int enchantCost) {
        this(rarity, buffStringID, enchantCost, false);
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

