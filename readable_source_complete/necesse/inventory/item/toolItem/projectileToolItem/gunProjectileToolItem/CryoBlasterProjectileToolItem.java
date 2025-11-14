/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;
import necesse.level.maps.incursion.IncursionData;

public class CryoBlasterProjectileToolItem
extends GunProjectileToolItem {
    public CryoBlasterProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 1500, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(55.0f).setUpgradedValue(1.0f, 75.83335f);
        this.attackXOffset = 10;
        this.attackYOffset = 12;
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(550);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return super.animDrawBehindHand(item);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.cryoBlaster).volume(0.22f);
    }
}

