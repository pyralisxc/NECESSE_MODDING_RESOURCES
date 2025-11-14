/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.WebbedGunBulletProjectile;
import necesse.gfx.GameResources;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;

public class WebbedGunProjectileToolItem
extends GunProjectileToolItem {
    public WebbedGunProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 550, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(26.0f).setUpgradedValue(1.0f, 116.6667f);
        this.attackXOffset = 12;
        this.attackYOffset = 14;
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(400);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.handgun).volume(0.4f);
    }

    @Override
    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, ItemAttackerMob attackerMob) {
        return new WebbedGunBulletProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, attackerMob);
    }
}

