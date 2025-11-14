/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.incursionModifiers.SecondLifePerkLevelEvent;
import necesse.entity.mobs.MobBeforeDamageOverTimeTakenEvent;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.level.maps.Level;

public class SecondLifePerkBuff
extends Buff {
    public SecondLifePerkBuff() {
        this.isVisible = true;
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public boolean shouldNetworkSync() {
        return false;
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 1000;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        eventSubscriber.subscribeEvent(MobBeforeDamageOverTimeTakenEvent.class, event -> {
            if (this.runLifeLineLogic(buff, event.getExpectedHealth())) {
                event.prevent();
            }
        });
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(buff, event);
        if (this.runLifeLineLogic(buff, event.getExpectedHealth())) {
            event.prevent();
        }
    }

    protected boolean runLifeLineLogic(ActiveBuff buff, int expectedHealth) {
        Level level = buff.owner.getLevel();
        if (level.isServer() && expectedHealth <= 0) {
            int eventUniqueID = buff.getGndData().getInt("eventUniqueID");
            LevelEvent event = level.entityManager.events.get(eventUniqueID, false);
            boolean success = false;
            if (event instanceof SecondLifePerkLevelEvent) {
                success = ((SecondLifePerkLevelEvent)event).onPlayerUsedLife(buff.owner);
            }
            if (success) {
                buff.owner.setHealth(Math.max(10, buff.owner.getMaxHealth() / 4));
                buff.removeStack(false);
            } else {
                buff.remove();
            }
            return true;
        }
        return false;
    }
}

