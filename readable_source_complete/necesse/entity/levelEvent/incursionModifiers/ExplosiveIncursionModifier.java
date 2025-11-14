/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.ExplosiveModifierLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.BiomeTrialIncursionData;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class ExplosiveIncursionModifier
extends UniqueIncursionModifier {
    @Override
    public int getModifierTickets(IncursionData data) {
        if (data instanceof BiomeTrialIncursionData) {
            return 0;
        }
        return super.getModifierTickets(data);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, int modifierIndex) {
        ExplosiveModifierLevelEvent event = new ExplosiveModifierLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt("explosive" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("explosive" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

