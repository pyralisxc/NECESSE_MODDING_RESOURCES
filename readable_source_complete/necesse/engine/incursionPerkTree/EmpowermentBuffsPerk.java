/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.EmpowermentBuffsModifierLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;

public class EmpowermentBuffsPerk
extends IncursionPerk {
    private final int empowermentPickUpsSpawnInterval = 12000;

    public EmpowermentBuffsPerk(Integer tier, int perkCost, int xPositionOnPerkTree, IncursionPerk ... prerequisitePerkRequired) {
        super(tier, perkCost, xPositionOnPerkTree, true, prerequisitePerkRequired);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, AltarData altarData, int modifierIndex) {
        EmpowermentBuffsModifierLevelEvent event = new EmpowermentBuffsModifierLevelEvent(12000L, level.getTime());
        level.entityManager.events.add(event);
        level.gndData.setInt("empowermentBuffs" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, AltarData altarData, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("empowermentBuffs" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

