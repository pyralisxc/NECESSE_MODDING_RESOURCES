/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.GreatbowWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class TungstenGreatbowProjectileToolItem
extends GreatbowProjectileToolItem {
    public TungstenGreatbowProjectileToolItem() {
        super(1300, GreatbowWeaponsLootTable.greatbowWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(120.0f).setUpgradedValue(1.0f, 170.33337f);
        this.attackRange.setBaseValue(1200);
        this.velocity.setBaseValue(400);
        this.attackXOffset = 10;
        this.attackYOffset = 36;
        this.particleColor = new Color(56, 70, 84);
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
    }
}

