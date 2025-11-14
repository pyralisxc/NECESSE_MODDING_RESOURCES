/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.IncursionPerkModifiers;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.manager.OnMobAddedListenerEntityComponent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class FlamelingsCanDiePerkLevelEvent
extends LevelEvent
implements OnMobAddedListenerEntityComponent {
    public FlamelingsCanDiePerkLevelEvent() {
        super(true);
        this.shouldSave = true;
    }

    @Override
    public boolean isNetworkImportant() {
        return true;
    }

    @Override
    public void onMobSpawned(Mob mob) {
        mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.PERK_FLAMELINGS_CAN_DIE, mob, 30.0f, null), false);
    }
}

