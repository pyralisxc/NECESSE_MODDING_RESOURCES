/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionAndTierRequirement;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.localization.Localization;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.level.maps.incursion.AltarData;

public class SunArenaTabletCanDropPerk
extends IncursionPerk {
    public SunArenaTabletCanDropPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public String getCustomTooltipLocalization() {
        return Localization.translate("incursion", this.getStringID() + "desc", "tier", (Object)IncursionBiomeRegistry.getBiomeTier(IncursionBiomeRegistry.SUN_ARENA.getID()));
    }

    @Override
    public int getTabletIDForTabletDropPerks() {
        return IncursionBiomeRegistry.SUN_ARENA.getID();
    }

    @Override
    public boolean hasSpecificIncursionAtTierCompleted(AltarData altarData) {
        this.incursionAndTierRequirement = new IncursionAndTierRequirement(IncursionBiomeRegistry.MOON_ARENA, 5);
        return altarData.checkForSpecificIncursionCompletedAtSpecificTierOrAbove(IncursionBiomeRegistry.MOON_ARENA.getID(), 5);
    }

    @Override
    public boolean locksAllOtherPerksOnTier() {
        return true;
    }
}

