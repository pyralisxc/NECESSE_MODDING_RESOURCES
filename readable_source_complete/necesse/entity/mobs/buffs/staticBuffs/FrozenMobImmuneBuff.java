/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.mobAbilityLevelEvent.FrozenEnemyHitLevelEvent;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.ai.behaviourTree.event.AIEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.FrozenMobBuff;

public class FrozenMobImmuneBuff
extends FrozenMobBuff {
    @Override
    protected void onThawFinished(ActiveBuff activeBuff) {
        super.onThawFinished(activeBuff);
        activeBuff.owner.startAttackCooldown();
        activeBuff.owner.ai.blackboard.submitEvent("resetTarget", new AIEvent());
        activeBuff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.THAWED_ENEMY, activeBuff.owner, 600.0f, null), true);
    }

    @Override
    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
        super.onBeforeHitCalculated(buff, event);
        if (!event.isPrevented()) {
            event.prevent();
            event.playHitSound = false;
            event.showDamageTip = false;
        }
        if (buff.owner.isServer()) {
            boolean thawedSelf = this.findAndThawAll(buff.owner);
            if (!thawedSelf) {
                this.startThawMob(buff.owner, 0, THAW_TIME_MIN);
            }
            buff.owner.getLevel().entityManager.addLevelEvent(new FrozenEnemyHitLevelEvent(buff.owner));
        }
    }
}

