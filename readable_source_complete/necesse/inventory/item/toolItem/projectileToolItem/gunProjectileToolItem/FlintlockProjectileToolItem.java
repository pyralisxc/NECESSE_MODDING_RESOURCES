/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;

public class FlintlockProjectileToolItem
extends GunProjectileToolItem {
    public FlintlockProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 1150, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(400);
        this.attackDamage.setBaseValue(47.0f).setUpgradedValue(1.0f, 99.166695f);
        this.attackXOffset = 8;
        this.attackYOffset = 10;
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(450);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.flintLock).volume(0.5f);
    }
}

