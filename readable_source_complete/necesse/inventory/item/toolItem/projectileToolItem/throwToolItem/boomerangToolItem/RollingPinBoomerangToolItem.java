/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.throwToolItem.boomerangToolItem.BoomerangToolItem;
import necesse.inventory.lootTable.presets.ThrowWeaponsLootTable;
import necesse.level.maps.Level;

public class RollingPinBoomerangToolItem
extends BoomerangToolItem {
    public RollingPinBoomerangToolItem() {
        super(400, ThrowWeaponsLootTable.throwWeapons, "rollingpin");
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(250);
        this.attackDamage.setBaseValue(19.0f).setUpgradedValue(1.0f, 84.00002f);
        this.attackRange.setBaseValue(300);
        this.velocity.setBaseValue(130);
        this.resilienceGain.setBaseValue(0.5f);
        this.itemAttackerProjectileCanHitWidth = 14.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips preEnchantmentTooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        preEnchantmentTooltips.add(Localization.translate("itemtooltip", "rollingpintip"), 400);
        return preEnchantmentTooltips;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        if (attackerMob.boomerangs.size() >= 2) {
            return "";
        }
        return null;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
    }
}

