/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.SummonedCountBuff;

public class SummonedMountBuff
extends SummonedCountBuff {
    @Override
    public boolean canCancel(ActiveBuff buff) {
        GameMessage dismountError;
        Mob mount;
        boolean canCancel = super.canCancel(buff);
        if (canCancel && (mount = buff.owner.getMount()) != null && mount.getFollowingMob() == buff.owner && (dismountError = mount.getMountDismountError(buff.owner, null)) != null) {
            return false;
        }
        return canCancel;
    }
}

