/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem
 *  necesse.inventory.lootTable.presets.GunWeaponsLootTable
 */
package aphorea.items.vanillaitemtypes.weapons;

import java.util.LinkedHashSet;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;

public class AphGunProjectileToolItem
extends GunProjectileToolItem {
    public AphGunProjectileToolItem(String ammoStringID, int enchantCost) {
        super(ammoStringID, enchantCost, GunWeaponsLootTable.gunWeapons);
    }

    public AphGunProjectileToolItem(LinkedHashSet<String> ammoTypes, int enchantCost) {
        super(ammoTypes, enchantCost, GunWeaponsLootTable.gunWeapons);
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }
}

