/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.AlchemicalInterferenceModifierLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class AlchemicalInterferenceIncursionModifier
extends UniqueIncursionModifier {
    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, int modifierIndex) {
        AlchemicalInterferenceModifierLevelEvent event = new AlchemicalInterferenceModifierLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt("alchemicalinterference" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("alchemicalinterference" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

