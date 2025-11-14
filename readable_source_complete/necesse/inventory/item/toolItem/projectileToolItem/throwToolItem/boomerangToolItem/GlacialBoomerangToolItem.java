/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseProjectileAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.projectile.followingProjectile.GlacialBoomerangProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;
import necesse.level.maps.Level;

public class GlacialBoomerangToolItem
extends BoomerangToolItem {
    public GlacialBoomerangToolItem() {
        super(1450, ThrowWeaponsLootTable.throwWeapons, null);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(300);
        this.attackCooldownTime.setBaseValue(400);
        this.attackDamage.setBaseValue(38.0f).setUpgradedValue(1.0f, 52.500015f);
        this.attackRange.setBaseValue(2000);
        this.velocity.setBaseValue(180);
        this.stackSize = 6;
        this.knockback.setBaseValue(100);
        this.resilienceGain.setBaseValue(0.3f);
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "glacialboomerangtip"));
        tooltips.add(Localization.translate("itemtooltip", "glacialboomerangtip2"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int boomerangs = GameMath.limit(Math.min(item.getAmount(), item.itemStackSize() - attackerMob.getBoomerangsUsage()), 1, 2);
        GameRandom random = new GameRandom(seed);
        FollowingProjectile[] projectiles = new FollowingProjectile[boomerangs];
        float angle = GameMath.getAngle(new Point2D.Float((float)x - attackerMob.x, (float)y - attackerMob.y)) + 90.0f;
        float anglePerProjectile = 60.0f;
        float angleOffset = (float)(-boomerangs) * anglePerProjectile / 2.0f + anglePerProjectile / 2.0f;
        for (int i = 0; i < projectiles.length; ++i) {
            GlacialBoomerangProjectile projectile = new GlacialBoomerangProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob));
            projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
            projectile.setAngle(angle + angleOffset + (float)i * anglePerProjectile);
            attackerMob.boomerangs.add(projectile);
            projectile.resetUniqueID(random);
            projectiles[i] = projectile;
        }
        if (attackerMob.isAttackHandlerFrom(item, slot)) {
            ((MouseProjectileAttackHandler)attackerMob.getAttackHandler()).addProjectiles(projectiles);
        } else {
            attackerMob.startAttackHandler(new MouseProjectileAttackHandler(attackerMob, slot, this.getAttackRange(item), 100, projectiles));
        }
        for (FollowingProjectile projectile : projectiles) {
            attackerMob.addAndSendAttackerProjectile(projectile);
        }
        return item;
    }
}

