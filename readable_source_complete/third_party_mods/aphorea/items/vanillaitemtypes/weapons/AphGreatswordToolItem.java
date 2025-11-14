/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.GreatswordChargeLevel
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem
 *  necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable
 */
package aphorea.items.vanillaitemtypes.weapons;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;
import necesse.inventory.lootTable.presets.GreatswordWeaponsLootTable;

public abstract class AphGreatswordToolItem
extends GreatswordToolItem {
    public AphGreatswordToolItem(int enchantCost, GreatswordChargeLevel ... chargeLevels) {
        super(enchantCost, GreatswordWeaponsLootTable.greatswordWeapons, chargeLevels);
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public static GreatswordChargeLevel[] getThreeChargeLevels(int level1Time, int level2Time, int level3Time, Color level1Color, Color level2Color, Color level3Color) {
        return new GreatswordChargeLevel[]{new GreatswordChargeLevel(level1Time, 1.0f, level1Color), new GreatswordChargeLevel(level2Time, 1.5f, level2Color), new GreatswordChargeLevel(level3Time, 2.0f, level3Color)};
    }
}

