/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SeedGunProjectileToolItem
 */
package aphorea.items.tools.weapons.range.blowgun;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.SeedGunProjectileToolItem;

public abstract class AphBlowgunToolItem
extends SeedGunProjectileToolItem {
    public AphBlowgunToolItem(int enchantCost) {
        this.enchantCost.setBaseValue(enchantCost).setUpgradedValue(1.0f, 2000);
        this.keyWords.add("blowgun");
        this.keyWords.remove("gun");
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"blowgun");
    }
}

