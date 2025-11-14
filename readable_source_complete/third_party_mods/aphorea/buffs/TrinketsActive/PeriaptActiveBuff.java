/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BuffRegistry
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 */
package aphorea.buffs.TrinketsActive;

import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class PeriaptActiveBuff
extends Buff {
    public PeriaptActiveBuff() {
        this.isImportant = true;
        this.canCancel = false;
        this.isVisible = true;
        this.shouldSave = true;
    }

    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    public void onRemoved(ActiveBuff buff) {
        if (buff.owner.isPlayer) {
            PlayerMob player = (PlayerMob)buff.owner;
            player.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)"periaptcooldown"), (Mob)player, 20.0f, null), false);
        }
    }
}

