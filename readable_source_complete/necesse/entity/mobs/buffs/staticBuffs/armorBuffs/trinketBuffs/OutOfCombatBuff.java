/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.ToggleActiveBuff;

public abstract class OutOfCombatBuff
extends ToggleActiveBuff {
    @Override
    protected boolean isNextActive(ActiveBuff buff) {
        return buff.owner.isInCombat() || buff.owner.getWorldEntity() != null && buff.owner.getLastAttackTime() + 2000L > buff.owner.getWorldEntity().getTime();
    }
}

