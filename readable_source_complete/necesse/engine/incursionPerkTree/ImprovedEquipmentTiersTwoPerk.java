/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;

public class ImprovedEquipmentTiersTwoPerk
extends IncursionPerk {
    public ImprovedEquipmentTiersTwoPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, false, prerequisitePerkRequired);
    }

    @Override
    public int setMaxEquipmentRewardTier() {
        return 8;
    }
}

