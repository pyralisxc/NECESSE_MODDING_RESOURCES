/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.CrawlmageddonModifierLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.BiomeTrialIncursionData;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class CrawlmageddonIncursionModifier
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
        CrawlmageddonModifierLevelEvent event = new CrawlmageddonModifierLevelEvent();
        level.entityManager.events.add(event);
        level.gndData.setInt("crawlmageddon" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("crawlmageddon" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

