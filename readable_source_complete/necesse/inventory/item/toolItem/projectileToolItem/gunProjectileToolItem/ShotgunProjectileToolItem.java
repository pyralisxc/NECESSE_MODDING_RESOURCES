/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.BulletItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;
import necesse.level.maps.Level;

public class ShotgunProjectileToolItem
extends GunProjectileToolItem {
    public ShotgunProjectileToolItem() {
        super(NORMAL_AMMO_TYPES, 700, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(750);
        this.attackDamage.setBaseValue(17.0f).setUpgradedValue(1.0f, 44.333344f);
        this.attackXOffset = 18;
        this.attackYOffset = 20;
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(350);
        this.moveDist = 20;
        this.resilienceGain.setBaseValue(0.3f);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = true;
        this.useForRaidsOnlyIfObtained = true;
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

    @Override
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, BulletItem bullet, boolean dropItem, GNDItemMap mapContent) {
        int range;
        GameRandom random = new GameRandom(seed);
        GameRandom spreadRandom = new GameRandom(seed + 10);
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(spreadRandom, attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance(x, y);
        } else {
            range = this.getAttackRange(item);
        }
        for (int i = 0; i <= 3; ++i) {
            Projectile projectile = this.getProjectile(item, bullet, attackerMob.x, attackerMob.y, x, y, range, attackerMob);
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.dropItem = dropItem;
            projectile.getUniqueID(random);
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist, (spreadRandom.nextFloat() - 0.5f) * 20.0f);
        }
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.shotgun);
    }
}

