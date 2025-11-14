/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.ButchersCleaverBoomerangProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;
import necesse.level.maps.Level;

public class ChefsSpecialBoomerangToolItem
extends BoomerangToolItem {
    public ChefsSpecialBoomerangToolItem() {
        super(1600, ThrowWeaponsLootTable.throwWeapons, null);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(350);
        this.attackDamage.setBaseValue(78.0f).setUpgradedValue(1.0f, 92.166695f);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(180);
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 14.0f;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips preEnchantmentTooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        preEnchantmentTooltips.add(Localization.translate("itemtooltip", "chefsspecialtip"), 400);
        return preEnchantmentTooltips;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return null;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GNDItemMap gndData = item.getGndData();
        boolean rollingPin = gndData.getBoolean("rollingpin");
        Projectile projectile = rollingPin ? ProjectileRegistry.getProjectile("chefsspecialrollingpin", level, attackerMob.x, attackerMob.y, (float)x, (float)y, (float)this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), (Mob)attackerMob) : new ButchersCleaverBoomerangProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getResilienceGain(item), this.getKnockback(item, attackerMob), 3, true);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.boomerangs.add(projectile);
        attackerMob.addAndSendAttackerProjectile(projectile);
        gndData.setBoolean("rollingpin", !rollingPin);
        return item;
    }
}

