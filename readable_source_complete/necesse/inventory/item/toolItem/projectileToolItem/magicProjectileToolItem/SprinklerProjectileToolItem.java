/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SprinklerProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class SprinklerProjectileToolItem
extends MagicProjectileToolItem {
    public SprinklerProjectileToolItem() {
        super(300, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(13.0f).setUpgradedValue(1.0f, 63.00002f);
        this.velocity.setBaseValue(100);
        this.attackXOffset = 14;
        this.attackYOffset = 4;
        this.attackRange.setBaseValue(350);
        this.manaCost.setBaseValue(0.75f).setUpgradedValue(1.0f, 1.5f);
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 25.0f;
        this.itemAttackerPredictionDistanceOffset = -20.0f;
        this.canBeUsedForRaids = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "sprinklertip1"));
        tooltips.add(Localization.translate("itemtooltip", "sprinklertip2"));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY).forEachItemSprite(i -> i.itemRotateOffset(45.0f));
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt2, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.4f).pitch(GameRandom.globalRandom.getFloatBetween(0.8f, 0.9f)));
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        SprinklerProjectile projectile = new SprinklerProjectile(level, attackerMob.x, attackerMob.y, x, y, this.getAttackRange(item), this.getAttackDamage(item), attackerMob);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }
}

