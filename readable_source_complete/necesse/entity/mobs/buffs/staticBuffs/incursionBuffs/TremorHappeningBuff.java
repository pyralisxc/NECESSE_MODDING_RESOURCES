/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.incursionBuffs;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.TremorsModifierLevelEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class TremorHappeningBuff
extends Buff {
    public TremorHappeningBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public String getDurationText(ActiveBuff buff) {
        LevelEvent event;
        int eventUniqueID = buff.getGndData().getInt("levelEventUniqueID");
        if (buff.owner.getLevel() != null && (event = buff.owner.getLevel().entityManager.events.get(eventUniqueID, false)) instanceof TremorsModifierLevelEvent) {
            int timeToEnd = ((TremorsModifierLevelEvent)event).getTimeToTremorEnd();
            return ActiveBuff.convertSecondsToText((float)timeToEnd / 1000.0f);
        }
        return super.getDurationText(buff);
    }
}

