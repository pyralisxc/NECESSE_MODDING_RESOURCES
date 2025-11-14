/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionAndTierRequirement;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.entity.levelEvent.incursionModifiers.AscendedShardsBossDropLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;

public class AscendedShardsCanDropPerk
extends IncursionPerk {
    public static int FRAGMENTS_DROP_AT_TIER = 8;
    public static float DROP_CHANCE = 1.0f;
    public static float ADDITIONAL_DROP_CHANCE_PER_TIER = 0.5f;

    public AscendedShardsCanDropPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public boolean hasSpecificIncursionAtTierCompleted(AltarData altarData) {
        this.incursionAndTierRequirement = new IncursionAndTierRequirement(8, 5);
        return altarData.checkForAmountOfIncursionsCompletedAtSpecificTierOrAbove(8, 5);
    }

    @Override
    public boolean locksAllOtherPerksOnTier() {
        return true;
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
        AscendedShardsBossDropLevelEvent event = new AscendedShardsBossDropLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt(this.getStringID() + modifierIndex, event.getUniqueID());
    }
}

