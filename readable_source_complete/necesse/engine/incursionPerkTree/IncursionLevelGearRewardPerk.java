/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.registries.UniqueIncursionRewardsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.UpgradeTierLootItem;
import necesse.level.maps.incursion.IncursionData;

public class IncursionLevelGearRewardPerk
extends IncursionPerk {
    public IncursionLevelGearRewardPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public TicketSystemList<LootItemInterface> onGenerateTabletRewards(TicketSystemList<LootItemInterface> ticketedRewards, GameRandom seededRandom, int tier, IncursionData incursionData) {
        int tabletTierWeight = 10 * tier;
        ticketedRewards.addObject(70 + tabletTierWeight, (Object)new UpgradeTierLootItem(UniqueIncursionRewardsRegistry.getRandomTierXIncursionWeaponReward(seededRandom), tier));
        ticketedRewards.addObject(40 + tabletTierWeight, (Object)new UpgradeTierLootItem(UniqueIncursionRewardsRegistry.getRandomTierXIncursionArmorSetReward(), tier));
        return ticketedRewards;
    }
}

