/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionAndTierRequirement;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.localization.Localization;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.level.maps.incursion.AltarData;

public class SpiderCastleTabletCanDropPerk
extends IncursionPerk {
    public SpiderCastleTabletCanDropPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public String getCustomTooltipLocalization() {
        return Localization.translate("incursion", this.getStringID() + "desc", "tier", (Object)IncursionBiomeRegistry.getBiomeTier(IncursionBiomeRegistry.SPIDER_CASTLE.getID()));
    }

    @Override
    public boolean hasSpecificIncursionAtTierCompleted(AltarData altarData) {
        this.incursionAndTierRequirement = new IncursionAndTierRequirement(IncursionBiomeRegistry.GRAVEYARD, 3);
        return altarData.checkForSpecificIncursionCompletedAtSpecificTierOrAbove(IncursionBiomeRegistry.GRAVEYARD.getID(), 3);
    }

    @Override
    public int getTabletIDForTabletDropPerks() {
        return IncursionBiomeRegistry.SPIDER_CASTLE.getID();
    }

    @Override
    public boolean locksAllOtherPerksOnTier() {
        return true;
    }
}

