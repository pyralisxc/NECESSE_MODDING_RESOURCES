/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.SniperBulletProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;

public class SniperProjectileToolItem
extends GunProjectileToolItem {
    public SniperProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 1050, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(125.0f).setUpgradedValue(1.0f, 186.66672f);
        this.attackXOffset = 20;
        this.attackYOffset = 10;
        this.attackCooldownTime.setBaseValue(1000);
        this.velocity.setBaseValue(650);
        this.moveDist = 65;
        this.attackRange.setBaseValue(1600);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected void addExtraGunTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraGunTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "snipertip"));
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.sniperrifle);
    }

    @Override
    public float zoomAmount() {
        return 300.0f;
    }

    @Override
    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, ItemAttackerMob attackerMob) {
        return new SniperBulletProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, attackerMob);
    }
}

