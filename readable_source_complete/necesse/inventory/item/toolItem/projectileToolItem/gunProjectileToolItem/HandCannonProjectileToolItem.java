/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;

public class HandCannonProjectileToolItem
extends GunProjectileToolItem {
    public HandCannonProjectileToolItem() {
        super("cannonball", 1200, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(1200);
        this.attackDamage.setBaseValue(125.0f);
        this.attackDamage.setUpgradedValue(1.0f, 233.3334f);
        this.attackXOffset = 10;
        this.attackYOffset = 8;
        this.attackRange.setBaseValue(600);
        this.velocity.setBaseValue(200);
        this.controlledRange = true;
        this.controlledMinRange = 32;
        this.controlledInaccuracy = 50;
        this.resilienceGain.setBaseValue(0.0f);
        this.canBeUsedForRaids = false;
    }

    @Override
    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("itemtooltip", "handcannontip1"));
        tooltips.add(Localization.translate("itemtooltip", "handcannontip2"));
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.explosionLight).pitchVariance(1.3f);
    }
}

