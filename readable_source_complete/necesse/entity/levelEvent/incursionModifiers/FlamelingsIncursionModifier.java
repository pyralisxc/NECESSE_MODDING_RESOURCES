/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.FlamelingsModifierLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class FlamelingsIncursionModifier
extends UniqueIncursionModifier {
    private final int flamelingSpawnInterval = 10000;
    private final int flamelingUptime = 4000;

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, int modifierIndex) {
        FlamelingsModifierLevelEvent event = new FlamelingsModifierLevelEvent(10000L, level.getTime(), 4000L);
        level.entityManager.events.add(event);
        level.gndData.setInt("flamelings" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("flamelings" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

