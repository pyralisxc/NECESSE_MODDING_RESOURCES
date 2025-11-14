/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;

public class MobWasKilledEvent {
    public final Mob target;
    public final Attacker attacker;

    public MobWasKilledEvent(Mob target, Attacker attacker) {
        this.target = target;
        this.attacker = attacker;
    }
}

