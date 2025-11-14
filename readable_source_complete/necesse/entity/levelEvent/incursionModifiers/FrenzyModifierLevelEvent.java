/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.incursionModifiers;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.MobHealthChangeListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class FrenzyModifierLevelEvent
extends LevelEvent
implements MobHealthChangeListenerEntityComponent {
    public FrenzyModifierLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public void onLevelMobHealthChanged(Mob mob, int beforeHealth, int health, float knockbackX, float knockbackY, Attacker attacker) {
        if (!mob.isPlayer) {
            float quarterHealthThreshold = (float)mob.getMaxHealth() * 0.4f;
            boolean hasFrenzy = mob.buffManager.hasBuff(BuffRegistry.FRENZY);
            if ((float)health <= quarterHealthThreshold && !hasFrenzy) {
                mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FRENZY, mob, 9999.0f, null), true);
            } else if (hasFrenzy) {
                mob.buffManager.removeBuff(BuffRegistry.FRENZY, true);
            }
        }
    }
}

