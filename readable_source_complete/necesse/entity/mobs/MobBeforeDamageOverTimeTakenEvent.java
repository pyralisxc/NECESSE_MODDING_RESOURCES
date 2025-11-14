/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.MobGenericEvent;
import necesse.entity.mobs.buffs.ActiveBuff;

public class MobBeforeDamageOverTimeTakenEvent
extends MobGenericEvent {
    public final ActiveBuff buff;
    public int damage;
    public GNDItemMap gndData = new GNDItemMap();
    private boolean prevented;

    public MobBeforeDamageOverTimeTakenEvent(ActiveBuff buff, int damage) {
        this.buff = buff;
        this.damage = damage;
    }

    public void prevent() {
        this.prevented = true;
    }

    public boolean isPrevented() {
        return this.prevented;
    }

    public int getExpectedHealth() {
        if (this.prevented) {
            return this.buff.owner.getHealth();
        }
        return Math.max(this.buff.owner.getHealth() - this.damage, 0);
    }
}

