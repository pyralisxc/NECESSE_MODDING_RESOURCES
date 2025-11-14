/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;
import necesse.level.maps.Level;

public class BrainOnAStickToolItem
extends SummonToolItem {
    public BrainOnAStickToolItem() {
        super("babyzombie", FollowPosition.PYRAMID, 1.0f, 300, SummonWeaponsLootTable.summonWeapons);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackDamage.setBaseValue(10.0f).setUpgradedValue(1.0f, 35.000008f);
    }

    @Override
    public void runServerSummon(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Mob last;
        List list = ((Stream)attackerMob.serverFollowersManager.streamFollowers().sequential()).filter(f -> f.summonType.equals(this.summonType)).map(f -> f.mob).filter(m -> m.getStringID().equals("babyzombie") || m.getStringID().equals("babyzombiearcher")).collect(Collectors.toList());
        String nextMobStringID = list.isEmpty() ? "babyzombie" : ((last = (Mob)list.get(list.size() - 1)).getStringID().equals("babyzombie") ? "babyzombiearcher" : "babyzombie");
        AttackingFollowingMob mob = (AttackingFollowingMob)MobRegistry.getMob(nextMobStringID, level);
        this.summonServerMob(attackerMob, mob, x, y, attackHeight, item);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "brainonasticktip"));
        return tooltips;
    }
}

