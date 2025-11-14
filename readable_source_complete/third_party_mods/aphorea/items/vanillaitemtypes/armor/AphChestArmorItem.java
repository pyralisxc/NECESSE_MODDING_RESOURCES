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
 *  necesse.inventory.item.armorItem.ChestArmorItem
 *  necesse.inventory.lootTable.presets.BodyArmorLootTable
 */
package aphorea.items.vanillaitemtypes.armor;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.ChestArmorItem;
import necesse.inventory.lootTable.presets.BodyArmorLootTable;

public abstract class AphChestArmorItem
extends ChestArmorItem {
    public AphChestArmorItem(int armorValue, int enchantCost, Item.Rarity rarity, String bodyTextureName, String armsTextureName) {
        super(armorValue, enchantCost, rarity, bodyTextureName, armsTextureName, BodyArmorLootTable.bodyArmor);
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

