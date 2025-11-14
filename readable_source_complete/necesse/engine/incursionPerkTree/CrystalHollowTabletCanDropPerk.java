/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionAndTierRequirement;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.localization.Localization;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.level.maps.incursion.AltarData;

public class CrystalHollowTabletCanDropPerk
extends IncursionPerk {
    public CrystalHollowTabletCanDropPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public String getCustomTooltipLocalization() {
        return Localization.translate("incursion", this.getStringID() + "desc", "tier", (Object)IncursionBiomeRegistry.getBiomeTier(IncursionBiomeRegistry.CRYSTAL_HOLLOW.getID()));
    }

    @Override
    public boolean hasSpecificIncursionAtTierCompleted(AltarData altarData) {
        this.incursionAndTierRequirement = new IncursionAndTierRequirement(IncursionBiomeRegistry.SUN_ARENA, 6);
        return altarData.checkForSpecificIncursionCompletedAtSpecificTierOrAbove(IncursionBiomeRegistry.SUN_ARENA.getID(), 6);
    }

    @Override
    public int getTabletIDForTabletDropPerks() {
        return IncursionBiomeRegistry.CRYSTAL_HOLLOW.getID();
    }

    @Override
    public boolean locksAllOtherPerksOnTier() {
        return true;
    }
}

