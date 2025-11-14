/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.registries.UniqueIncursionRewardsRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.lootItem.UpgradeTierLootItem;
import necesse.level.maps.incursion.IncursionData;

public class MoreMeleeWeaponsRewardPerk
extends IncursionPerk {
    public MoreMeleeWeaponsRewardPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public TicketSystemList<LootItemInterface> onGenerateTabletRewards(TicketSystemList<LootItemInterface> ticketedRewards, GameRandom seededRandom, int tier, IncursionData incursionData) {
        int tabletTierWeight = 10 * tier;
        LootItemInterface meleeWeaponReward = incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.INCURSION_LEVEL_GEAR_REWARD.getID()) ? UniqueIncursionRewardsRegistry.getRandomTierXBiomeAndIncursionMeleeWeaponReward(seededRandom) : UniqueIncursionRewardsRegistry.getRandomTierXBiomeMeleeWeaponReward(seededRandom);
        ticketedRewards.addObject(75 + tabletTierWeight, (Object)new UpgradeTierLootItem(meleeWeaponReward, Math.min(tier + 1, IncursionData.ITEM_TIER_UPGRADE_CAP)));
        return ticketedRewards;
    }
}

