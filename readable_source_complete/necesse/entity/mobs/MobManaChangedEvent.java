/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.MobGenericEvent;

public class MobManaChangedEvent
extends MobGenericEvent {
    public final float lastMana;
    public final float currentMana;
    public final boolean fromUpdatePacket;

    public MobManaChangedEvent(float lastMana, float currentMana, boolean fromUpdatePacket) {
        this.lastMana = lastMana;
        this.currentMana = currentMana;
        this.fromUpdatePacket = fromUpdatePacket;
    }
}

