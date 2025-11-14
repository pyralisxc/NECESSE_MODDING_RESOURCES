/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.MobBeforeDamageOverTimeTakenEvent;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class SpawnInvincibilityBuff
extends Buff {
    public SpawnInvincibilityBuff() {
        this.shouldSave = true;
        this.isVisible = false;
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        eventSubscriber.subscribeEvent(MobBeforeDamageOverTimeTakenEvent.class, MobBeforeDamageOverTimeTakenEvent::prevent);
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(buff, event);
        event.prevent();
        event.showDamageTip = false;
        event.playHitSound = false;
    }

    @Override
    public void updateLocalDisplayName() {
        this.displayName = new StaticMessage("NOT OBTAINABLE: Spawn Invincibility");
    }
}

