/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.ButchersCleaverBoomerangProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;
import necesse.level.maps.Level;

public class ButchersCleaverBoomerangToolItem
extends BoomerangToolItem {
    public ButchersCleaverBoomerangToolItem() {
        super(1600, ThrowWeaponsLootTable.throwWeapons, null);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(380);
        this.attackDamage.setBaseValue(64.0f).setUpgradedValue(1.0f, 75.83335f);
        this.attackRange.setBaseValue(700);
        this.velocity.setBaseValue(200);
        this.resilienceGain.setBaseValue(0.2f);
        this.itemAttackerProjectileCanHitWidth = 14.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips preEnchantmentTooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        preEnchantmentTooltips.add(Localization.translate("itemtooltip", "butcherscleavertip"), 400);
        return preEnchantmentTooltips;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return null;
    }

    @Override
    protected SoundSettings getSwingSound() {
        return new SoundSettings(GameResources.swing1);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        ButchersCleaverBoomerangProjectile projectile = new ButchersCleaverBoomerangProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, this.getThrowingVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getResilienceGain(item), this.getKnockback(item, attackerMob), 2, false);
        attackerMob.boomerangs.add(projectile);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile);
        return item;
    }
}

