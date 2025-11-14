/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;

public abstract class ToggleActiveBuff
extends TrinketBuff {
    @Override
    public void serverTick(ActiveBuff buff) {
        this.updateActive(buff);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        this.updateActive(buff);
    }

    public boolean isActive(ActiveBuff buff) {
        return buff != null && buff.getGndData().getBoolean("active");
    }

    protected final void updateActive(ActiveBuff buff) {
        boolean shouldActive;
        boolean lastActive = buff.getGndData().getBoolean("active");
        boolean bl = shouldActive = !this.isNextActive(buff);
        if (lastActive != shouldActive) {
            buff.resetDefaultModifiers();
            buff.getGndData().setBoolean("active", shouldActive);
            this.updateActive(buff, shouldActive);
            buff.forceManagerUpdate();
        }
    }

    protected abstract void updateActive(ActiveBuff var1, boolean var2);

    protected abstract boolean isNextActive(ActiveBuff var1);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        this.updateActive(buff);
    }
}

