/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.DredgingStaffEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.presets.MagicWeaponsLootTable;
import necesse.level.maps.Level;

public class DredgingStaffProjectileToolItem
extends MagicProjectileToolItem {
    public DredgingStaffProjectileToolItem() {
        super(900, MagicWeaponsLootTable.magicWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(600);
        this.attackDamage.setBaseValue(50.0f).setUpgradedValue(1.0f, 105.00003f);
        this.velocity.setBaseValue(150);
        this.attackXOffset = 14;
        this.attackYOffset = 4;
        this.attackRange.setBaseValue(200);
        this.knockback.setBaseValue(50);
        this.manaCost.setBaseValue(2.5f).setUpgradedValue(1.0f, 2.5f);
        this.resilienceGain.setBaseValue(1.0f);
        this.itemAttackerProjectileCanHitWidth = 10.0f;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.pointRotation(attackDirX, attackDirY).forEachItemSprite(i -> i.itemRotateOffset(45.0f));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "dredgingstafftip"));
        return tooltips;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int range = this.getAttackRange(item);
        DredgingStaffEvent event = new DredgingStaffEvent(attackerMob, attackerMob.getX(), attackerMob.getY(), new GameRandom(seed), x, y, this.getAttackDamage(item), this.getResilienceGain(item), this.getProjectileVelocity(item, attackerMob), range, (int)((float)range * 0.7f));
        attackerMob.addAndSendAttackerLevelEvent(event);
        this.consumeMana(attackerMob, item);
        return item;
    }
}

