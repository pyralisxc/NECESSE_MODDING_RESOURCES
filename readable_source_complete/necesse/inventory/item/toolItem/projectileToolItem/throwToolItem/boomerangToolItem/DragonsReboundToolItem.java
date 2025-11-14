/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

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
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;
import necesse.level.maps.Level;

public class DragonsReboundToolItem
extends BoomerangToolItem {
    public DragonsReboundToolItem() {
        super(1800, ThrowWeaponsLootTable.throwWeapons, null);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(77.0f).setUpgradedValue(1.0f, 91.00002f);
        this.attackRange.setBaseValue(650);
        this.velocity.setBaseValue(210);
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 14.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return null;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GNDItemMap gndData = item.getGndData();
        boolean sage = gndData.getBoolean("sage");
        Projectile projectile = ProjectileRegistry.getProjectile(sage ? "sageboomerang" : "gritboomerang", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        attackerMob.boomerangs.add(projectile);
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile);
        gndData.setBoolean("sage", !sage);
        return item;
    }
}

