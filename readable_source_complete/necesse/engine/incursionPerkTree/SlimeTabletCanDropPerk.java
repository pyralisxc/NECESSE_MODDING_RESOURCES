/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.localization.Localization;
import necesse.engine.registries.IncursionBiomeRegistry;

public class SlimeTabletCanDropPerk
extends IncursionPerk {
    public SlimeTabletCanDropPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public String getCustomTooltipLocalization() {
        return Localization.translate("incursion", this.getStringID() + "desc", "tier", (Object)IncursionBiomeRegistry.getBiomeTier(IncursionBiomeRegistry.SLIME_CAVE.getID()));
    }

    @Override
    public int getTabletIDForTabletDropPerks() {
        return IncursionBiomeRegistry.SLIME_CAVE.getID();
    }

    @Override
    public boolean locksAllOtherPerksOnTier() {
        return true;
    }
}

