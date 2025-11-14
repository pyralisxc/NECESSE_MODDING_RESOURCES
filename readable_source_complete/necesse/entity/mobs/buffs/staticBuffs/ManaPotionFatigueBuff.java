/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ManaPotionFatigueBuff
extends Buff {
    public ManaPotionFatigueBuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        Buff manaExhaustion = BuffRegistry.Debuffs.MANA_EXHAUSTION;
        if (buff.owner.buffManager.hasBuff(manaExhaustion)) {
            buff.owner.isManaExhausted = false;
            buff.owner.buffManager.removeBuff(manaExhaustion, false);
        }
    }
}

