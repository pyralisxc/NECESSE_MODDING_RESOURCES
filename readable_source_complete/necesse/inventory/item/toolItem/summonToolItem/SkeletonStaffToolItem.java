/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;
import necesse.level.maps.Level;

public class SkeletonStaffToolItem
extends SummonToolItem {
    public SkeletonStaffToolItem() {
        super("babyskeleton", FollowPosition.PYRAMID, 0.5f, 1800, SummonWeaponsLootTable.summonWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(19.0f).setUpgradedValue(1.0f, 23.33334f);
    }

    @Override
    public GameTooltips getSpaceTakenTooltip(InventoryItem item, PlayerMob perspective) {
        return null;
    }

    @Override
    public void runServerSummon(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        AttackingFollowingMob mob1 = (AttackingFollowingMob)MobRegistry.getMob("babyskeleton", level);
        this.summonServerMob(attackerMob, mob1, x, y, attackHeight, item);
        AttackingFollowingMob mob2 = (AttackingFollowingMob)MobRegistry.getMob("babyskeletonmage", level);
        this.summonServerMob(attackerMob, mob2, x, y, attackHeight, item);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "skeletonstafftip"));
        return tooltips;
    }
}

