/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.ArachnidWebBowAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class ArachnidWebBowToolItem
extends BowProjectileToolItem {
    public ArachnidWebBowToolItem(OneOfLootItems lootTableCategory) {
        super(1900, lootTableCategory);
        this.attackAnimTime.setBaseValue(450);
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(45.0f).setUpgradedValue(1.0f, 58.33335f);
        this.velocity.setBaseValue(300);
        this.attackRange.setBaseValue(800);
        this.attackXOffset = 12;
        this.attackYOffset = 31;
        this.resilienceGain.setBaseValue(0.5f);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate("itemtooltip", "arachnidwebbowtip"));
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new ArachnidWebBowAttackHandler(attackerMob, slot, item, this, seed, x, y));
        return item;
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }
}

