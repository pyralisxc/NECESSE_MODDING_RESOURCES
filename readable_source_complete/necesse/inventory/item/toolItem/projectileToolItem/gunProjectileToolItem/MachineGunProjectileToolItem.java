/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;

public class MachineGunProjectileToolItem
extends GunProjectileToolItem {
    public MachineGunProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 400, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(16.0f).setUpgradedValue(1.0f, 52.500015f);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.moveDist = 50;
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(400);
        this.knockback.setBaseValue(25);
        this.ammoConsumeChance = 0.5f;
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
        super.addAmmoTooltips(tooltips, item);
        tooltips.add(Localization.translate("itemtooltip", "machineguntip"));
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
        drawOptions.addedArmPosOffset(-3, 5);
    }

    @Override
    public Projectile getProjectile(InventoryItem item, BulletItem bulletItem, float x, float y, float targetX, float targetY, int range, ItemAttackerMob attackerMob) {
        return super.getProjectile(item, bulletItem, x, y + 12.0f, targetX, targetY + 12.0f, range, attackerMob);
    }
}

