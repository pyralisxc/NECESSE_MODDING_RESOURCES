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
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.lootTable.presets.IncursionSummonWeaponsLootTable;
import necesse.level.maps.Level;

public class EmpressCommandToolItem
extends SummonToolItem {
    public EmpressCommandToolItem() {
        super("babyspiderkinarcher", FollowPosition.newPyramid(64, 64), 1.0f, 1900, IncursionSummonWeaponsLootTable.incursionSummonWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(20.0f).setUpgradedValue(1.0f, 25.666674f);
    }

    @Override
    public GameTooltips getSpaceTakenTooltip(InventoryItem item, PlayerMob perspective) {
        return null;
    }

    @Override
    public void runServerSummon(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        Mob last;
        List list = ((Stream)attackerMob.serverFollowersManager.streamFollowers().sequential()).filter(f -> f.summonType.equals(this.summonType)).map(f -> f.mob).filter(m -> m.getStringID().equals("babyspiderkinwarrior") || m.getStringID().equals("babyspiderkinarcher")).collect(Collectors.toList());
        String nextMobStringID = list.isEmpty() ? "babyspiderkinwarrior" : ((last = (Mob)list.get(list.size() - 1)).getStringID().equals("babyspiderkinwarrior") ? "babyspiderkinarcher" : "babyspiderkinwarrior");
        AttackingFollowingMob mob = (AttackingFollowingMob)MobRegistry.getMob(nextMobStringID, level);
        this.summonServerMob(attackerMob, mob, x, y, attackHeight, item);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "empresscommandtip"));
        return tooltips;
    }
}

