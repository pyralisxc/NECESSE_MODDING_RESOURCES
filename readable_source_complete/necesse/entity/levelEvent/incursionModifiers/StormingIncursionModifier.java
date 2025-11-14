/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.StormingModifierLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class StormingIncursionModifier
extends UniqueIncursionModifier {
    private final int stormingInterval = 8000;
    private final int chargeUpTime = 2000;

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, int modifierIndex) {
        StormingModifierLevelEvent event = new StormingModifierLevelEvent(8000L, level.getTime(), 2000L);
        level.entityManager.events.add(event);
        level.gndData.setInt("storming" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("storming" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

