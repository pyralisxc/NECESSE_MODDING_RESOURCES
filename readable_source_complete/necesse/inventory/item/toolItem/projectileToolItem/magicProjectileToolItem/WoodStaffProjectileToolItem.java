/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class WoodStaffProjectileToolItem
extends MagicProjectileToolItem {
    public WoodStaffProjectileToolItem() {
        super(100, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(800);
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 245.00008f);
        this.velocity.setBaseValue(75);
        this.attackXOffset = 20;
        this.attackYOffset = 20;
        this.attackRange.setBaseValue(500);
        this.knockback.setBaseValue(5);
        this.manaCost.setBaseValue(1.0f).setUpgradedValue(1.0f, 2.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.itemAttackerPredictionDistanceOffset = -20.0f;
        this.canBeUsedForRaids = true;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Projectile projectile = ProjectileRegistry.getProjectile("goldbolt", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        GameRandom random = new GameRandom(seed);
        projectile.resetUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }
}

