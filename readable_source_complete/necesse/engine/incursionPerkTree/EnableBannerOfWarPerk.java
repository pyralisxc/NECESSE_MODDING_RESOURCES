/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.EnableBannerOfWarLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;

public class EnableBannerOfWarPerk
extends IncursionPerk {
    public EnableBannerOfWarPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
        EnableBannerOfWarLevelEvent event = new EnableBannerOfWarLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt("enableBannerOfWarLevelEvent" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, AltarData altarData, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("enableBannerOfWarLevelEvent" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

