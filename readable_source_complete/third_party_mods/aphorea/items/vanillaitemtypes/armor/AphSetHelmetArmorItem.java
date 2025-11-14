/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.gameDamageType.DamageType
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.armorItem.SetHelmetArmorItem
 *  necesse.inventory.lootTable.presets.ArmorSetsLootTable
 *  necesse.inventory.lootTable.presets.HeadArmorLootTable
 */
package aphorea.items.vanillaitemtypes.armor;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.armorItem.SetHelmetArmorItem;
import necesse.inventory.lootTable.presets.ArmorSetsLootTable;
import necesse.inventory.lootTable.presets.HeadArmorLootTable;

public abstract class AphSetHelmetArmorItem
extends SetHelmetArmorItem {
    public AphSetHelmetArmorItem(int armorValue, DamageType damageType, int enchantCost, Item.Rarity rarity, String textureName, String setChestStringID, String setBootsStringID, String buffType) {
        super(armorValue, damageType, enchantCost, HeadArmorLootTable.headArmor, ArmorSetsLootTable.armorSets, rarity, textureName, setChestStringID, setBootsStringID, buffType);
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

