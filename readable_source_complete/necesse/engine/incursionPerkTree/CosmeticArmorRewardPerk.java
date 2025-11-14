/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.registries.UniqueIncursionRewardsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.level.maps.incursion.IncursionData;

public class CosmeticArmorRewardPerk
extends IncursionPerk {
    public CosmeticArmorRewardPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public TicketSystemList<LootItemInterface> onGenerateTabletRewards(TicketSystemList<LootItemInterface> ticketedRewards, GameRandom seededRandom, int tier, IncursionData incursionData) {
        int tabletTierWeight = tier * 10;
        ticketedRewards.addObject(75 + tabletTierWeight, (Object)UniqueIncursionRewardsRegistry.getRandomCosmeticArmorSetReward());
        return ticketedRewards;
    }
}

