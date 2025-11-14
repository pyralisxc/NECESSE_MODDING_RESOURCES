/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class KatanaDashStacksBuff
extends Buff {
    public KatanaDashStacksBuff() {
        this.isImportant = true;
        this.canCancel = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 100;
    }

    @Override
    public boolean overridesStackDuration() {
        return true;
    }

    @Override
    public boolean showsFirstStackDurationText() {
        return super.showsFirstStackDurationText();
    }
}

