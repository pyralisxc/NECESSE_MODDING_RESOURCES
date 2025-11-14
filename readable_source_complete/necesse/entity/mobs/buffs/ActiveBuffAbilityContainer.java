/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import necesse.engine.network.Packet;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;

public class ActiveBuffAbilityContainer {
    public final int uniqueID;
    public final boolean isRunningClient;
    public final ActiveBuffAbility buffAbility;
    public final ActiveBuff activeBuff;

    public ActiveBuffAbilityContainer(int uniqueID, boolean isRunningClient, ActiveBuffAbility buffAbility, ActiveBuff activeBuff) {
        this.uniqueID = uniqueID;
        this.isRunningClient = isRunningClient;
        this.buffAbility = buffAbility;
        this.activeBuff = activeBuff;
    }

    public boolean tick(PlayerMob player) {
        if (this.activeBuff.isRemoved()) {
            return false;
        }
        return this.buffAbility.tickActiveAbility(player, this.activeBuff, this.isRunningClient);
    }

    public void update(PlayerMob player, Packet content) {
        this.buffAbility.onActiveAbilityUpdate(player, this.activeBuff, content);
    }

    public void onStopped(PlayerMob player) {
        this.buffAbility.onActiveAbilityStopped(player, this.activeBuff);
    }
}

