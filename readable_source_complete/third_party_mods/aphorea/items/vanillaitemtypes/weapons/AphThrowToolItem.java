/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem
 *  necesse.inventory.lootTable.presets.ThrowWeaponsLootTable
 */
package aphorea.items.vanillaitemtypes.weapons;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.ThrowToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;

public abstract class AphThrowToolItem
extends ThrowToolItem {
    public AphThrowToolItem(int enchantCost) {
        super(enchantCost, ThrowWeaponsLootTable.throwWeapons);
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

