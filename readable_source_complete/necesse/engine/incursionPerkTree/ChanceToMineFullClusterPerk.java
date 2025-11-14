/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.entity.levelEvent.incursionModifiers.ChanceToMineFullClusterLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;

public class ChanceToMineFullClusterPerk
extends IncursionPerk {
    public static float CLEAR_CHANCE_ON_ORE_DESTROYED = 0.5f;

    public ChanceToMineFullClusterPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
        ChanceToMineFullClusterLevelEvent event = new ChanceToMineFullClusterLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt(this.getStringID() + modifierIndex, event.getUniqueID());
    }
}

