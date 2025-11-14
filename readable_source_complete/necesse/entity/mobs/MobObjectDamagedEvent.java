/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.ObjectDamageResult;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobGenericEvent;
import necesse.level.maps.Level;

public class MobObjectDamagedEvent
extends MobGenericEvent {
    public final Level level;
    public final int totalDamage;
    public final float toolTier;
    public final ObjectDamageResult result;
    public final Attacker attacker;

    public MobObjectDamagedEvent(Level level, int totalDamage, float toolTier, ObjectDamageResult result, Attacker attacker) {
        this.level = level;
        this.totalDamage = totalDamage;
        this.toolTier = toolTier;
        this.result = result;
        this.attacker = attacker;
    }
}

