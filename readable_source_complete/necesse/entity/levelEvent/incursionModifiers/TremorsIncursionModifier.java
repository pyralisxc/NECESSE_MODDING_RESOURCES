/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.localization.message.LocalMessage;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.TremorsModifierLevelEvent;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class TremorsIncursionModifier
extends UniqueIncursionModifier {
    private final int tremorInterval = 25000;
    private final int tremorDuration = 5000;

    @Override
    public LocalMessage getModifierDescription() {
        return new LocalMessage("ui", "incursionmodifier" + this.getStringID() + "info", "tremorinterval", 25, "tremorduration", 5);
    }

    @Override
    public void onIncursionLevelGenerated(IncursionLevel level, int modifierIndex) {
        TremorsModifierLevelEvent event = new TremorsModifierLevelEvent(level.getTime(), 25000L, 5000L);
        level.entityManager.events.add(event);
        level.gndData.setInt("tremors" + modifierIndex, event.getUniqueID());
    }

    @Override
    public void onIncursionLevelCompleted(IncursionLevel level, int modifierIndex) {
        int eventUniqueID = level.gndData.getInt("tremors" + modifierIndex);
        LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
        if (event != null) {
            event.over();
        }
    }
}

