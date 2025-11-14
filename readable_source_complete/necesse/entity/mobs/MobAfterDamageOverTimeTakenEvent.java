/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.entity.mobs.MobBeforeDamageOverTimeTakenEvent;
import necesse.entity.mobs.MobGenericEvent;
import necesse.entity.mobs.buffs.ActiveBuff;

public class MobAfterDamageOverTimeTakenEvent
extends MobGenericEvent {
    public final ActiveBuff buff;
    public final int beforeHealth;
    public final int damage;
    public GNDItemMap gndData;
    public final boolean prevented;

    public MobAfterDamageOverTimeTakenEvent(MobBeforeDamageOverTimeTakenEvent beforeEvent, int beforeHealth) {
        this.buff = beforeEvent.buff;
        this.beforeHealth = beforeHealth;
        this.damage = beforeEvent.damage;
        this.gndData = beforeEvent.gndData;
        this.prevented = beforeEvent.isPrevented();
    }
}

