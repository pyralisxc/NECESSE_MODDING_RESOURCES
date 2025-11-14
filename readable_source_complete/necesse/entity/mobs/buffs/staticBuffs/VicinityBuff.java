/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public abstract class VicinityBuff
extends Buff {
    @Override
    public boolean canCancel(ActiveBuff buff) {
        return false;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }

    @Override
    public boolean shouldNetworkSync() {
        return false;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }
}

