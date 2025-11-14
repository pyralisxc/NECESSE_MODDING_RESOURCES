/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.attackHandler.MouseProjectileAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.level.maps.Level;

public class MouseTestProjectileToolItem
extends MagicProjectileToolItem {
    public MouseTestProjectileToolItem() {
        super(600, null);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(13.0f);
        this.velocity.setBaseValue(120);
        this.attackXOffset = 6;
        this.attackYOffset = 8;
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(1000);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new StaticMessage("Mouse Projectile Test");
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = ProjectileRegistry.getProjectile("mousetest", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getProjectileVelocity(item, attackerMob), 100, this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
        projectile.resetUniqueID(new GameRandom(seed));
        if (attackerMob.isAttackHandlerFrom(item, slot)) {
            ((MouseProjectileAttackHandler)attackerMob.getAttackHandler()).addProjectiles((FollowingProjectile)projectile);
        } else {
            attackerMob.startAttackHandler(new MouseProjectileAttackHandler(attackerMob, slot, this.getAttackRange(item), (FollowingProjectile)projectile));
        }
        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        return item;
    }
}

