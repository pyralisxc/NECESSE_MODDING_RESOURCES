/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.boomerangProjectile.BoxingGloveBoomerangProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.meleeProjectileToolItem.MeleeProjectileToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.Level;

public class BoxingGloveGunToolItem
extends MeleeProjectileToolItem {
    public BoxingGloveGunToolItem() {
        super(350, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(18.0f).setUpgradedValue(1.0f, 87.50002f);
        this.velocity.setBaseValue(200);
        this.knockback.setBaseValue(150);
        this.attackXOffset = 8;
        this.attackYOffset = 6;
        this.attackRange.setBaseValue(200);
        this.itemAttackerProjectileCanHitWidth = 8.0f;
        this.itemAttackerPredictionDistanceOffset = -20.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "boxingglovetip"));
        return tooltips;
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.boxingGloveGun).volume(0.6f);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        BoxingGloveBoomerangProjectile projectile = new BoxingGloveBoomerangProjectile(level, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, attackerMob), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 20);
        return item;
    }
}

